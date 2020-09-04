package kaist.iclab.abclogger

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.grpc.proto.DataProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kaist.iclab.abclogger.schema.Datum
import kaist.iclab.abclogger.schema.HeartBeat
import java.util.concurrent.TimeUnit

class DatabaseWriter(private val database: Database) {
    private val compositeDisposable = CompositeDisposable()
    private val dataBuffer: Subject<DataProtos.Datum> = PublishSubject.create()
    private val heartBeatBuffer: Subject<HeartBeatProtos.HeartBeat> = PublishSubject.create()
    private val io = Schedulers.io()

    fun write(datum: DataProtos.Datum) {
        dataBuffer.onNext(datum)
    }

    fun write(heartBeat: HeartBeatProtos.HeartBeat) {
        heartBeatBuffer.onNext(heartBeat)
    }

    fun subscribe() {
        val dataDisposable = dataBuffer.buffer(
                10, TimeUnit.SECONDS
        ).subscribeOn(io).subscribe { proto ->
            try {
                val objects = proto.map { Datum.toObject(it) }
                if (objects.isNotEmpty()) database.collection<Datum>().insertMany(objects)
            } catch (e: Exception) {
                Log.error("Error occurs while writing data.", e)
            }
        }

        val heartBeatDisposable = heartBeatBuffer.buffer(
                10, TimeUnit.SECONDS
        ).subscribeOn(io).subscribe { proto ->
            try {
                val objects = proto.map { HeartBeat.toObject(it) }
                if (objects.isNotEmpty()) database.collection<HeartBeat>().insertMany(objects)
            } catch (e: Exception) {
                Log.error("Error occurs while writing heart beat.", e)
            }
        }

        compositeDisposable.addAll(dataDisposable, heartBeatDisposable)
    }

    fun unsubscribe() {
        compositeDisposable.clear()
    }
}
