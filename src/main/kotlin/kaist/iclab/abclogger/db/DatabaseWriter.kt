package kaist.iclab.abclogger.db

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.common.getOffsetDateTime
import kaist.iclab.abclogger.grpc.proto.DataProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kaist.iclab.abclogger.schema.Datum
import kaist.iclab.abclogger.schema.HeartBeat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import java.util.concurrent.TimeUnit

class DatabaseWriter(private val database: Database) {
    private val dataBuffer: Subject<DataProtos.Datum> = PublishSubject.create()
    private val heartBeatBuffer: Subject<HeartBeatProtos.HeartBeat> = PublishSubject.create()
    private val io = Schedulers.io()

    init {
        val scope = CoroutineScope(Dispatchers.IO)

        dataBuffer.buffer(
                5, TimeUnit.SECONDS
        ).toFlowable(
                BackpressureStrategy.BUFFER
        ).asFlow().onEach { protoBuffers ->
            try {
                val currentTime = System.currentTimeMillis()
                val objects = protoBuffers.map { proto ->
                    val obj = Datum.toObject(proto)

                    obj.copy(
                            offsetTimestamp = getOffsetDateTime(obj.timestamp ?: currentTime, obj.utcOffsetSec),
                            uploadTime = currentTime,
                            offsetUploadTime = getOffsetDateTime(currentTime)
                    )
                }
                if (objects.isNotEmpty()) {
                    database.collection<Datum>().insertMany(objects)
                }
            } catch (e: Exception) {
                Log.error("DatabaseWriter.write() - Datum", e)
            }
        }.launchIn(scope)

        heartBeatBuffer.buffer(
                10, TimeUnit.SECONDS
        ).toFlowable(
                BackpressureStrategy.BUFFER
        ).asFlow().onEach { protoBuffers ->
            try {
                val currentTime = System.currentTimeMillis()
                val objects = protoBuffers.map { proto ->
                    val obj = HeartBeat.toObject(proto)

                    obj.copy(
                            offsetTimestamp = getOffsetDateTime(obj.timestamp ?: currentTime, obj.utcOffsetSec),
                            uploadTime = currentTime,
                            offsetUploadTime = getOffsetDateTime(currentTime)
                    )
                }
                if (objects.isNotEmpty()) {
                    database.collection<HeartBeat>().insertMany(objects)
                }
            } catch (e: Exception) {
                Log.error("DatabaseWriter.write() - HeartBeats", e)
            }
        }.launchIn(scope)
    }

    fun write(datum: DataProtos.Datum) {
        dataBuffer.onNext(datum)
    }

    fun write(heartBeat: HeartBeatProtos.HeartBeat) {
        heartBeatBuffer.onNext(heartBeat)
    }
}
