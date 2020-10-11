package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.grpc.proto.*
import kotlinx.serialization.Serializable
import org.litote.kmongo.group

@Serializable
data class Aggregation(
    val group: List<Group> = listOf()
) {
    companion object : ProtoSerializer<Aggregation, AggregationProtos.Aggregation> {
        override fun toProto(o: Aggregation, isMd5Hashed: Boolean): AggregationProtos.Aggregation = AggregationProtos.Aggregation.newBuilder().apply {
            addAllGroup(o.group.map { Group.toProto(it, isMd5Hashed) })
        }.build()
    }
}

@Serializable
data class Group(
    val datumType: String? = null,
    val subject: Subject? = null,
    val value: Double? = null
) {
    companion object : ProtoSerializer<Group, AggregationProtos.Aggregation.Group> {
        override fun toProto(o: Group, isMd5Hashed: Boolean): AggregationProtos.Aggregation.Group = AggregationProtos.Aggregation.Group.newBuilder().apply {
            datumType = safeEnumValuesOf(o.datumType, DatumProtos.DatumType.UNRECOGNIZED)
            subject = o.subject?.let { Subject.toProto(it, isMd5Hashed) } ?: SubjectProtos.Subject.getDefaultInstance()
            value = o.value ?: UNKNOWN_DOUBLE
        }.build()
    }
}
