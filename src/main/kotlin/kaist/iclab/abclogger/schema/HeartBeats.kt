package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class HeartBeat(
    val timestamp: Long? = null,
    val utcOffsetSec: Int? = null,
    @Contextual
    val offsetTimestamp: OffsetDateTime? = null,
    val email: String? = null,
    val deviceInfo: String? = null,
    val deviceId: String? = null,
    val uploadTime: Long? = null,
    @Contextual
    val offsetUploadTime: OffsetDateTime? = null,
    val status: List<Status> = emptyList()
) {
    companion object : ProtoSerializer<HeartBeat, HeartBeatProtos.HeartBeat> {
        override fun toProto(o: HeartBeat): HeartBeatProtos.HeartBeat =
            HeartBeatProtos.HeartBeat.newBuilder().apply {
                timestamp = o.timestamp ?: UNKNOWN_LONG
                utcOffsetSec = o.utcOffsetSec ?: UNKNOWN_INT
                email = o.email ?: UNKNOWN_STRING
                deviceInfo = o.deviceInfo ?: UNKNOWN_STRING
                deviceId = o.deviceId ?: UNKNOWN_STRING
                addAllStatus(o.status.map { Status.toProto(it) })
            }.build()

        override fun toObject(p: HeartBeatProtos.HeartBeat): HeartBeat =
            with(p) {
                HeartBeat(
                    timestamp = timestamp,
                    utcOffsetSec = utcOffsetSec,
                    email = email,
                    deviceInfo = deviceInfo,
                    deviceId = deviceId,
                    status = statusList.map { Status.toObject(it) }
                )
            }
    }
}

@Serializable
data class Status(
    val dataType: String? = null,
    val turnedOnTime: Long? = null,
    val lastTimeWritten: Long? = null,
    val recordsCollected: Long? = null,
    val recordsUploaded: Long? = null,
    val recordsRemained: Long? = null,
    val isEnabled: Boolean? = null,
    val status: Map<String, String>? = null,
    val errorMessage: String? = null
) {
    companion object : ProtoSerializer<Status, HeartBeatProtos.Status> {
        override fun toProto(o: Status): HeartBeatProtos.Status =
            HeartBeatProtos.Status.newBuilder().apply {
                dataType = try {
                    CommonProtos.DataType.valueOf(o.dataType ?: "")
                } catch (e: Exception) {
                    CommonProtos.DataType.NOT_SPECIFIED
                }
                turnedOnTime = o.turnedOnTime ?: UNKNOWN_LONG
                lastTimeWritten = o.lastTimeWritten ?: UNKNOWN_LONG
                recordsCollected = o.recordsCollected ?: UNKNOWN_LONG
                recordsUploaded = o.recordsUploaded ?: UNKNOWN_LONG
                recordsRemained = o.recordsRemained ?: UNKNOWN_LONG
                isEnabled = o.isEnabled ?: UNKNOWN_BOOLEAN
                putAllStatus(o.status ?: mapOf())
                errorMessage = o.errorMessage ?: UNKNOWN_STRING
            }.build()

        override fun toObject(p: HeartBeatProtos.Status): Status =
            with(p) {
                Status(
                    dataType = dataType.name,
                    turnedOnTime = turnedOnTime,
                    lastTimeWritten = lastTimeWritten,
                    recordsCollected = recordsCollected,
                    recordsUploaded = recordsUploaded,
                    recordsRemained = recordsRemained,
                    isEnabled = isEnabled,
                    status = statusMap,
                    errorMessage = errorMessage,
                )
            }
    }
}
