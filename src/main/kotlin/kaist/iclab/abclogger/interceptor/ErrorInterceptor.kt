package kaist.iclab.abclogger.interceptor

import io.grpc.*
import kaist.iclab.abclogger.common.Log

class ErrorInterceptor : ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?
    ): ServerCall.Listener<ReqT> {
        val service = call?.methodDescriptor?.serviceName ?: "UNKNOWN_SERVICE"
        val method = call?.methodDescriptor?.fullMethodName ?: "UNKNOWN_METHOD"

        return next?.startCall(call, headers)?.let {
            getErrorHandler(
                service = service,
                method = method,
                listener = it
            )
        } ?: object : ServerCall.Listener<ReqT>() { }
    }

    private fun <T : Any?> getErrorHandler(
        service: String,
        method: String,
        listener: ServerCall.Listener<T>
    ): ServerCall.Listener<T> =
        object : ForwardingServerCallListener.SimpleForwardingServerCallListener<T>(listener) {
            override fun onHalfClose() {
                try {
                    super.onHalfClose()
                } catch (e: Exception) {
                    Log.error("$service.$method", e)
                    throw e
                }
            }

            override fun onReady() {
                try {
                    super.onReady()
                } catch (e: Exception) {
                    Log.error("$service.$method", e)
                    throw e
                }
            }
        }
}