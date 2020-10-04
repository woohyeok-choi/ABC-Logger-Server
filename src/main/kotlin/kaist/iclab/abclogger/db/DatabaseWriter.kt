package kaist.iclab.abclogger.db

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.schema.Datum
import kaist.iclab.abclogger.schema.HeartBeat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import java.util.concurrent.TimeUnit

class DatabaseWriter(private val database: Database) {
    private val dataBuffer: Subject<Datum> = PublishSubject.create()
    private val heartBeatBuffer: Subject<HeartBeat> = PublishSubject.create()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val dataFlow = dataBuffer.buffer(
        5, TimeUnit.SECONDS
    ).toFlowable(
        BackpressureStrategy.BUFFER
    ).asFlow()

    private val heartBeatFlow = heartBeatBuffer.buffer(
        10, TimeUnit.SECONDS
    ).toFlowable(
        BackpressureStrategy.BUFFER
    ).asFlow()

    init {
        scope.launch {
            dataFlow.collect { data ->
                try {
                    if (data.isNotEmpty()) {
                        database.collection<Datum>().insertMany(data)
                        Log.info("DatabaseWriter.write() - Data (size=${data.size})")
                    }
                } catch (e: Exception) {
                    Log.error("DatabaseWriter.write() - Data", e)
                }
            }
        }

        scope.launch {
            heartBeatFlow.collect { data ->
                try {
                    if (data.isNotEmpty()) {
                        database.collection<HeartBeat>().insertMany(data)
                        Log.info("DatabaseWriter.write() - HeartBeats (size=${data.size})")
                    }
                } catch (e: Exception) {
                    Log.error("DatabaseWriter.write() - HeartBeat", e)
                }
            }
        }
    }

    fun write(datum: Datum) {
        dataBuffer.onNext(datum)
    }

    fun write(heartBeat: HeartBeat) {
        heartBeatBuffer.onNext(heartBeat)
    }
}
