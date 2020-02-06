package kaist.iclab.abclogger

import io.grpc.ManagedChannelBuilder
import kaist.iclab.abclogger.grpc.DataOperationsCoroutineGrpc
import kaist.iclab.abclogger.grpc.DatumProto
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.toList
import java.util.concurrent.TimeUnit

fun main() = runBlocking {
    val result = upload(50)
    println("Size: ${result.size}/ ${result}")
}


suspend fun readSubjects(): List<DatumProto.Datum.Subject> = withContext(Dispatchers.IO) {
    val channel = ManagedChannelBuilder.forTarget("143.248.90.87:50051")
            .usePlaintext()
            .build()
    val stub = DataOperationsCoroutineGrpc.newStubWithContext(channel).withDeadlineAfter(10, TimeUnit.SECONDS)
    val request = DatumProto.Datum.Query.Subjects.newBuilder()
            .setDataType(DatumProto.Datum.Type.BLUETOOTH)
            .setFromTime(0)
            .setToTime(Long.MAX_VALUE).build()
    val receiveChannel = stub.readSubjects(request)
    val iterator = receiveChannel.iterator()
    val result = mutableListOf<DatumProto.Datum.Subject>()
    while (iterator.hasNext()) result.add(iterator.next())

    return@withContext result
}

suspend fun readData(): List<DatumProto.Datum> = withContext(Dispatchers.IO) {
    val channel = ManagedChannelBuilder.forTarget("143.248.90.87:50051")
            .usePlaintext()
            .build()
    val stub = DataOperationsCoroutineGrpc.newStubWithContext(channel).withDeadlineAfter(10, TimeUnit.SECONDS)
    val request = DatumProto.Datum.Query.Data.newBuilder()
            .setDataType(DatumProto.Datum.Type.BLUETOOTH)
            .setFromTime(0)
            .setToTime(Long.MAX_VALUE).build()
    val receiveChannel = stub.readData(request)
    receiveChannel.consumeEach {
        println(it)
    }
    return@withContext listOf<DatumProto.Datum>()
}

suspend fun upload(size: Int) = withContext(Dispatchers.IO) {
    val channel = ManagedChannelBuilder.forTarget("143.248.100.24:50052")
            .usePlaintext()
            .build()

    val stub = DataOperationsCoroutineGrpc.newStubWithContext(channel).withDeadlineAfter(10, TimeUnit.SECONDS)

    val data = (0 until size).map {
        async {
            try {
                stub.createDatum(DatumProto.Datum.newBuilder().setAppUsageEvent(DatumProto.Datum.AppUsageEvent.getDefaultInstance()).build())
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }.awaitAll().filterNotNull()

    channel.shutdownNow().awaitTermination(10, TimeUnit.SECONDS)
    data
}