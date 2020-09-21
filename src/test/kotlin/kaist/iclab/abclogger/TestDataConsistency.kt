package kaist.iclab.abclogger

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kaist.iclab.abclogger.grpc.DataOperationsGrpcKt
import kaist.iclab.abclogger.grpc.QueryProtos
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.DataProtos
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow


private const val START_TIME = 1000L
private const val END_TIME = 1500L
private val TIME_RANGE = START_TIME until END_TIME
private val DATA_TYPES = CommonProtos.DataType.values().filter {
    it != CommonProtos.DataType.NOT_SPECIFIED &&
        it != CommonProtos.DataType.UNRECOGNIZED
}

class TestDataConsistency : StringSpec() {
    private lateinit var app: App
    private lateinit var channel: ManagedChannel
    private lateinit var stub: DataOperationsGrpcKt.DataOperationsCoroutineStub

    override fun testCaseOrder(): TestCaseOrder? = TestCaseOrder.Sequential

    override fun beforeSpec(spec: Spec) {
        app = App()

        app.start(
            portNumber = 50051,
            dbServerName = "localhost",
            dbPortNumber = 27017,
            dbName = "data",
            dbRootPassword = "admin",
            dbRootUserName = "admin",
            dbWriterUserName = "abcwriter",
            dbWriterUserPassword = "abcwriter",
            adminEmail = "",
            adminPassword = "",
            recipients = emptyList(),
            logPath = "./log.log",
            authTokens = emptyList()
        )

        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .directExecutor()
            .usePlaintext()
            .build()

        stub = DataOperationsGrpcKt.DataOperationsCoroutineStub(channel)
    }

    override fun afterSpec(spec: Spec) {
        channel.shutdown()
        app.stop()
    }

    init {
        "create datum" {
            val data = DATA_TYPES.map { dataType ->
                TIME_RANGE.map { timestamp ->
                    buildDatum(
                        dataType = dataType,
                        timestamp = timestamp,
                        email = "CREATE_DATUM",
                        deviceInfo = "DEVICE_INFO_1",
                        deviceId = "DEVICE_ID_1"
                    )
                }
            }.flatten()

            data.map {
                stub.createDatum(it)
            }.size shouldBe data.size

            delay(10 * 2000)
        }

        "check data length for create datum" {

            val expectedSize = DATA_TYPES.size * (END_TIME - START_TIME)
            val realSize = stub.countData(
                buildQueryForData(email = "CREATE_DATUM")
            )
            expectedSize shouldBe realSize.value
        }

        "create data" {
            val chunks = DATA_TYPES.map { dataType ->
                TIME_RANGE.map { timestamp ->
                    buildDatum(
                        dataType = dataType,
                        timestamp = timestamp,
                        email = "CREATE_DATA",
                        deviceInfo = "DEVICE_INFO_1",
                        deviceId = "DEVICE_ID_2"
                    )
                }
            }.flatten().chunked(200)
            chunks.map {
                stub.createData(
                    QueryProtos.Bulk.Data.newBuilder().addAllData(it).build()
                )
            }.size shouldBe chunks.size

            delay(10 * 2000)
        }

        "check data length for create data" {

            val expectedSize = DATA_TYPES.size * (END_TIME - START_TIME)
            val realSize = stub.countData(
                buildQueryForData(email = "CREATE_DATA")
            )
            expectedSize shouldBe realSize.value
        }

        "create data as stream" {
            val data = DATA_TYPES.map { dataType ->
                TIME_RANGE.map { timestamp ->
                    buildDatum(
                        dataType = dataType,
                        timestamp = timestamp,
                        email = "CREATE_DATA_AS_STREAM",
                        deviceInfo = "DEVICE_INFO_2",
                        deviceId = "DEVICE_ID_3"
                    )
                }
            }.flatten()

            stub.createDataAsStream(data.asFlow()) shouldBe CommonProtos.Empty.getDefaultInstance()

            delay(10 * 2000)
        }

        "check data length for create data as stream" {
            val expectedSize = DATA_TYPES.size * (END_TIME - START_TIME)
            val realSize = stub.countData(
                buildQueryForData(email = "CREATE_DATA_AS_STREAM")
            )
            expectedSize shouldBe realSize.value
        }

        "read empty data" {
            stub.readData(
                buildQueryForData(fromTimestamp = 0, toTimestamp = 100)
            ).dataList.size shouldBe 0
        }

        "read all data" {
            stub.readData(
                buildQueryForData(fromTimestamp = 0, toTimestamp = 0)
            ).dataList.size shouldBe 500
        }

        "read data by timestamps" {
            stub.readData(
                buildQueryForData(fromTimestamp = START_TIME, toTimestamp = START_TIME + 200)
            ).dataList should getDataMatcher(fromTimestamp = START_TIME, toTimestamp = START_TIME + 200)
        }

        "read data by dataType" {
            stub.readData(
                buildQueryForData(dataType = CommonProtos.DataType.SURVEY)
            ).dataList should getDataMatcher(dataType = CommonProtos.DataType.SURVEY)
        }

        "read data by email" {
            stub.readData(
                buildQueryForData(email = "CREATE_DATA_AS_STREAM")
            ).dataList should getDataMatcher(email = "CREATE_DATA_AS_STREAM")
        }

        "read data by device info" {
            stub.readData(
                buildQueryForData(deviceInfo = "DEVICE_INFO_1")
            ).dataList should getDataMatcher(deviceInfo = "DEVICE_INFO_1")
        }

        "read data by device id" {
            stub.readData(
                buildQueryForData(deviceId = "DEVICE_ID_1")
            ).dataList should getDataMatcher(deviceId = "DEVICE_ID_1")
        }

        "read data by timestamp and data type" {
            stub.readData(
                buildQueryForData(
                    fromTimestamp = START_TIME + 100,
                    toTimestamp = START_TIME + 500,
                    dataType = CommonProtos.DataType.APP_USAGE_EVENT
                )
            ).dataList should getDataMatcher(
                fromTimestamp = START_TIME + 100,
                toTimestamp = START_TIME + 500,
                dataType = CommonProtos.DataType.APP_USAGE_EVENT
            )
        }

        "read data by timestamp, data type, and email" {
            stub.readData(
                buildQueryForData(
                    fromTimestamp = START_TIME + 100,
                    toTimestamp = START_TIME + 500,
                    dataType = CommonProtos.DataType.PHYSICAL_STAT,
                    email = "CREATE_DATUM"
                )
            ).dataList should getDataMatcher(
                fromTimestamp = START_TIME + 100,
                toTimestamp = START_TIME + 500,
                dataType = CommonProtos.DataType.PHYSICAL_STAT,
                email = "CREATE_DATUM"
            )
        }

        "read data by timestamp, data type, email, and device info" {
            stub.readData(
                buildQueryForData(
                    fromTimestamp = START_TIME + 100,
                    toTimestamp = START_TIME + 500,
                    dataType = CommonProtos.DataType.PHYSICAL_ACTIVITY,
                    email = "CREATE_DATA_AS_STREAM",
                    deviceInfo = "DEVICE_INFO_2"
                )
            ).dataList should getDataMatcher(
                fromTimestamp = START_TIME + 100,
                toTimestamp = START_TIME + 500,
                dataType = CommonProtos.DataType.PHYSICAL_ACTIVITY,
                email = "CREATE_DATA_AS_STREAM",
                deviceInfo = "DEVICE_INFO_2"
            )
        }

        "read data by timestamp, data type, email, device info, and device id" {
            stub.readData(
                buildQueryForData(
                    fromTimestamp = START_TIME + 100,
                    toTimestamp = START_TIME + 500,
                    dataType = CommonProtos.DataType.PHYSICAL_ACTIVITY_TRANSITION,
                    email = "CREATE_DATA",
                    deviceInfo = "DEVICE_INFO_1",
                    deviceId = "DEVICE_ID_2"
                )
            ).dataList should getDataMatcher(
                fromTimestamp = START_TIME + 100,
                toTimestamp = START_TIME + 500,
                dataType = CommonProtos.DataType.PHYSICAL_ACTIVITY_TRANSITION,
                email = "CREATE_DATA",
                deviceInfo = "DEVICE_INFO_1",
                deviceId = "DEVICE_ID_2"

            )
        }

        "read data by timestamp, and email" {
            stub.readData(
                buildQueryForData(
                    fromTimestamp = START_TIME + 100,
                    toTimestamp = START_TIME + 500,
                    email = "CREATE_DATA_AS_STREAM"
                )
            ).dataList should getDataMatcher(
                fromTimestamp = START_TIME + 100,
                toTimestamp = START_TIME + 500,
                email = "CREATE_DATA_AS_STREAM"
            )
        }

        "read data by timestamp, email, device info" {
            stub.readData(
                buildQueryForData(
                    fromTimestamp = START_TIME + 100,
                    toTimestamp = START_TIME + 500,
                    email = "CREATE_DATA_AS_STREAM",
                    deviceInfo = "DEVICE_INFO_2"
                )
            ).dataList should getDataMatcher(
                fromTimestamp = START_TIME + 100,
                toTimestamp = START_TIME + 500,
                email = "CREATE_DATA_AS_STREAM",
                deviceInfo = "DEVICE_INFO_2"
            )
        }

        "read data by timestamp and device info" {
            stub.readData(
                buildQueryForData(
                    fromTimestamp = START_TIME + 100,
                    toTimestamp = START_TIME + 500,
                    deviceInfo = "DEVICE_INFO_1"
                )
            ).dataList should getDataMatcher(
                fromTimestamp = START_TIME + 100,
                toTimestamp = START_TIME + 500,
                deviceInfo = "DEVICE_INFO_1"
            )
        }

        "read subjects" {
            stub.readSubjects(
                QueryProtos.Query.Subjects.newBuilder().build()
            ).subjectList shouldContainExactlyInAnyOrder listOf(
                SubjectProtos.Subject.newBuilder().apply {
                    email = "CREATE_DATUM"
                    deviceInfo = "DEVICE_INFO_1"
                    deviceId = "DEVICE_ID_1"
                }.build(),
                SubjectProtos.Subject.newBuilder().apply {
                    email = "CREATE_DATA"
                    deviceInfo = "DEVICE_INFO_1"
                    deviceId = "DEVICE_ID_2"
                }.build(),
                SubjectProtos.Subject.newBuilder().apply {
                    email = "CREATE_DATA_AS_STREAM"
                    deviceInfo = "DEVICE_INFO_2"
                    deviceId = "DEVICE_ID_3"
                }.build()
            )
        }
    }

    private fun buildQueryForData(
        dataType: CommonProtos.DataType? = null,
        email: String? = null,
        deviceId: String? = null,
        deviceInfo: String? = null,
        fromTimestamp: Long? = null,
        toTimestamp: Long? = null,
        limit: Int? = null,
        isAscending: Boolean? = null
    ): QueryProtos.Query.Data = QueryProtos.Query.Data.newBuilder().apply {
        this.dataType = dataType ?: CommonProtos.DataType.NOT_SPECIFIED
        this.email = email ?: ""
        this.deviceId = deviceId ?: ""
        this.deviceInfo = deviceInfo ?: ""
        this.fromTimestamp = fromTimestamp ?: 0
        this.toTimestamp = toTimestamp ?: 0
        this.limit = limit ?: 500
        this.isAscending = isAscending ?: false
    }.build()

    private fun buildShouldString(subj: String, verb: String, obj: String): Pair<String, String> =
        "$subj should $verb $obj" to "$subj should not $verb $obj"

    private fun getDataMatcher(
        dataType: CommonProtos.DataType? = null,
        email: String? = null,
        deviceId: String? = null,
        deviceInfo: String? = null,
        fromTimestamp: Long? = null,
        toTimestamp: Long? = null
    ): Matcher<MutableList<DataProtos.Datum>> = object : Matcher<MutableList<DataProtos.Datum>> {
        override fun test(value: MutableList<DataProtos.Datum>): MatcherResult {
            val checkTimestamp = if (fromTimestamp != null && toTimestamp != null) {
                value.find { it.timestamp !in (fromTimestamp until toTimestamp) }?.let {
                    buildShouldString(
                        "Timestamp ${it.timestamp}", "be in", "$fromTimestamp until $toTimestamp"
                    )
                }
            } else null

            val checkEmail = if (!email.isNullOrBlank()) {
                value.find { it.email != email }?.let {
                    buildShouldString(
                        "Email ${it.email}", "be equal to", "$email"
                    )
                }
            } else null

            val checkDataType = if (dataType != null && dataType != CommonProtos.DataType.NOT_SPECIFIED) {
                value.find { it.dataCase.name != dataType.name }?.let {
                    buildShouldString(
                        "Data type ${it.dataCase.name}", "be equal to", dataType.name
                    )
                }
            } else null

            val checkDeviceInfo = if (!deviceInfo.isNullOrBlank()) {
                value.find { it.deviceInfo != deviceInfo }?.let {
                    buildShouldString(
                        "Device info ${it.deviceInfo}", "be equal to", deviceInfo
                    )
                }
            } else null

            val checkDeviceId = if (!deviceId.isNullOrBlank()) {
                value.find { it.deviceId != deviceId }?.let {
                    buildShouldString(
                        "Device info ${it.deviceId}", "be equal to", deviceId
                    )
                }
            } else null

            val results = listOfNotNull(
                checkTimestamp, checkEmail, checkDataType, checkDeviceInfo, checkDeviceId
            )

            return MatcherResult(
                results.isEmpty(),
                results.joinToString("; ") { it.first },
                results.joinToString("; ") { it.second }
            )
        }
    }
}