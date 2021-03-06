@file:Suppress("UNCHECKED_CAST")

package kaist.iclab.abclogger

import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Message
import io.grpc.*
import io.kotest.core.test.TestContext
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kaist.iclab.abclogger.grpc.proto.*
import kaist.iclab.abclogger.grpc.service.ServiceProtos
import kaist.iclab.abclogger.schema.dataCaseToDataType
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

fun app(): App {
    val app = App()

    app.start(
        portNumber = 50051,
        dbServerName = "localhost",
        dbName = "data",
        dbRootPassword = "admin",
        dbRootUserName = "admin",
        dbWriterUserName = "abcwriter",
        dbWriterUserPassword = "abcwriter",
        dbReadUsers = mapOf(
            "abcreader" to "abcreader",
            "abcreader2" to "abcreader2"
        ),
        adminEmail = "",
        adminPassword = "",
        recipients = emptyList(),
        logPath = "./test/log.log",
        rootTokens = emptyList(),
        readOnlyTokens = listOf("read-only-token")
    )

    return app
}

fun channel()  = ManagedChannelBuilder.forAddress("localhost", 50051)
    .directExecutor()
    .usePlaintext()
    .intercept(clientInterceptor("auth_token", "read-only-token"))
    .build()



fun datum(
    datumType: DatumProtos.DatumType,
    timestamp: Long,
    subject: SubjectProtos.Subject,
) = DatumProtos.Datum.newBuilder().apply {
    this.timestamp = timestamp
    this.subject = subject

    when (datumType) {
        DatumProtos.DatumType.PHYSICAL_ACTIVITY_TRANSITION ->
            this.physicalActivityTransition = DatumProtos.PhysicalActivityTransition.newBuilder().fill()
        DatumProtos.DatumType.PHYSICAL_ACTIVITY ->
            this.physicalActivity = DatumProtos.PhysicalActivity.newBuilder().fill()
        DatumProtos.DatumType.APP_USAGE_EVENT ->
            this.appUsageEvent = DatumProtos.AppUsageEvent.newBuilder().fill()
        DatumProtos.DatumType.BATTERY ->
            this.battery = DatumProtos.Battery.newBuilder().fill()
        DatumProtos.DatumType.BLUETOOTH ->
            this.bluetooth = DatumProtos.Bluetooth.newBuilder().fill()
        DatumProtos.DatumType.CALL_LOG ->
            this.callLog = DatumProtos.CallLog.newBuilder().fill()
        DatumProtos.DatumType.DEVICE_EVENT ->
            this.deviceEvent = DatumProtos.DeviceEvent.newBuilder().fill()
        DatumProtos.DatumType.EMBEDDED_SENSOR ->
            this.embeddedSensor = DatumProtos.EmbeddedSensor.newBuilder().fill()
        DatumProtos.DatumType.EXTERNAL_SENSOR ->
            this.externalSensor = DatumProtos.ExternalSensor.newBuilder().fill()
        DatumProtos.DatumType.INSTALLED_APP ->
            this.installedApp = DatumProtos.InstalledApp.newBuilder().fill()
        DatumProtos.DatumType.KEY_LOG ->
            this.keyLog = DatumProtos.KeyLog.newBuilder().fill()
        DatumProtos.DatumType.LOCATION ->
            this.location = DatumProtos.Location.newBuilder().fill()
        DatumProtos.DatumType.MEDIA ->
            this.media = DatumProtos.Media.newBuilder().fill()
        DatumProtos.DatumType.MESSAGE ->
            this.message = DatumProtos.Message.newBuilder().fill()
        DatumProtos.DatumType.NOTIFICATION ->
            this.notification = DatumProtos.Notification.newBuilder().fill()
        DatumProtos.DatumType.FITNESS ->
            this.fitness = DatumProtos.Fitness.newBuilder().fill()
        DatumProtos.DatumType.SURVEY ->
            this.survey = DatumProtos.Survey.newBuilder().fill()
        DatumProtos.DatumType.DATA_TRAFFIC ->
            this.dataTraffic = DatumProtos.DataTraffic.newBuilder().fill()
        DatumProtos.DatumType.WIFI ->
            this.wifi = DatumProtos.Wifi.newBuilder().fill()
        else -> {
        }
    }
}.build()


fun heartBeat(
    timestamp: Long,
    subject: SubjectProtos.Subject,
    dataTypes: Collection<DatumProtos.DatumType>
) = HeartBeatProtos.HeartBeat.newBuilder().apply {
    this.timestamp = timestamp
    this.subject = subject
    this.addAllDataStatus(dataTypes.map { type ->
        HeartBeatProtos.DataStatus.newBuilder().fill {
            datumType = type
        }
    })
}.build()

private fun <T : Message, B : GeneratedMessageV3.Builder<B>> B.fill(block: (B.() -> Unit)? = null): T {
    descriptorForType.fields.forEach { fieldDescriptor ->
        val value: Any? = when (fieldDescriptor.javaType) {
            Descriptors.FieldDescriptor.JavaType.STRING -> "STRING"
            Descriptors.FieldDescriptor.JavaType.BYTE_STRING -> ByteString.copyFromUtf8("STRING")
            Descriptors.FieldDescriptor.JavaType.BOOLEAN -> true
            Descriptors.FieldDescriptor.JavaType.FLOAT -> 100.0F
            Descriptors.FieldDescriptor.JavaType.INT -> 100
            Descriptors.FieldDescriptor.JavaType.LONG -> 100L
            else -> null
        }

        if (value != null) {
            if (fieldDescriptor.isRepeated) {
                (0 until 10).forEach { addRepeatedField(fieldDescriptor, "$it-th $value") }
            } else {
                setField(fieldDescriptor, value)
            }
        }
    }
    block?.invoke(this)
    return build() as T
}

fun queryRead(
    dataTypes: Set<DatumProtos.DatumType>,
    subjects: Set<SubjectProtos.Subject>,
    fromTimestamp: Long,
    toTimestamp: Long
) = ServiceProtos.Query.Read.newBuilder().apply {
    addAllDatumType(dataTypes)
    addAllGroupName(subjects.map { it.groupName })
    addAllEmail(subjects.map { it.email })
    addAllInstanceId(subjects.map { it.instanceId })
    addAllSource(subjects.map { it.source })
    addAllDeviceManufacturer(subjects.map { it.deviceManufacturer })
    addAllDeviceModel(subjects.map { it.deviceModel })
    addAllDeviceVersion(subjects.map { it.deviceVersion })
    addAllDeviceOs(subjects.map { it.deviceOs })
    addAllAppId(subjects.map { it.appId })
    addAllAppVersion(subjects.map { it.appVersion })
    this.fromTimestamp = fromTimestamp
    this.toTimestamp = toTimestamp
}.build()

fun queryAggregate(
    dataTypes: Set<DatumProtos.DatumType>,
    subjects: Set<SubjectProtos.Subject>,
    fromTimestamp: Long,
    toTimestamp: Long
) = ServiceProtos.Query.Aggregate.newBuilder().apply {
    addAllDatumType(dataTypes)
    addAllGroupName(subjects.map { it.groupName })
    addAllEmail(subjects.map { it.email })
    addAllInstanceId(subjects.map { it.instanceId })
    addAllSource(subjects.map { it.source })
    addAllDeviceManufacturer(subjects.map { it.deviceManufacturer })
    addAllDeviceModel(subjects.map { it.deviceModel })
    addAllDeviceVersion(subjects.map { it.deviceVersion })
    addAllDeviceOs(subjects.map { it.deviceOs })
    addAllAppId(subjects.map { it.appId })
    addAllAppVersion(subjects.map { it.appVersion })
    this.fromTimestamp = fromTimestamp
    this.toTimestamp = toTimestamp
}.build()

infix fun <T, C : Collection<T>> C.shouldContainAllExactly(
    expected: C
) = this should containAllExactly(expected)

fun <T> containAllExactly(expected: Collection<T>) = object : Matcher<Collection<T>> {
    override fun test(value: Collection<T>): MatcherResult =
        MatcherResult.invoke(
            expected.size == value.size && expected.containsAll(value) && value.containsAll(expected),
            {
                "Not same"
            },
            {
                "Not same"
            }
        )

}

fun subjectsMatcher(
    subjects: List<SubjectProtos.Subject>
) = object : Matcher<List<SubjectProtos.Subject>> {
    override fun test(value: List<SubjectProtos.Subject>): MatcherResult {
        return MatcherResult.invoke(
            value.all { it in subjects },
            "One of values are not contained in $subjects",
            "One of values are contained in $subjects",
        )
    }
}

suspend fun TestContext.testCreateAndReadDatum(
    delay: Long = TimeUnit.MINUTES.toMillis(1),
    create: suspend () -> Collection<DatumProtos.Datum>,
    read: suspend () -> Collection<DatumProtos.Datum>,
    subject: suspend () -> Collection<SubjectProtos.Subject>,
    aggregate: suspend () -> AggregationProtos.Aggregation
) {
    val originalData = create.invoke()
    delay(delay)

    val realData = read.invoke()
    val aggregation = aggregate.invoke()
    val subjects = subject.invoke()

    realData shouldContainAllExactly originalData
    subjects shouldContainAllExactly originalData.map { it.subject }.toSet()
    originalData shouldHaveSize aggregation.groupList.sumOf { it.value }.toInt()

    val groupByCount = realData.fold(mutableMapOf<Pair<DatumProtos.DatumType, SubjectProtos.Subject>, Double>()) { acc, datum ->
        val key = dataCaseToDataType(datum.dataCase) to datum.subject
        val value = acc[key] ?: 0.0
        acc[key] = value + 1
        acc
    }

    groupByCount.entries.forEach { (k, v) ->
        aggregation.groupList.firstOrNull {
            it.datumType == k.first && it.subject == k.second
        }?.value shouldBe v
    }
}

suspend fun TestContext.testCreateAndReadHeartBeat(
    delay: Long = TimeUnit.MINUTES.toMillis(1),
    createHeartBeats: suspend () -> Collection<HeartBeatProtos.HeartBeat>,
    readHeartBeats: suspend () -> Collection<HeartBeatProtos.HeartBeat>,
) {
    val originalHeartBeats = createHeartBeats.invoke()

    delay(delay)

    val realHeartBeats = readHeartBeats.invoke()

    originalHeartBeats shouldContainAllExactly realHeartBeats
}



fun clientInterceptor(authKey: String, authToken: String) = object : ClientInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?): ClientCall<ReqT, RespT> {
        val newCall = next!!.newCall(method, callOptions)

        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(newCall) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata?) {
                headers?.put(Metadata.Key.of(authKey, Metadata.ASCII_STRING_MARSHALLER), authToken)
                super.start(responseListener, headers)
            }
        }
    }
}
