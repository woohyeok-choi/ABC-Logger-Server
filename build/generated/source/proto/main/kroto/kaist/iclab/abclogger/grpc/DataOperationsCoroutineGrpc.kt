// THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY.
package kaist.iclab.abclogger.grpc

import com.github.marcoferrer.krotoplus.coroutines.StubDefinition
import com.github.marcoferrer.krotoplus.coroutines.client.clientCallUnary
import com.github.marcoferrer.krotoplus.coroutines.server.ServiceScope
import com.github.marcoferrer.krotoplus.coroutines.server.serverCallUnary
import com.github.marcoferrer.krotoplus.coroutines.server.serverCallUnimplementedUnary
import com.github.marcoferrer.krotoplus.coroutines.withCoroutineContext
import io.grpc.BindableService
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.stub.AbstractStub
import io.grpc.stub.StreamObserver
import io.grpc.stub.annotations.RpcMethod
import javax.annotation.Generated
import kotlin.String
import kotlin.Unit
import kotlin.jvm.JvmStatic

@Generated(
        value = ["by Kroto+ Proto-c Grpc Coroutines Plugin (version 0.6.0)"],
        comments = "Source: data.proto"
)
object DataOperationsCoroutineGrpc {
    const val SERVICE_NAME: String = DataOperationsGrpc.SERVICE_NAME

    @JvmStatic
    @get:RpcMethod(
            fullMethodName = "$SERVICE_NAME/CreateDatum",
            requestType = DatumProto.Datum::class,
            responseType = DatumProto.Empty::class,
            methodType = MethodDescriptor.MethodType.UNARY
    )
    val createDatumMethod: MethodDescriptor<DatumProto.Datum, DatumProto.Empty>
        get() = DataOperationsGrpc.getCreateDatumMethod()

    @JvmStatic
    @get:RpcMethod(
            fullMethodName = "$SERVICE_NAME/ReadData",
            requestType = DatumProto.Datum.Query.Data::class,
            responseType = DatumProto.Datum.Data::class,
            methodType = MethodDescriptor.MethodType.UNARY
    )
    val readDataMethod: MethodDescriptor<DatumProto.Datum.Query.Data, DatumProto.Datum.Data>
        get() = DataOperationsGrpc.getReadDataMethod()

    @JvmStatic
    @get:RpcMethod(
            fullMethodName = "$SERVICE_NAME/ReadSubjects",
            requestType = DatumProto.Datum.Query.Subjects::class,
            responseType = DatumProto.Datum.Subjects::class,
            methodType = MethodDescriptor.MethodType.UNARY
    )
    val readSubjectsMethod: MethodDescriptor<DatumProto.Datum.Query.Subjects,
            DatumProto.Datum.Subjects>
        get() = DataOperationsGrpc.getReadSubjectsMethod()

    fun newStub(channel: Channel): DataOperationsCoroutineStub =
            DataOperationsCoroutineStub.newStub(channel)
    suspend fun newStubWithContext(channel: Channel): DataOperationsCoroutineStub =
            DataOperationsCoroutineStub.newStubWithContext(channel)
    class DataOperationsCoroutineStub private constructor(channel: Channel, callOptions: CallOptions
            = CallOptions.DEFAULT) : AbstractStub<DataOperationsCoroutineStub>(channel, callOptions)
            {
        override fun build(channel: Channel, callOptions: CallOptions): DataOperationsCoroutineStub
                = DataOperationsCoroutineStub(channel,callOptions)

        suspend fun createDatum(request: DatumProto.Datum = DatumProto.Datum.getDefaultInstance()):
                DatumProto.Empty = clientCallUnary(request,
                DataOperationsGrpc.getCreateDatumMethod())

        suspend inline fun createDatum(block: DatumProto.Datum.Builder.() -> Unit): DatumProto.Empty
                {
            val request = DatumProto.Datum.newBuilder()
                .apply(block)
                .build()
            return createDatum(request)
        }

        suspend fun readData(request: DatumProto.Datum.Query.Data =
                DatumProto.Datum.Query.Data.getDefaultInstance()): DatumProto.Datum.Data =
                clientCallUnary(request, DataOperationsGrpc.getReadDataMethod())

        suspend inline fun readData(block: DatumProto.Datum.Query.Data.Builder.() -> Unit):
                DatumProto.Datum.Data {
            val request = DatumProto.Datum.Query.Data.newBuilder()
                .apply(block)
                .build()
            return readData(request)
        }

        suspend fun readSubjects(request: DatumProto.Datum.Query.Subjects =
                DatumProto.Datum.Query.Subjects.getDefaultInstance()): DatumProto.Datum.Subjects =
                clientCallUnary(request, DataOperationsGrpc.getReadSubjectsMethod())

        suspend inline fun readSubjects(block: DatumProto.Datum.Query.Subjects.Builder.() -> Unit):
                DatumProto.Datum.Subjects {
            val request = DatumProto.Datum.Query.Subjects.newBuilder()
                .apply(block)
                .build()
            return readSubjects(request)
        }

        companion object : StubDefinition<DataOperationsCoroutineStub> {
            override val serviceName: String = DataOperationsGrpc.SERVICE_NAME

            override fun newStub(channel: Channel): DataOperationsCoroutineStub =
                    DataOperationsCoroutineStub(channel)
            override suspend fun newStubWithContext(channel: Channel): DataOperationsCoroutineStub =
                    DataOperationsCoroutineStub(channel).withCoroutineContext()}
    }

    abstract class DataOperationsImplBase : BindableService, ServiceScope {
        private val delegate: ServiceDelegate = ServiceDelegate()

        override fun bindService(): ServerServiceDefinition = delegate.bindService()
        open suspend fun createDatum(request: DatumProto.Datum): DatumProto.Empty =
                serverCallUnimplementedUnary(DataOperationsGrpc.getCreateDatumMethod())

        open suspend fun readData(request: DatumProto.Datum.Query.Data): DatumProto.Datum.Data =
                serverCallUnimplementedUnary(DataOperationsGrpc.getReadDataMethod())

        open suspend fun readSubjects(request: DatumProto.Datum.Query.Subjects):
                DatumProto.Datum.Subjects =
                serverCallUnimplementedUnary(DataOperationsGrpc.getReadSubjectsMethod())

        private inner class ServiceDelegate : DataOperationsGrpc.DataOperationsImplBase() {
            override fun createDatum(request: DatumProto.Datum, responseObserver:
                    StreamObserver<DatumProto.Empty>) {
                serverCallUnary(DataOperationsGrpc.getCreateDatumMethod(),responseObserver) {
                    createDatum(request)
                }
            }

            override fun readData(request: DatumProto.Datum.Query.Data, responseObserver:
                    StreamObserver<DatumProto.Datum.Data>) {
                serverCallUnary(DataOperationsGrpc.getReadDataMethod(),responseObserver) {
                    readData(request)
                }
            }

            override fun readSubjects(request: DatumProto.Datum.Query.Subjects, responseObserver:
                    StreamObserver<DatumProto.Datum.Subjects>) {
                serverCallUnary(DataOperationsGrpc.getReadSubjectsMethod(),responseObserver) {
                    readSubjects(request)
                }
            }
        }
    }
}