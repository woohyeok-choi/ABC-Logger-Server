package kaist.iclab.abclogger.service

import kaist.iclab.abclogger.db.DatabaseReader
import kaist.iclab.abclogger.db.DatabaseWriter
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.DatumProtos
import kaist.iclab.abclogger.grpc.service.DataOperationsGrpcKt
import kaist.iclab.abclogger.grpc.service.ServiceProtos
import kaist.iclab.abclogger.interceptor.AuthInterceptor
import kaist.iclab.abclogger.schema.Datum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class DataOperations(
    private val reader: DatabaseReader,
    private val writer: DatabaseWriter,
    private val bulkSize: Int,
    context: CoroutineContext = EmptyCoroutineContext
) : DataOperationsGrpcKt.DataOperationsCoroutineImplBase(context) {
    override suspend fun createDatum(request: DatumProtos.Datum): CommonProtos.Empty {
        writer.write(Datum.toObject(request))
        return CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun createData(request: ServiceProtos.Bulk.Data): CommonProtos.Empty {
        request.datumList.forEach { writer.write(Datum.toObject(it)) }
        return CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun createDataAsStream(requests: Flow<DatumProtos.Datum>): CommonProtos.Empty {
        requests.map { Datum.toObject(it) }.collect { writer.write(it) }
        return CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun readData(request: ServiceProtos.Query.Read): ServiceProtos.Bulk.Data {
        val isMd5Hashed = AuthInterceptor.IS_MD5_HASHED.get() == true

        return readDataInternal(request, false, isMd5Hashed).toList().map { datum ->
            Datum.toProto(datum, isMd5Hashed)
        }.let { data ->
            ServiceProtos.Bulk.Data.newBuilder().apply {
                addAllDatum(data)
            }.build()
        }
    }

    override fun readDataAsStream(request: ServiceProtos.Query.Read): Flow<DatumProtos.Datum> {
        val isMd5Hashed = AuthInterceptor.IS_MD5_HASHED.get() == true

        return readDataInternal(request, true, isMd5Hashed).toFlow().map { datum ->
            Datum.toProto(datum, isMd5Hashed)
        }
    }

    private fun readDataInternal(request: ServiceProtos.Query.Read, isStream: Boolean, isMd5Hashed: Boolean) = reader.readData(
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