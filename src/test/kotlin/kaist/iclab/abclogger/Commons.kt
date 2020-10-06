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
        dbPortNumber = 27017,
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
        authTokens = emptyList()
    )

    return app
}

fun channel()  = ManagedChannelBuilder.forAddress("localhost", 50051)
    .directExecutor()
    .usePlaintext()
    .build()



fun datum(
    datumType: DatumProtos.Datum.Type,
    timestamp: Long,
    subject: SubjectProtos.Subject,
) = DatumProtos.Datum.newBuilder().apply {
    this.timestamp = timestamp
    this.subject = subject

    when (datumType) {
        DatumProtos.Datum.Type.PHYSICAL_ACTIVITY_TRANSITION ->
            this.physicalActivityTransition = DatumProtos.PhysicalActivityTransition.newBuilder().fill()
        DatumProtos.Datum.Type.PHYSICAL_ACTIVITY ->
            this.physicalActivity = DatumProtos.PhysicalActivity.newBuilder().fill()
        DatumProtos.Datum.Type.APP_USAGE_EVENT ->
            this.appUsageEvent = DatumProtos.AppUsageEvent.newBuilder().fill()
        DatumProtos.Datum.Type.BATTERY ->
            this.battery = DatumProtos.Battery.newBuilder().fill()
        DatumProtos.Datum.Type.BLUETOOTH ->
            this.bluetooth = DatumProtos.Bluetooth.newBuilder().fill()
        DatumProtos.Datum.Type.CALL_LOG ->
            this.callLog = DatumProtos.CallLog.newBuilder().fill()
        DatumProtos.Datum.Type.DEVICE_EVENT ->
            this.deviceEvent = DatumProtos.DeviceEvent.newBuilder().fill()
        DatumProtos.Datum.Type.EMBEDDED_SENSOR ->
            this.embeddedSensor = DatumProtos.EmbeddedSensor.newBuilder().fill()
        DatumProtos.Datum.Type.EXTERNAL_SENSOR ->
            this.externalSensor = DatumProtos.ExternalSensor.newBuilder().fill()
        DatumProtos.Datum.Type.INSTALLED_APP ->
            this.installedApp = DatumProtos.InstalledApp.newBuilder().fill()
        DatumProtos.Datum.Type.KEY_LOG ->
            this.keyLog = DatumProtos.KeyLog.newBuilder().fill()
        DatumProtos.Datum.Type.LOCATION ->
            this.location = DatumProtos.Location.newBuilder().fill()
        DatumProtos.Datum.Type.MEDIA ->
            this.media = DatumProtos.Media.newBuilder().fill()
        DatumProtos.Datum.Type.MESSAGE ->
            this.message = DatumProtos.Message.newBuilder().fill()
        DatumProtos.Datum.Type.NOTIFICATION ->
            this.notification = DatumProtos.Notification.newBuilder().fill()
        DatumProtos.Datum.Type.FITNESS ->
            this.fitness = DatumProtos.Fitness.newBuilder().fill()
        DatumProtos.Datum.Type.SURVEY ->
            this.survey = DatumProtos.Survey.newBuilder().fill()
        DatumProtos.Datum.Type.DATA_TRAFFIC ->
            this.dataTraffic = DatumProtos.DataTraffic.newBuilder().fill()
        DatumProtos.Datum.Type.WIFI ->
            this.wifi = DatumProtos.Wifi.newBuilder().fill()
        else -> {
        }
    }
}.build()


fun heartBeat(
    timestamp: Long,
    subject: SubjectProtos.Subject,
    dataTypes: Collection<DatumProtos.Datum.Type>
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
    dataTypes: Set<DatumProtos.Datum.Type>,
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
    dataTypes: Set<DatumProtos.Datum.Type>,
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
    aggregate: suspend () -> AggregationProtos.Aggregation
) {
    val originalData = create.invoke()
    delay(delay)

    val realData = read.invoke()
    val aggregation = aggregate.invoke()

    realData shouldContainAllExactly originalData
    originalData shouldHaveSize aggregation.groupList.sumOf { it.value }.toInt()

    val groupByCount = realData.fold(mutableMapOf<Pair<DatumProtos.Datum.Type, SubjectProtos.Subject>, Double>()) { acc, datum ->
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
    readSubjects: suspend () -> Collection<SubjectProtos.Subject>,
    aggregateSubjects: suspend () -> AggregationProtos.Aggregation,
) {
    val originalHeartBeats = createHeartBeats.invoke()
    val originalSubjects = originalHeartBeats.fold(
        mutableMapOf<DatumProtos.Datum.Type, Set<SubjectProtos.Subject>>()
    ) { acc, heartBeat ->
        heartBeat.dataStatusList.forEach { collector ->
            val key = collector.datumType
            val value = acc[key] ?: mutableSetOf()
            acc[key] = value + heartBeat.subject
        }
        acc
    }

    delay(delay)

    val realHeartBeats = readHeartBeats.invoke()
    val realSubjects = readSubjects.invoke()

    val subjectAggregation = aggregateSubjects.invoke()

    originalHeartBeats shouldContainAllExactly realHeartBeats

    originalSubjects.values.flatten().toSet() shouldContainAllExactly realSubjects
    originalSubjects.entries.forEach { (k, v) ->
        subjectAggregation.groupList.firstOrNull {
            it.datumType == k
        }?.value shouldBe v.size.toDouble()
    }
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
