package kaist.iclab.abclogger

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kaist.iclab.abclogger.grpc.DataOperationsGrpcKt
import kaist.iclab.abclogger.grpc.QueryProtos
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow

private const val START_TIME = 1000L
private const val END_TIME = 10000L
private val TIME_RANGE = START_TIME until END_TIME


class TestHeartBeatsConsistency : StringSpec() {
    private lateinit var app: App
    private lateinit var channel: ManagedChannel
    private lateinit var stub: DataOperationsGrpcKt.DataOperationsCoroutineStub

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
                logPath = "./log.log"
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
        "create heart beat" {
            val data = TIME_RANGE.map { timestamp ->
                buildHeartBeat(
                        timestamp = timestamp,
                        email = "CREATE_HEART_BEAT",
                        deviceInfo = "DEVICE_INFO_1",
                        deviceId = "DEVICE_ID_1"
                )
            }
            data.map { stub.createHeartBeat(it) }.size shouldBe data.size

            delay(10 * 2000)
        }

        "check data length for create heart beat" {
            val expectedSize = END_TIME - START_TIME
            val realSize = stub.countHeartBeat(
                    QueryProtos.Query.HeartBeats.newBuilder().setEmail("CREATE_HEART_BEAT").build()
            ).value
            expectedSize shouldBe realSize
        }

        "check data length for create heart beat by subset of data" {
            val expectedSize = END_TIME - START_TIME
            val realSize = stub.countHeartBeat(
                    QueryProtos.Query.HeartBeats.newBuilder()
                            .setEmail("CREATE_HEART_BEAT")
                            .setDataType(CommonProtos.DataType.MESSAGE)
                            .build()
            ).value
            expectedSize shouldBe realSize
        }

        "create heart beats" {
            val data = TIME_RANGE.map { timestamp ->
                buildHeartBeat(
                        timestamp = timestamp,
                        email = "CREATE_HEART_BEATS",
                        deviceInfo = "DEVICE_INFO_1",
                        deviceId = "DEVICE_ID_1"
                )
            }.chunked(200)

            data.map {
                stub.createHeartBeats(
                        QueryProtos.Bulk.HeartBeats.newBuilder().addAllHeartbeat(it).build()
                )
            }.size shouldBe data.size

            delay(20 * 1000)
        }

        "check data length for create heart beats" {
            val expectedSize = END_TIME - START_TIME
            val realSize = stub.countHeartBeat(
                    QueryProtos.Query.HeartBeats.newBuilder().setEmail("CREATE_HEART_BEATS").build()
            ).value
            realSize shouldBe expectedSize
        }

        "check data length for create heart beats by subset of data" {
            val expectedSize = END_TIME - START_TIME
            val realSize = stub.countHeartBeat(
                    QueryProtos.Query.HeartBeats.newBuilder()
                            .setEmail("CREATE_HEART_BEATS")
                            .setDataType(CommonProtos.DataType.PHYSICAL_ACTIVITY_TRANSITION)
                            .build()
            ).value
            realSize shouldBe expectedSize
        }

        "create heart beats as stream" {
            val data = TIME_RANGE.map { timestamp ->
                buildHeartBeat(
                        timestamp = timestamp,
                        email = "CREATE_HEART_BEATS_AS_STREAM",
                        deviceInfo = "DEVICE_INFO_1",
                        deviceId = "DEVICE_ID_1"
                )
            }.asFlow()

            stub.createHeartBeatsAsStream(data) shouldBe CommonProtos.Empty.getDefaultInstance()

            delay(10 * 2000)
        }

        "check data length for create heart beats as stream" {
            val expectedSize = END_TIME - START_TIME
            val realSize = stub.countHeartBeat(
                    QueryProtos.Query.HeartBeats.newBuilder().setEmail("CREATE_HEART_BEATS_AS_STREAM").build()
            ).value
            realSize shouldBe expectedSize
        }

        "check data length for create heart beats as stream by subset of data" {
            val expectedSize = END_TIME - START_TIME
            val realSize = stub.countHeartBeat(
                    QueryProtos.Query.HeartBeats.newBuilder()
                            .setEmail("CREATE_HEART_BEATS_AS_STREAM")
                            .setDataType(CommonProtos.DataType.PHYSICAL_STAT)
                            .build()
            ).value
            realSize shouldBe expectedSize
        }

        "read heart beat by data type" {
            val heartBeat = stub.readHeartBeats(
                    QueryProtos.Query.HeartBeats.newBuilder()
                            .setDataType(CommonProtos.DataType.MESSAGE)
                            .build()
            ).heartbeatList
            heartBeat.all { it.statusList.size == 1 } shouldBe true
            heartBeat.all { it.statusList.first().dataType == CommonProtos.DataType.MESSAGE } shouldBe true
        }
    }
}