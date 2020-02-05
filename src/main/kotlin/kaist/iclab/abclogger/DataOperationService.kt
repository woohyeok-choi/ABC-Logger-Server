package kaist.iclab.abclogger

import io.grpc.Status
import kaist.iclab.abclogger.grpc.DataOperationsCoroutineGrpc
import kaist.iclab.abclogger.grpc.DatumProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.channels.SendChannel
import kotlin.coroutines.CoroutineContext

class DataOperationService(private val reader: DBReader, private val writer: DBWriter) : DataOperationsCoroutineGrpc.DataOperationsImplBase() {
    private val localThread = ThreadLocal.withInitial { "data_operation" }.asContextElement()
    override val initialContext: CoroutineContext = Dispatchers.IO + localThread

    override suspend fun createDatum(request: DatumProto.Datum): DatumProto.Empty {
        writer.write(request)
        return DatumProto.Empty.getDefaultInstance()
    }

    override suspend fun readData(request: DatumProto.Datum.Query.Data, responseChannel: SendChannel<DatumProto.Datum>) {
        try {
            val dataType = request.dataType
            val subjectEmail = request.subjectEmail
            val fromTime = request.fromTime
            val toTime = request.toTime
            val limit = request.limit
            val isDescending = request.isDescending

            val data = reader.readData(
                    dataType = dataType,
                    subjectEmail = subjectEmail,
                    fromTime = fromTime,
                    toTime = toTime,
                    limit = limit,
                    isDescending = isDescending
            )

            if (data.isEmpty()) throw Status.NOT_FOUND.withDescription("There is no data.").asRuntimeException()
            data.forEach { datum -> responseChannel.send(datum) }
        } catch (e: Exception) {
            Log.error("Failed to readData for request: $request", e)
            responseChannel.close(e)
        }
    }

    override suspend fun readSubjects(request: DatumProto.Datum.Query.Subjects, responseChannel: SendChannel<DatumProto.Datum.Subject>) {
        try {
            val dataType = request.dataType
            val fromTime = request.fromTime
            val toTime = request.toTime
            val limit = request.limit
            val isDescending = request.isDescending

            val data = reader.readSubjects(
                    dataType = dataType,
                    fromTime = fromTime,
                    toTime = toTime,
                    limit = limit,
                    isDescending = isDescending
            )

            if (data.isEmpty()){
                throw Status.NOT_FOUND.withDescription("There is no data.").asRuntimeException()
            }

            data.forEach { datum -> responseChannel.send(datum) }
        } catch (e: Exception) {
            Log.error("Failed to readSubjects for request: $request", e)
            responseChannel.close(e)
        }
    }
}