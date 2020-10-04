package kaist.iclab.abclogger.service

import kaist.iclab.abclogger.db.DatabaseReader
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kaist.iclab.abclogger.grpc.service.AggregateOperationsGrpcKt
import kaist.iclab.abclogger.grpc.service.ServiceProtos
import kaist.iclab.abclogger.grpc.service.SubjectsOperationsGrpcKt
import kaist.iclab.abclogger.schema.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class SubjectsOperations(
    private val reader: DatabaseReader,
    private val bulkSize: Int,
    context: CoroutineContext = EmptyCoroutineContext
) : SubjectsOperationsGrpcKt.SubjectsOperationsCoroutineImplBase(context) {
    override suspend fun readSubjects(request: ServiceProtos.Query.Read): ServiceProtos.Bulk.Subjects =
        readDataInternal(request, false).toList().map { subject ->
            Subject.toProto(subject)
        }.let { subjects ->
            ServiceProtos.Bulk.Subjects.newBuilder().apply {
                addAllSubject(subjects)
            }.build()
        }

    override fun readSubjectsAsStream(request: ServiceProtos.Query.Read): Flow<SubjectProtos.Subject> =
        readDataInternal(request, true).toFlow().map { subject ->
            Subject.toProto(subject)
        }

    private fun readDataInternal(request: ServiceProtos.Query.Read, isStream: Boolean) = reader.readSubjects(
        fromTimestamp = request.fromTimestamp,
        toTimestamp = request.toTimestamp,
        dataTypes = request.datumTypeList.map { it.name },
        groupNames = request.groupNameList,
        emails = request.emailList,
        instanceIds = request.instanceIdList,
        sources = request.sourceList,
        deviceManufacturers = request.deviceManufacturerList,
        deviceModels = request.deviceModelList,
        deviceVersion = request.deviceVersionList,
        deviceOses = request.deviceOsList,
        appIds = request.appIdList,
        appVersions = request.appVersionList,
        limit = if (isStream) {
            request.limit.takeIf { it > 0 } ?: Int.MAX_VALUE
        } else {
            request.limit.takeIf { it > 0 }?.coerceAtMost(bulkSize) ?: bulkSize
        },
        isAscending = request.isAscending
    )
}