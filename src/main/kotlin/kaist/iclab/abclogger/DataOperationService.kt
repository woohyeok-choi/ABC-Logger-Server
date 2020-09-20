package kaist.iclab.abclogger

import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.db.DatabaseReader
import kaist.iclab.abclogger.db.DatabaseWriter
import kaist.iclab.abclogger.grpc.DataOperationsGrpcKt
import kaist.iclab.abclogger.grpc.QueryProtos
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.DataProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kaist.iclab.abclogger.schema.Datum
import kaist.iclab.abclogger.schema.HeartBeat
import kaist.iclab.abclogger.schema.Subject
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map

class DataOperationService(private val reader: DatabaseReader, private val writer: DatabaseWriter)
    : DataOperationsGrpcKt.DataOperationsCoroutineImplBase(ThreadLocal.withInitial { "kaist.iclab.abclogger.DataOperationService" }.asContextElement()) {
   override suspend fun createDatum(request: DataProtos.Datum): CommonProtos.Empty = log("createDatum($request)") {
        writer.write(request)
        CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun createData(request: QueryProtos.Bulk.Data): CommonProtos.Empty = log("createData($request)") {
        request.dataList.forEach { writer.write(it) }
        CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun createDataAsStream(requests: Flow<DataProtos.Datum>): CommonProtos.Empty = suspendLog("createDataAsStream($requests)") {
        requests.collect { writer.write(it) }
        CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun createHeartBeat(request: HeartBeatProtos.HeartBeat): CommonProtos.Empty = log("createHeartBeat($request)") {
        writer.write(request)
        CommonProtos.Empty.getDefaultInstance()
    }

    override suspend fun readData(request: QueryProtos.Query.Data): QueryProtos.Bulk.Data = suspendLog("readData($request)") {
        val publisher = reader.readData(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED) "" else request.dataType.name,
                email = request.email,
                deviceInfo = request.deviceInfo,
                deviceId = request.deviceId,
                limit = if(request.limit == 0) MAX_LIMIT else request.limit.coerceAtMost(MAX_LIMIT),
                isAscending = request.isAscending
        )
        val responses = publisher.toList().map { Datum.toProto(it) }

        QueryProtos.Bulk.Data.newBuilder().apply {
            addAllData(responses)
        }.build()
    }

    override fun readDataAsStream(request: QueryProtos.Query.Data): Flow<DataProtos.Datum> = log("readDataAsStream($request)") {
        val publisher = reader.readData(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED) "" else request.dataType.name,
                email = request.email,
                deviceInfo = request.deviceInfo,
                deviceId = request.deviceId,
                limit = if(request.limit == 0) MAX_LIMIT else request.limit.coerceAtMost(MAX_LIMIT),
                isAscending = request.isAscending
        )
        publisher.toFlow().map { Datum.toProto(it) }
    }

    override suspend fun countData(request: QueryProtos.Query.Data): QueryProtos.Count = suspendLog("countData($request)"){
        val count = reader.countData(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED) "" else request.dataType.name,
                email = request.email,
                deviceInfo = request.deviceInfo,
                deviceId = request.deviceId
        )
        QueryProtos.Count.newBuilder().setValue(count).build()
    }

    override suspend fun readHeartBeats(request: QueryProtos.Query.HeartBeats): QueryProtos.Bulk.HeartBeats = suspendLog("readHeartBeats($request)") {
        val publisher = reader.readHeartBeats(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED) "" else request.dataType.name,
                email = request.email,
                deviceInfo = request.deviceInfo,
                deviceId = request.deviceId,
                limit = if(request.limit == 0) MAX_LIMIT else request.limit.coerceAtMost(MAX_LIMIT),
                isAscending = request.isAscending
        )
        val responses = publisher.toList().map { HeartBeat.toProto(it) }

        QueryProtos.Bulk.HeartBeats.newBuilder().apply {
            addAllHeartBeat(responses)
        }.build()
    }

    override fun readHeartBeatsAsStream(request: QueryProtos.Query.HeartBeats): Flow<HeartBeatProtos.HeartBeat> = log("readHeartBeatsAsStream($request)") {
        val publisher = reader.readHeartBeats(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED) "" else request.dataType.name,
                email = request.email,
                deviceInfo = request.deviceInfo,
                deviceId = request.deviceId,
                limit = if(request.limit == 0) MAX_LIMIT else request.limit.coerceAtMost(MAX_LIMIT),
                isAscending = request.isAscending
        )
        publisher.toFlow().map { HeartBeat.toProto(it) }
    }

    override suspend fun countHeartBeat(request: QueryProtos.Query.HeartBeats): QueryProtos.Count = suspendLog("countHeartBeat($request)"){
        val count = reader.countHeartBeats(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED) "" else request.dataType.name,
                email = request.email,
                deviceInfo = request.deviceInfo,
                deviceId = request.deviceId
        )
        QueryProtos.Count.newBuilder().setValue(count).build()
    }

    override suspend fun readSubjects(request: QueryProtos.Query.Subjects): QueryProtos.Bulk.Subjects = suspendLog("readSubjects($request)") {
        val publisher = reader.readSubjects(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED ||
                        request.dataType == CommonProtos.DataType.UNRECOGNIZED) "" else request.dataType.name,
                limit = if(request.limit == 0) MAX_LIMIT else request.limit.coerceAtMost(MAX_LIMIT),
                isAscending = request.isAscending
        )
        val responses = publisher.toList().map { Subject.toProto(it) }

        QueryProtos.Bulk.Subjects.newBuilder().apply {
            addAllSubject(responses)
        }.build()
    }

    override fun readSubjectsAsStream(request: QueryProtos.Query.Subjects): Flow<SubjectProtos.Subject> = log("readSubjectsAsStream($request)") {
        val publisher = reader.readSubjects(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED ||
                        request.dataType == CommonProtos.DataType.UNRECOGNIZED) "" else request.dataType.name,
                limit = if(request.limit == 0) MAX_LIMIT else request.limit.coerceAtMost(MAX_LIMIT),
                isAscending = request.isAscending
        )
        publisher.toFlow().map { Subject.toProto(it) }
    }

    override suspend fun countSubjects(request: QueryProtos.Query.Subjects): QueryProtos.Count = suspendLog("countSubjects($request)") {
        val count = reader.countSubjects(
                fromTimestamp = request.fromTimestamp,
                toTimestamp = request.toTimestamp,
                dataType = if (request.dataType == CommonProtos.DataType.NOT_SPECIFIED ||
                        request.dataType == CommonProtos.DataType.UNRECOGNIZED) "" else request.dataType.name
        )
        QueryProtos.Count.newBuilder().setValue(count).build()
    }

    private suspend fun <RES> suspendLog(method: String, block: suspend () -> RES) = try {
        Log.info("DataOperationService.$method")
        block()
    } catch (e: Exception) {
        Log.error("DataOperationService.$method", e)
        throw e
    }

    private fun <RES> log(method: String, block: () -> RES) = try {
        Log.info("DataOperationService.$method")
        block()
    } catch (e: Exception) {
        Log.error("DataOperationService.$method", e)
        throw e
    }

    companion object {
        private const val MAX_LIMIT = 500
    }
}