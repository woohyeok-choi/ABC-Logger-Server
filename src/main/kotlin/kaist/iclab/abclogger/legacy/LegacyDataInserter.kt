package kaist.iclab.abclogger.legacy

import io.grpc.*
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kaist.iclab.abclogger.db.DatabaseWriter
import kaist.iclab.abclogger.grpc.proto.DatumProtos
import kaist.iclab.abclogger.grpc.service.DataOperationsGrpcKt
import kaist.iclab.abclogger.schema.Datum
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.collect
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

object LegacyDataInserter {
    fun insertData(file: File, writer: DatabaseWriter) {
        file.inputStream().use { stream ->
            val buffer = ConcurrentLinkedQueue<Datum>()
            while (true) {
                val datum = DatumProtos.Datum.parseDelimitedFrom(stream) ?: break
                buffer.add(Datum.toObject(datum))

                if (buffer.size > 10000) {
                    writer.writeData(buffer.toList())
                    buffer.clear()
                }
            }
            if (buffer.isNotEmpty()) writer.writeData(buffer.toList())
        }
    }
}