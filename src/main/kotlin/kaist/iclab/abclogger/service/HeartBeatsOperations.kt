package kaist.iclab.abclogger.service

import kaist.iclab.abclogger.db.DatabaseReader
import kaist.iclab.abclogger.db.DatabaseWriter
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kaist.iclab.abclogger.grpc.service.HeartBeatsOperationGrpcKt
import kaist.iclab.abclogger.grpc.service.ServiceProtos
import kaist.iclab.abclogger.interceptor.AuthInterceptor
import kaist.iclab.abclogger.schema.HeartBeat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class HeartBeatsOperations(
    private val reader: DatabaseReader,
    private val writer: DatabaseWriter,
    private val bulkSize: Int,
    context: CoroutineContext = EmptyCoroutineContext
) : HeartBeatsOperationGrpcKt.HeartBeatsOperationCoroutineImplBase(context) {
    override suspend fun createHeartBeat(request: HeartBeatProtos.HeartBeat): CommonProtos.Empty {
        writer.write(HeartBeat.toObject(request))
        return CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun readHeartBeats(request: ServiceProtos.Query.Read): ServiceProtos.Bulk.HeartBeats {
        val isMd5Hashed = AuthInterceptor.IS_MD5_HASHED.get() == true

        return readDataInternal(request, false, isMd5Hashed).toList().map { heartBeat ->
            HeartBeat.toProto(heartBeat, isMd5Hashed)
        }.let { heartBeats ->
            ServiceProtos.Bulk.HeartBeats.newBuilder().apply {
                addAllHeartBeat(heartBeats)
            }.build()
        }
    }

    override fun readHeartBeatsAsStream(request: ServiceProtos.Query.Read): Flow<HeartBeatProtos.HeartBeat> {
        val isMd5Hashed = AuthInterceptor.IS_MD5_HASHED.get() == true

        return readDataInternal(request, true, isMd5Hashed).toFlow().map { heartBeat ->
            HeartBeat.toProto(heartBeat, isMd5Hashed)
        }
    }

    private fun readDataInternal(request: ServiceProtos.Query.Read, isStream: Boolean, isMd5Hashed: Boolean) = reader.readHeartBeats(
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
        isAscending = request.isAscending,
        isMd5Hashed = isMd5Hashed
    )
}