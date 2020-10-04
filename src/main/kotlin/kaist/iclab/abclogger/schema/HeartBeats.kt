package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.common.toOffsetDateTime
import kaist.iclab.abclogger.grpc.proto.DatumProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class HeartBeat(
    val timestamp: Long? = null,
    val utcOffsetSec: Int? = null,
    val subject: Subject? = null,
    val dataStatus: List<DataStatus> = listOf(),
    @Contextual
    val offsetTimestamp: OffsetDateTime? = null
) {
    companion object : ProtoSerializer<HeartBeat, HeartBeatProtos.HeartBeat> {
        override fun toProto(o: HeartBeat): HeartBeatProtos.HeartBeat =
            HeartBeatProtos.HeartBeat.newBuilder().apply {
                timestamp = o.timestamp ?: UNKNOWN_LONG
                utcOffsetSec = o.utcOffsetSec ?: UNKNOWN_INT
                subject = o.subject?.let { Subject.toProto(it) } ?: SubjectProtos.Subject.getDefaultInstance()
                addAllDataStatus(o.dataStatus.map { DataStatus.toProto(it)})
            }.build()

        override fun toObject(p: HeartBeatProtos.HeartBeat): HeartBeat =
            with(p) {
                HeartBeat(
                    timestamp = timestamp,
                    utcOffsetSec = utcOffsetSec,
                    subject = subject?.let { Subject.toObject(it) },
                    dataStatus = dataStatusList.map { DataStatus.toObject(it) },
                    offsetTimestamp = toOffsetDateTime(timestamp, utcOffsetSec)
                )
            }
    }
}

@Serializable
data class DataStatus(
    val name: String? = null,
    val qualifiedName: String? = null,
    val datumType: String? = null,
    val turnedOnTime: Long? = null,
    val lastTimeWritten: Long? = null,
    val recordsCollected: Long? = null,
    val recordsUploaded: Long? = null,
    val operation: String? = null,
    val error: String? = null,
    val others: Map<String, String> = mapOf()
) {
    companion object : ProtoSerializer<DataStatus, HeartBeatProtos.DataStatus> {
        override fun toProto(o: DataStatus): HeartBeatProtos.DataStatus =
            HeartBeatProtos.DataStatus.newBuilder().apply {
                name = o.name
                qualifiedName = o.qualifiedName
                datumType = safeEnumValuesOf(o.datumType, DatumProtos.Datum.Type.UNRECOGNIZED)
                turnedOnTime = o.turnedOnTime ?: UNKNOWN_LONG
                lastTimeWritten = o.lastTimeWritten ?: UNKNOWN_LONG
                recordsCollected = o.recordsCollected ?: UNKNOWN_LONG
                recordsUploaded = o.recordsUploaded ?: UNKNOWN_LONG
                operation = safeEnumValuesOf(o.operation, HeartBeatProtos.DataStatus.Operation.UNRECOGNIZED)
                error = o.error ?: UNKNOWN_STRING
                putAllOthers(o.others)
            }.build()

        override fun toObject(p: HeartBeatProtos.DataStatus): DataStatus =
            with(p) {
                DataStatus(
                    name = name,
                    qualifiedName = qualifiedName,
                    datumType = datumType.name,
                    turnedOnTime = turnedOnTime,
                    lastTimeWritten = lastTimeWritten,
                    recordsCollected = recordsCollected,
                    recordsUploaded = recordsUploaded,
                    operation = operation.name,
                    error = error,
                    others = othersMap
                )
            }
    }
}
