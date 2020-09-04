package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
        val email: String? = null,
        val deviceId: String? = null,
        val deviceInfo: String? = null
) {

    companion object : ProtoSerializer<Subject, SubjectProtos.Subject> {
        override fun toProto(o: Subject): SubjectProtos.Subject =
                with(o) {
                    SubjectProtos.Subject.newBuilder().apply {
                        email = this@with.email ?: ""
                        deviceInfo = this@with.deviceInfo ?: ""
                        deviceId = this@with.deviceId ?: ""
                    }.build()
                }

        override fun toObject(p: SubjectProtos.Subject): Subject =
                with(p) {
                    Subject(
                            email = email,
                            deviceInfo = deviceInfo,
                            deviceId = deviceId
                    )
                }
    }
}