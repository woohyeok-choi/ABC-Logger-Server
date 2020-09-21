package kaist.iclab.abclogger

import io.grpc.*
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kaist.iclab.abclogger.grpc.DataOperationsGrpcKt
import kaist.iclab.abclogger.grpc.QueryProtos
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.service.AuthInterceptor
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
            logPath = "./log.log",
            authTokens = listOf("test_key")
        )

        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .directExecutor()
            .intercept(getHeaderClientInterceptor(AuthInterceptor.AUTH_TOKEN, "test_key"))
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

        "read heart beat by data type" {
            val heartBeat = stub.readHeartBeats(
                QueryProtos.Query.HeartBeats.newBuilder()
                    .setDataType(CommonProtos.DataType.MESSAGE)
                    .build()
            ).heartBeatList
            heartBeat.all { it.statusList.size == 1 } shouldBe true
            heartBeat.all { it.statusList.first().dataType == CommonProtos.DataType.MESSAGE } shouldBe true
        }
    }
}