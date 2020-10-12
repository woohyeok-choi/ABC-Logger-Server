package kaist.iclab.abclogger.service

import kaist.iclab.abclogger.db.DatabaseAggregator
import kaist.iclab.abclogger.grpc.proto.AggregationProtos
import kaist.iclab.abclogger.grpc.service.AggregateOperationsGrpcKt
import kaist.iclab.abclogger.grpc.service.ServiceProtos
import kaist.iclab.abclogger.interceptor.AuthInterceptor
import kaist.iclab.abclogger.schema.Group
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class AggregateOperations(
    private val aggregator: DatabaseAggregator,
    context: CoroutineContext = EmptyCoroutineContext
) : AggregateOperationsGrpcKt.AggregateOperationsCoroutineImplBase(context) {
    override suspend fun countSubjects(request: ServiceProtos.Query.Aggregate): AggregationProtos.Aggregation {
        val isMd5Hashed = AuthInterceptor.IS_MD5_HASHED.get() == true

        return aggregator.countSubjects(
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
            appVersions = request.appVersionList
        ).toList().map { group ->
            Group.toProto(group, isMd5Hashed)
        }.let { groups ->
            AggregationProtos.Aggregation.newBuilder().apply {
                addAllGroup(groups)
            }.build()
        }
    }

    override suspend fun countData(request: ServiceProtos.Query.Aggregate): AggregationProtos.Aggregation {
        val isMd5Hashed = AuthInterceptor.IS_MD5_HASHED.get() == true

        return aggregator.countData(
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
            appVersions = request.appVersionList
        ).toList().map { group ->
            Group.toProto(group, isMd5Hashed)
        }.let { groups ->
            AggregationProtos.Aggregation.newBuilder().apply {
                addAllGroup(groups)
            }.build()
        }
    }
}