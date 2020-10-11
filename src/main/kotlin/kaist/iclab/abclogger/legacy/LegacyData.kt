package kaist.iclab.abclogger.legacy

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kaist.iclab.abclogger.grpc.proto.DatumProtos
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import okio.Okio
import java.io.File
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import com.google.protobuf.Message as ProtoMessage

abstract class ToProto<T : ProtoMessage>(
    open val timestamp: Long,
    open val pid: String,
    @Json(name = "source_info")
    open val sourceInfo: String,
) {
    abstract fun toProto(): T
}

fun main() {
    val defaultPath = "F:\\Projects\\ABC-Logger-Server\\test\\legacy-data"
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val userAdapter = moshi.adapter<List<User>>(Types.newParameterizedType(List::class.java, User::class.java))
    val users = userAdapter.fromJson(
        File("$defaultPath\\emails.json").readText()
    )!!

    File("$defaultPath\\output.pb").outputStream().use { stream ->
        buildData<LegacyActivityTransition>(
            users = users,
            path = "$defaultPath\\activity_transition.json",
            stream = stream
        ) {
            physicalActivityTransition = it.toProto()
        }
        buildData<LegacyAppUsageEvent>(
            users = users,
            path = "$defaultPath\\app_usage_event.json",
            stream = stream
        ) {
            appUsageEvent = it.toProto()
        }
        buildData<LegacyBattery>(
            users = users,
            path = "$defaultPath\\battery.json",
            stream = stream
        ) {
            battery = it.toProto()
        }
        buildData<LegacyCall>(
            users = users,
            path = "$defaultPath\\call.json",
            stream = stream
        ) {
            callLog = it.toProto()
        }
        buildData<LegacyConnectivity>(
            users = users,
            path = "$defaultPath\\connectivity.json",
            stream = stream
        ) {
            deviceEvent = it.toProto()
        }
        buildData<LegacyDataTraffic>(
            users = users,
            path = "$defaultPath\\data_traffic.json",
            stream = stream
        ) {
            dataTraffic = it.toProto()
        }
        buildData<LegacyDeviceEvent>(
            users = users,
            path = "$defaultPath\\device_event.json",
            stream = stream
        ) {
            deviceEvent = it.toProto()
        }
        buildData<LegacyLocation>(
            users = users,
            path = "$defaultPath\\location.json",
            stream = stream
        ) {
            location = it.toProto()
        }
        buildData<LegacyMedia>(
            users = users,
            path = "$defaultPath\\media.json",
            stream = stream
        ) {
            media = it.toProto()
        }
        buildData<LegacyMessage>(
            users = users,
            path = "$defaultPath\\message.json",
            stream = stream
        ) {
            message = it.toProto()
        }
        buildData<LegacyNotification>(
            users = users,
            path = "$defaultPath\\notification.json",
            stream = stream
        ) {
            notification = it.toProto()
        }
        buildData<LegacyWifi>(
            users = users,
            path = "$defaultPath\\wifi.json",
            stream = stream
        ) {
            wifi = it.toProto()
        }
         buildData(
            users = users,
            path = "$defaultPath\\wearables-fitbit.json",
            stream = stream
        )
    }
}

inline fun <reified T : ToProto<*>> buildData(users: List<User>, path: String, stream: OutputStream, block: DatumProtos.Datum.Builder.(T) -> Unit) {
    val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        .adapter<List<T>>(
            Types.newParameterizedType(List::class.java, T::class.java)
        )
    val data = adapter.fromJson(Okio.buffer(Okio.source(File(path))))!!

    data.asSequence().forEach { datum ->
        val user = users.firstOrNull { it.hashedEmail == datum.pid && it.sourceInfo == datum.sourceInfo }
        if (user == null) {
            println(datum)
            throw NoSuchElementException()
        }

        DatumProtos.Datum.newBuilder().apply {
            timestamp = datum.timestamp
            subject = user.toProto()
            uploadTime = 0
            utcOffsetSec = TimeUnit.HOURS.toSeconds(9).toInt()
            block.invoke(this, datum)
        }.build().writeDelimitedTo(stream)
    }
}

fun buildData(users: List<User>, path: String, stream: OutputStream) {
    val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        .adapter<List<LegacyFitbitData>>(
            Types.newParameterizedType(List::class.java, LegacyFitbitData::class.java)
        )
    val data = adapter.fromJson(Okio.buffer(Okio.source(File(path))))!!

    data.asSequence().forEach { datum ->
        val user = users.firstOrNull { it.hashedEmail == datum.pid  }
        if (user == null) {
            println(datum)
            throw NoSuchElementException()
        }

        DatumProtos.Datum.newBuilder().apply {
            timestamp = datum.timestamp
            subject = SubjectProtos.Subject.newBuilder().apply {
                groupName = "BeActive"
                email = user.email
                source = "WEARABLE"
                deviceManufacturer = "FITBIT"
                deviceModel = "IONIC"
            }.build()
            uploadTime = 0
            utcOffsetSec = TimeUnit.HOURS.toSeconds(9).toInt()
            externalSensor = datum.toProto()
        }.build().writeDelimitedTo(stream)
    }
}

data class User(
    val email: String,
    @Json(name = "md5(email)")
    val hashedEmail: String,
    @Json(name = "source_info")
    val sourceInfo: String,
) {
    val groupName = "BeActive"
    val source = "SMARTPHONE"
    val deviceManufacturer = sourceInfo.substringBefore("-")
    val deviceModel = sourceInfo.substringAfter("-").substringBeforeLast("-")
    val deviceVersion = sourceInfo.substringAfterLast("-")
    val deviceOs = "Android-$deviceVersion"
    val appId = "kaist.iclab.abclogger"
    val appVersion = "0.9.6"

    fun toProto() = SubjectProtos.Subject.newBuilder().apply {
        groupName = this@User.groupName
        email = this@User.email
        source = this@User.source
        deviceManufacturer = this@User.deviceManufacturer.toUpperCase()
        deviceModel = this@User.deviceModel.toUpperCase()
        deviceVersion = this@User.deviceVersion
        deviceOs = this@User.deviceOs
        appId = this@User.appId
        appVersion = this@User.appVersion
    }.build()
}

data class LegacyActivityTransition(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    @Json(name = "transition_type")
    val transitionType: String
) : ToProto<DatumProtos.PhysicalActivityTransition>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.PhysicalActivityTransition = DatumProtos.PhysicalActivityTransition.newBuilder().apply {
        isEntered = transitionType.startsWith("ENTER")
        type = transitionType.substringAfter("_")
    }.build()
}

data class LegacyAppUsageEvent(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val name: String? = null,
    @Json(name = "package_name")
    val packageName: String,
    val type: String,
    @Json(name = "is_system_app")
    val isSystemApp: Int,
    @Json(name = "is_updated_system_app")
    val isUpdatedSystemApp: Int
) : ToProto<DatumProtos.AppUsageEvent>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.AppUsageEvent = DatumProtos.AppUsageEvent.newBuilder().apply {
        name = this@LegacyAppUsageEvent.name
        packageName = this@LegacyAppUsageEvent.packageName
        type = when (this@LegacyAppUsageEvent.type) {
            "MOVE_TO_FOREGROUND" -> "ACTIVITY_RESUMED"
            "MOVE_TO_BACKGROUND" -> "ACTIVITY_PAUSED"
            else -> this@LegacyAppUsageEvent.type
        }
        isSystemApp = this@LegacyAppUsageEvent.isSystemApp == 1
        isUpdatedSystemApp = this@LegacyAppUsageEvent.isUpdatedSystemApp == 1
    }.build()
}

data class LegacyBattery(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val level: Int,
    val temperature: Int,
    val plugged: String,
    val status: String
) : ToProto<DatumProtos.Battery>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.Battery = DatumProtos.Battery.newBuilder().apply {
        level = this@LegacyBattery.level
        temperature = this@LegacyBattery.temperature
        pluggedType = this@LegacyBattery.plugged
        status = this@LegacyBattery.status
    }.build()
}

data class LegacyCall(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val number: String,
    val type: String,
    val duration: Long,
    val presentation: String,
    @Json(name = "data_usage")
    val dataUsage: Long,
    val contact: String,
    @Json(name = "times_contacted")
    val timesContacted: Int,
    @Json(name = "is_starred")
    val isStarred: Int,
    @Json(name = "is_pinned")
    val isPinned: Int
) : ToProto<DatumProtos.CallLog>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.CallLog = DatumProtos.CallLog.newBuilder().apply {
        duration = this@LegacyCall.duration
        number = this@LegacyCall.number
        type = this@LegacyCall.type
        presentation = this@LegacyCall.presentation
        dataUsage = this@LegacyCall.dataUsage
        contactType = this@LegacyCall.contact
        isStarred = this@LegacyCall.isStarred == 1
        isPinned = this@LegacyCall.isPinned == 1
    }.build()
}

data class LegacyDataTraffic(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    @Json(name = "rx_kb")
    val rxKb: Long,
    @Json(name = "tx_kb")
    val txKb: Long
) : ToProto<DatumProtos.DataTraffic>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.DataTraffic = DatumProtos.DataTraffic.newBuilder().apply {
        rxBytes = this@LegacyDataTraffic.rxKb * 1000
        txBytes = this@LegacyDataTraffic.txKb * 1000
        fromTime = timestamp - 15000
        toTime = timestamp
    }.build()
}

data class LegacyDeviceEvent(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val type: String
) : ToProto<DatumProtos.DeviceEvent>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.DeviceEvent = DatumProtos.DeviceEvent.newBuilder().apply {
        type = when (this@LegacyDeviceEvent.type) {
            "ACTIVATE_AIRPLANE_MODE" -> "AIRPLANE_MODE_CHANGED_ACTIVATED"
            "DEACTIVATE_AIRPLANE_MODE" -> "AIRPLANE_MODE_CHANGED_DEACTIVATE"
            "TURN_OFF_DEVICE" -> "SHUTDOWN"
            "ACTIVATE_POWER_SAVE_MODE" -> "POWER_SAVE_MODE_CHANGED_ACTIVATED"
            "DEACTIVATE_POWER_SAVE_MODE" -> "POWER_SAVE_MODE_CHANGED_DEACTIVATED"
            "HEADSET_MIC_UNPLUGGED" -> "HEADSET_PLUG_UNPLUGGED"
            "HEADSET_UNPLUGGED" -> "HEADSET_PLUG_UNPLUGGED"
            "RINGER_MODE_NORMAL" -> "RINGER_MODE_CHANGED_NORMAL"
            "RINGER_MODE_SILENT" -> "RINGER_MODE_CHANGED_SILENT"
            "RINGER_MODE_VIBRATE" -> "RINGER_MODE_CHANGED_VIBRATE"
            "TURN_ON_DEVICE" -> "BOOT_COMPLETED"
            "UNLOCK" -> "USER_UNLOCKED"
            else -> this@LegacyDeviceEvent.type
        }
    }.build()
}

data class LegacyLocation(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float
) : ToProto<DatumProtos.Location>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.Location = DatumProtos.Location.newBuilder().apply {
        latitude = this@LegacyLocation.latitude
        longitude = this@LegacyLocation.longitude
        altitude = this@LegacyLocation.altitude
        accuracy = this@LegacyLocation.accuracy
        speed = this@LegacyLocation.speed
    }.build()
}

data class LegacyMedia(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    @Json(name = "mime_type")
    val mimeType: String
) : ToProto<DatumProtos.Media>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.Media = DatumProtos.Media.newBuilder().apply {
        mimeType = this@LegacyMedia.mimeType
    }.build()
}


data class LegacyMessage(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val number: String,
    @Json(name = "message_class")
    val messageClass: String,
    @Json(name = "message_box")
    val messageBox: String,
    val contact: String,
    @Json(name = "times_contacted")
    val timesContacted: Int,
    @Json(name = "is_starred")
    val isStarred: Int,
    @Json(name = "is_pinned")
    val isPinned: Int
) : ToProto<DatumProtos.Message>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.Message = DatumProtos.Message.newBuilder().apply {
        number = this@LegacyMessage.number
        messageClass = this@LegacyMessage.messageClass
        messageBox = this@LegacyMessage.messageBox
        contactType = this@LegacyMessage.contact
        isStarred = this@LegacyMessage.isStarred == 1
        isPinned = this@LegacyMessage.isPinned == 1
    }.build()
}

data class LegacyConnectivity(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    @Json(name = "is_connected")
    val isConnected: Int,
    val type: String
) : ToProto<DatumProtos.DeviceEvent>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.DeviceEvent = DatumProtos.DeviceEvent.newBuilder().apply {
        type = if (isConnected == 1) "CONNECTIVITY_AVAILABLE_${this@LegacyConnectivity.type}" else "CONNECTIVITY_LOST_${this@LegacyConnectivity.type}"
    }.build()
}

data class LegacyNotification(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val name: String? = null,
    @Json(name = "package_name")
    val packageName: String,
    @Json(name = "is_system_app")
    val isSystemApp: Int,
    @Json(name = "is_updated_system_app")
    val isUpdatedSystemApp: Int,
    val key: String,
    @Json(name = "is_posted")
    val isPosted: Int,
    val title: String,
    val visibility: String,
    val category: String
) : ToProto<DatumProtos.Notification>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.Notification = DatumProtos.Notification.newBuilder().apply {
        name = this@LegacyNotification.name
        packageName = this@LegacyNotification.packageName
        isSystemApp = this@LegacyNotification.isSystemApp == 1
        isUpdatedSystemApp = this@LegacyNotification.isUpdatedSystemApp == 1
        key = this@LegacyNotification.key
        title = this@LegacyNotification.title
        isPosted = this@LegacyNotification.isPosted == 1
        visibility = this@LegacyNotification.visibility
        category = this@LegacyNotification.category
    }.build()
}


data class LegacyWifi(
    override val timestamp: Long,
    override val pid: String,
    @Json(name = "source_info")
    override val sourceInfo: String,
    val bssid: String? = null,
    val frequency: Int,
    val rssi: Int,
) : ToProto<DatumProtos.Wifi>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.Wifi = DatumProtos.Wifi.getDefaultInstance()

    companion object {
        fun toProto(data: List<LegacyWifi>) = DatumProtos.Wifi.newBuilder().apply {
            addAllAccessPoint(
                data.map {
                    DatumProtos.Wifi.AccessPoint.newBuilder().apply {
                        bssid = it.bssid
                        frequency = it.frequency
                        rssi = it.rssi
                    }.build()
                }
            )
        }
    }
}

data class LegacyFitbitData(
    val hashedEmail: String,
    override val timestamp: Long,
    override val pid: String = hashedEmail,
    @Json(name = "source_info")
    override val sourceInfo: String = "",
    val deviceType: String,
    val valueType: String,
    val identifier: String,
    val valueFormat: String,
    val valueUnit: String,
    val value: List<String>,
    val others: Map<String, String>,
) : ToProto<DatumProtos.ExternalSensor>(
    timestamp, pid, sourceInfo
) {
    override fun toProto(): DatumProtos.ExternalSensor = DatumProtos.ExternalSensor.newBuilder().apply {
        deviceType = this@LegacyFitbitData.deviceType
        valueType = this@LegacyFitbitData.valueType
        identifier = this@LegacyFitbitData.identifier
        valueFormat = this@LegacyFitbitData.valueFormat
        valueUnit = this@LegacyFitbitData.valueUnit
        addAllValue(this@LegacyFitbitData.value)
        putAllOthers(this@LegacyFitbitData.others)
    }.build()
}