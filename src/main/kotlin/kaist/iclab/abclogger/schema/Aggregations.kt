package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.grpc.proto.*
import kotlinx.serialization.Serializable
import org.litote.kmongo.group

@Serializable
data class Aggregation(
    val group: List<Group> = listOf()
) {
    companion object : ProtoSerializer<Aggregation, AggregationProtos.Aggregation> {
        override fun toProto(o: Aggregation): AggregationProtos.Aggregation = AggregationProtos.Aggregation.newBuilder().apply {
            addAllGroup(o.group.map { Group.toProto(it) })
        }.build()

        override fun toObject(p: AggregationProtos.Aggregation): Aggregation = with(p) {
            Aggregation(
                group = groupList.map { Group.toObject(it) }
            )
        }
    }
}

@Serializable
data class Group(
    val datumType: String? = null,
    val subject: Subject? = null,
    val value: Double? = null
) {
    companion object: ProtoSerializer<Group, AggregationProtos.Aggregation.Group> {
        override fun toProto(o: Group): AggregationProtos.Aggregation.Group = AggregationProtos.Aggregation.Group.newBuilder().apply {
            datumType = safeEnumValuesOf(o.datumType, DatumProtos.Datum.Type.UNRECOGNIZED)
            subject = o.subject?.let { Subject.toProto(it) } ?: SubjectProtos.Subject.getDefaultInstance()
            value = o.value ?: UNKNOWN_DOUBLE
        }.build()

        override fun toObject(p: AggregationProtos.Aggregation.Group): Group = with(p) {
            Group(
                datumType = datumType.name,
                subject = Subject.toObject(subject),
                value = value,
            )
        }
    }
}
