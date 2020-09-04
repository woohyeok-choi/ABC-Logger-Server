package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class HeartBeat(
        val timestamp: Long? = null,
        val utcOffsetSec: Int? = null,
        @ContextualSerialization
        val offsetDateTime: OffsetDateTime? = null,
        val email: String? = null,
        val deviceInfo: String? = null,
        val deviceId: String? = null,
        val uploadTime: Long? = null,
        @ContextualSerialization
        val offsetUploadDateTime: OffsetDateTime? = null,
        val status: List<Status> = emptyList()
) {
    companion object : ProtoSerializer<HeartBeat, HeartBeatProtos.HeartBeat> {
        override fun toProto(o: HeartBeat): HeartBeatProtos.HeartBeat =
                with(o) {
                    HeartBeatProtos.HeartBeat.newBuilder().apply {
                        timestamp = this@with.timestamp ?: Long.MIN_VALUE
                        utcOffsetSec = this@with.utcOffsetSec ?: Int.MIN_VALUE
                        email = this@with.email ?: ""
                        deviceInfo = this@with.deviceInfo ?: ""
                        deviceId = this@with.deviceId ?: ""
                        uploadTime = this@with.uploadTime ?: Long.MIN_VALUE
                        addAllStatus(this@with.status.map { Status.toProto(it) })
                    }.build()
                }

        override fun toObject(p: HeartBeatProtos.HeartBeat): HeartBeat =
                with(p) {
                    HeartBeat(
                            timestamp = timestamp,
                            utcOffsetSec = utcOffsetSec,
                            email = email,
                            deviceInfo = deviceInfo,
                            deviceId = deviceId,
                            uploadTime = uploadTime,
                            status = statusList.map { Status.toObject(it) }
                    )
                }
    }
}

@Serializable
data class Status(
        val dataType: String? = null,
        val lastTimeWritten: Long? = null,
        val nData: Long? = null,
        val hasStarted: Boolean? = null,
        val description: String? = null
) {

    companion object : ProtoSerializer<Status, HeartBeatProtos.Status> {
        override fun toProto(o: Status): HeartBeatProtos.Status =
                with(o) {
                    HeartBeatProtos.Status.newBuilder().apply {
                        dataType = try {
                            CommonProtos.DataType.valueOf(this@with.dataType ?: "")
                        } catch (e: Exception) {
                            CommonProtos.DataType.NOT_SPECIFIED
                        }
                        lastTimeWritten = this@with.lastTimeWritten ?: Long.MIN_VALUE
                        nData = this@with.nData ?: Long.MIN_VALUE
                        hasStarted = this@with.hasStarted ?: false
                        description = this@with.description ?: ""
                    }.build()

                }

        override fun toObject(p: HeartBeatProtos.Status): Status =
                with(p) {
                    Status(
                            dataType = dataType.name,
                            lastTimeWritten = lastTimeWritten,
                            nData = nData,
                            hasStarted = hasStarted,
                            description = description
                    )
                }

    }
}