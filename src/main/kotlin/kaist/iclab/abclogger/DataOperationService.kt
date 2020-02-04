package kaist.iclab.abclogger

import io.grpc.Status
import kaist.iclab.abclogger.grpc.DataOperationsCoroutineGrpc
import kaist.iclab.abclogger.grpc.DatumProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asContextElement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.CoroutineContext

class DataOperationService(private val writeBuffer: WriteBuffer, private val bufferMapper: (DatumProto.Datum) -> String?) : DataOperationsCoroutineGrpc.DataOperationsImplBase() {
    private val localThread = ThreadLocal.withInitial { "data_operation" }.asContextElement()
    override val initialContext: CoroutineContext = Dispatchers.IO + localThread

    override suspend fun createDatum(request: DatumProto.Datum): DatumProto.Empty {
        val tableName = bufferMapper(request) ?: throw Status.INVALID_ARGUMENT.withDescription("No corresponding table.").asRuntimeException()
        writeBuffer.put(tableName, request)
        return DatumProto.Empty.getDefaultInstance()
    }

    override suspend fun readData(request: DatumProto.Datum.Query.Data): DatumProto.Datum.Data {
        return super.readData(request)
    }

    override suspend fun readSubjects(request: DatumProto.Datum.Query.Subjects): DatumProto.Datum.Subjects {
        val dataType = request.dataType
                ?: throw Status.INVALID_ARGUMENT.withDescription("readSubjects requires a field, 'dataType'.").asRuntimeException()
        val fromTime = request.fromTime
        val toTime = request.toTime
        val limit = request.limit
        val isDescending = request.isDescending


        return super.readSubjects(request)
    }
}