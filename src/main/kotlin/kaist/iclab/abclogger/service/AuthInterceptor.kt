package kaist.iclab.abclogger.service

import io.grpc.*

class AuthInterceptor(private val readKeys: Set<String>) : ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT>? {

        val authToken = headers?.get(Metadata.Key.of(AUTH_TOKEN, Metadata.ASCII_STRING_MARSHALLER))

        return if (authToken !in readKeys) {
            call?.close(Status.UNAUTHENTICATED, Metadata())
            object : ServerCall.Listener<ReqT>() { }
        } else {
            next?.startCall(call, headers)
        }
    }

    companion object {
        const val AUTH_TOKEN = "auth_token"
    }
}