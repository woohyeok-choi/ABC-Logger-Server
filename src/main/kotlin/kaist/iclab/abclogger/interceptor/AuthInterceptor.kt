package kaist.iclab.abclogger.interceptor

import io.grpc.*

class AuthInterceptor(private val authTokens: Set<String>) : ServerInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT> {

        val authToken = headers?.get(Metadata.Key.of(AUTH_TOKEN, Metadata.ASCII_STRING_MARSHALLER))

        val listener = if (authTokens.isNotEmpty() && authToken !in authTokens) {
            call?.close(Status.UNAUTHENTICATED, Metadata())
            null
        } else {
            next?.startCall(call, headers)
        }

        return listener ?: object : ServerCall.Listener<ReqT>() { }
    }

    companion object {
        const val AUTH_TOKEN = "auth_token"
    }
}