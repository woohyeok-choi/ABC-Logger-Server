package kaist.iclab.abclogger.interceptor

import io.grpc.*

class AuthInterceptor(private val rootTokens: Set<String>, private val readOnlyTokens: Set<String>) : ServerInterceptor {
    private val allTokens = rootTokens + readOnlyTokens

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        call: ServerCall<ReqT, RespT>?,
        headers: Metadata?,
        next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT> {

        val authToken = headers?.get(Metadata.Key.of(AUTH_TOKEN, Metadata.ASCII_STRING_MARSHALLER))

        return if (allTokens.isNotEmpty() && authToken !in allTokens) {
            call?.close(Status.UNAUTHENTICATED.withDescription("Unauthenticated request. " +
                "Any request should have a field, $AUTH_TOKEN, with a valid token." +
                "Please ask related information to abc.logger@kse.kaist.ac.kr"), Metadata())
            object : ServerCall.Listener<ReqT>() { }
        } else {
            val isReadOnly = authToken in readOnlyTokens
            val context = Context.current().withValue(IS_MD5_HASHED, isReadOnly)

            Contexts.interceptCall(
                context, call, headers, next
            )
        }
    }

    companion object {
        private const val AUTH_TOKEN = "auth_token"
        private const val KEY_IS_MD5_ENCRYPTED = "IS_MD5_ENCRYPTED"
        val IS_MD5_HASHED: Context.Key<Boolean> = Context.key(KEY_IS_MD5_ENCRYPTED)
    }
}