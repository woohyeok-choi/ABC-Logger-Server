package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.common.toMd5Hash
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kotlinx.serialization.Serializable

@Serializable
data class Subject(
    val groupName : String? = null,
    val email: String? = null,
    val hashedEmail: String? = null,
    val instanceId: String? = null,
    val source: String? = null,
    val deviceManufacturer: String? = null,
    val deviceModel: String? = null,
    val deviceVersion: String? = null,
    val deviceOs: String? = null,
    val appId: String? = null,
    val appVersion: String? = null
) {
    companion object : TwoWaySerializer<Subject, SubjectProtos.Subject> {
        override fun toProto(o: Subject, isMd5Hashed: Boolean) = SubjectProtos.Subject.newBuilder().apply {
            groupName = o.groupName ?: UNKNOWN_STRING
            email = (if (isMd5Hashed) o.hashedEmail else o.email) ?: UNKNOWN_STRING
            instanceId = o.instanceId ?: UNKNOWN_STRING
            source = o.source ?: UNKNOWN_STRING
            deviceManufacturer = o.deviceManufacturer ?: UNKNOWN_STRING
            deviceModel = o.deviceModel ?: UNKNOWN_STRING
            deviceVersion = o.deviceVersion ?: UNKNOWN_STRING
            deviceOs = o.deviceOs ?: UNKNOWN_STRING
            appId = o.appId ?: UNKNOWN_STRING
            appVersion = o.appVersion ?: UNKNOWN_STRING
        }.build()

        override fun toObject(p: SubjectProtos.Subject): Subject =
            with(p) {
                Subject(
                    groupName = groupName,
                    email = email,
                    hashedEmail = toMd5Hash(email),
                    instanceId = instanceId,
                    source = source,
                    deviceManufacturer = deviceManufacturer,
                    deviceModel = deviceModel,
                    deviceVersion = deviceVersion,
                    deviceOs = deviceOs,
                    appId = appId,
                    appVersion = appVersion
                )
            }
    }
}