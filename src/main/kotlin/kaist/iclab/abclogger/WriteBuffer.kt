package kaist.iclab.abclogger

import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kaist.iclab.abclogger.grpc.DatumProto

import java.util.concurrent.TimeUnit

class WriteBuffer(tables: Array<BaseTable>) {
    private val buffers : Map<String, Subject<DatumProto.Datum>> = tables.associate { table -> table.tableName to PublishSubject.create<DatumProto.Datum>() }

    fun put(tableName: String, datum: DatumProto.Datum) {
        buffers[tableName]?.onNext(datum)
    }

    fun subscribe(tableName: String, subscriber: (List<DatumProto.Datum>) -> Unit) : Disposable? {
        return buffers[tableName]?.buffer(10, TimeUnit.SECONDS)?.subscribe(subscriber)
    }
}