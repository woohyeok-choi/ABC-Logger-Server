package kaist.iclab.abclogger

import kaist.iclab.abclogger.grpc.proto.DataProtos
import kotlinx.serialization.Serializable

interface ToProto<T : ProtoMessage> {
    fun toProto(): T
}

interface ToObject<T : ProtoMessage, V : Any> {
    fun toObject(proto: T): V
}


@Serializable
abstract class Value(val dataType: String)

@Serializable
data class Datum(
        val timestamp: Long? = null,
        val utcOffset: Float? = null,
        val email: String? = null,
        val deviceInfo: String? = null,
        val deviceId: String? = null,
        val uploadTime: Long? = null,
        val value: Value? = null
): ToProto<DataProtos.Datum> {
    override fun toProto(): DataProtos.Datum =
            DataProtos.Datum.newBuilder().apply {
                timestamp = this@Datum.timestamp ?: Long.MIN_VALUE
                utcOffset = this@Datum.utcOffset ?: Float.NaN
                subjectEmail = this@Datum.email ?: ""
                deviceInfo = this@Datum.deviceInfo ?: ""
                deviceId = this@Datum.deviceId ?: ""
                uploadTime = this@Datum.uploadTime ?: Long.MIN_VALUE

                when (value) {
                    is PhysicalActivityTransition -> physicalActivityTransition = this@Datum.value.toProto()
                    is PhysicalActivity -> physicalActivity = this@Datum.value.toProto()
                    is AppUsageEvent -> appUsageEvent = this@Datum.value.toProto()
                    is Battery -> battery = this@Datum.value.toProto()
                    is Bluetooth -> bluetooth = this@Datum.value.toProto()
                    is CallLog -> callLog = this@Datum.value.toProto()
                    is DeviceEvent -> deviceEvent = this@Datum.value.toProto()
                    is Sensor -> sensor = this@Datum.value.toProto()
                    is InstalledApp -> installedApp = this@Datum.value.toProto()
                    is KeyLog -> keyLog = this@Datum.value.toProto()
                    is Location -> location = this@Datum.value.toProto()
                    is Media -> media = this@Datum.value.toProto()
                    is Message -> message = this@Datum.value.toProto()
                    is Notification -> notification = this@Datum.value.toProto()
                    is PhysicalStat -> physicalStat = this@Datum.value.toProto()
                    is Survey -> survey = this@Datum.value.toProto()
                    is DataTraffic -> dataTraffic = this@Datum.value.toProto()
                    is Wifi -> wifi = this@Datum.value.toProto()
                }
            }.build()

    companion object : ToObject<DataProtos.Datum, Datum> {
        override fun toObject(proto: DataProtos.Datum): Datum =
                Datum(
                        timestamp = proto.timestamp,
                        utcOffset = proto.utcOffset,
                        email = proto.subjectEmail
                        type = proto.type,
                        isEntered = proto.isEntered
                )
    }
}

@Serializable
data class PhysicalActivityTransition(
        val type: String? = null,
        val isEntered: Boolean? = null
) : Value("physical_activity_transition"), ToProto<DataProtos.PhysicalActivityTransition> {
    override fun toProto(): DataProtos.PhysicalActivityTransition =
            DataProtos.PhysicalActivityTransition.newBuilder().apply {
                type = this@PhysicalActivityTransition.type ?: ""
                isEntered = this@PhysicalActivityTransition.isEntered ?: false
            }.build()

    companion object : ToObject<DataProtos.PhysicalActivityTransition, PhysicalActivityTransition> {
        override fun toObject(proto: DataProtos.PhysicalActivityTransition): PhysicalActivityTransition =
                PhysicalActivityTransition(
                        type = proto.type,
                        isEntered = proto.isEntered
                )
    }
}


@Serializable
data class PhysicalActivity(
        val type: String? = null,
        val confidence: Int? = null
) : Value("physical_activity"), ToProto<DataProtos.PhysicalActivity> {
    override fun toProto(): DataProtos.PhysicalActivity =
            DataProtos.PhysicalActivity.newBuilder().apply {
                type = this@PhysicalActivity.type ?: ""
                confidence = this@PhysicalActivity.confidence ?: Int.MIN_VALUE
            }.build()

    companion object : ToObject<DataProtos.PhysicalActivity, PhysicalActivity> {
        override fun toObject(proto: DataProtos.PhysicalActivity): PhysicalActivity =
                PhysicalActivity(
                        type = proto.type,
                        confidence = proto.confidence
                )
    }
}


@Serializable
data class AppUsageEvent(
        val name: String? = null,
        val packageName: String? = null,
        val type: String? = null,
        val isSystemApp: Boolean? = null,
        val isUpdatedSystemApp: Boolean? = null
) : Value("app_usage_event"), ToProto<DataProtos.AppUsageEvent> {
    override fun toProto(): DataProtos.AppUsageEvent =
            DataProtos.AppUsageEvent.newBuilder().apply {
                name = this@AppUsageEvent.name ?: ""
                packageName = this@AppUsageEvent.packageName ?: ""
                type = this@AppUsageEvent.type ?: ""
                isSystemApp = this@AppUsageEvent.isSystemApp ?: false
                isUpdatedSystemApp = this@AppUsageEvent.isUpdatedSystemApp ?: false
            }.build()

    companion object : ToObject<DataProtos.AppUsageEvent, AppUsageEvent> {
        override fun toObject(proto: DataProtos.AppUsageEvent): AppUsageEvent =
                AppUsageEvent(
                        name = proto.name,
                        packageName = proto.packageName,
                        type = proto.type,
                        isSystemApp = proto.isSystemApp,
                        isUpdatedSystemApp = proto.isUpdatedSystemApp
                )
    }
}

@Serializable
data class Battery(
        val level: Int? = null,
        val scale: Int? = null,
        val temperature: Int? = null,
        val voltage: Int? = null,
        val health: String? = null,
        val pluggedType: String? = null,
        val status: String? = null
) : Value("battery"), ToProto<DataProtos.Battery> {
    override fun toProto(): DataProtos.Battery =
            DataProtos.Battery.newBuilder().apply {
                level = this@Battery.level ?: Int.MIN_VALUE
                scale = this@Battery.scale ?: Int.MIN_VALUE
                temperature = this@Battery.temperature ?: Int.MIN_VALUE
                voltage = this@Battery.voltage ?: Int.MIN_VALUE
                health = this@Battery.health ?: ""
                pluggedType = this@Battery.pluggedType ?: ""
                status = this@Battery.status ?: ""
            }.build()

    companion object : ToObject<DataProtos.Battery, Battery> {
        override fun toObject(proto: DataProtos.Battery): Battery =
                Battery(
                        level = proto.level,
                        scale = proto.scale,
                        temperature = proto.temperature,
                        voltage = proto.voltage,
                        health = proto.health,
                        pluggedType = proto.pluggedType,
                        status = proto.status
                )
    }
}

@Serializable
data class Bluetooth(
        val deviceName: String? = null,
        val address: String? = null,
        val rssi: Int? = null
) : Value("bluetooth"), ToProto<DataProtos.Bluetooth> {
    override fun toProto(): DataProtos.Bluetooth =
            DataProtos.Bluetooth.newBuilder().apply {
                deviceName = this@Bluetooth.deviceName ?: ""
                address = this@Bluetooth.address ?: ""
                rssi = this@Bluetooth.rssi ?: Int.MIN_VALUE
            }.build()

    companion object : ToObject<DataProtos.Bluetooth, Bluetooth> {
        override fun toObject(proto: DataProtos.Bluetooth): Bluetooth =
                Bluetooth(
                        deviceName = proto.deviceName,
                        address = proto.address,
                        rssi = proto.rssi
                )
    }
}

@Serializable
data class CallLog(
        val duration: Long? = null,
        val number: String? = null,
        val type: String? = null,
        val dataUsage: Long? = null,
        val presentation: String? = null,
        val contactType: String? = null,
        val isStarred: Boolean? = null,
        val isPinned: Boolean? = null
) : Value("call_log"), ToProto<DataProtos.CallLog> {
    override fun toProto(): DataProtos.CallLog =
            DataProtos.CallLog.newBuilder().apply {
                duration = this@CallLog.duration ?: Long.MIN_VALUE
                number = this@CallLog.number ?: ""
                type = this@CallLog.type ?: ""
                dataUsage = this@CallLog.dataUsage ?: Long.MIN_VALUE
                presentation = this@CallLog.presentation ?: ""
                contactType = this@CallLog.contactType ?: ""
                isStarred = this@CallLog.isStarred ?: false
                isPinned = this@CallLog.isPinned ?: false
            }.build()

    companion object : ToObject<DataProtos.CallLog, CallLog> {
        override fun toObject(proto: DataProtos.CallLog): CallLog =
                CallLog(
                        duration = proto.duration,
                        number = proto.number,
                        type = proto.type,
                        dataUsage = proto.dataUsage,
                        presentation = proto.presentation,
                        contactType = proto.contactType,
                        isStarred = proto.isStarred,
                        isPinned = proto.isPinned
                )
    }
}

@Serializable
data class DeviceEvent(
        val type: String? = null
) : Value("device_event"), ToProto<DataProtos.DeviceEvent> {
    override fun toProto(): DataProtos.DeviceEvent =
            DataProtos.DeviceEvent.newBuilder().apply {
                type = this@DeviceEvent.type ?: ""
            }.build()

    companion object : ToObject<DataProtos.DeviceEvent, DeviceEvent> {
        override fun toObject(proto: DataProtos.DeviceEvent): DeviceEvent =
                DeviceEvent(
                        type = proto.type
                )
    }
}

@Serializable
data class Sensor(
        val sensorId: String? = null,
        val name: String? = null,
        val type: String? = null,
        val description: String? = null,
        val values: List<String> = emptyList()
) : Value("sensor"), ToProto<DataProtos.Sensor> {
    override fun toProto(): DataProtos.Sensor =
            DataProtos.Sensor.newBuilder().apply {
                sensorId = this@Sensor.sensorId ?: ""
                name = this@Sensor.name ?: ""
                type = this@Sensor.type ?: ""
                description = this@Sensor.description ?: ""
                addAllValues(this@Sensor.values)
            }.build()

    companion object : ToObject<DataProtos.Sensor, Sensor> {
        override fun toObject(proto: DataProtos.Sensor): Sensor =
                Sensor(
                        sensorId = proto.sensorId,
                        name = proto.name,
                        type = proto.type,
                        description = proto.description,
                        values = proto.valuesList
                )
    }
}

@Serializable
data class InstalledApp(
        val name: String? = null,
        val packageName: String? = null,
        val isSystemApp: Boolean? = null,
        val isUpdatedSystemApp: Boolean? = null,
        val firstInstallTime: Long? = null,
        val lastUpdateTime: Long? = null
) : Value("installed_app"), ToProto<DataProtos.InstalledApp> {
    override fun toProto(): DataProtos.InstalledApp =
            DataProtos.InstalledApp.newBuilder().apply {
                name = this@InstalledApp.name ?: ""
                packageName = this@InstalledApp.packageName ?: ""
                isSystemApp = this@InstalledApp.isSystemApp ?: false
                isUpdatedSystemApp = this@InstalledApp.isUpdatedSystemApp ?: false
                firstInstallTime = this@InstalledApp.firstInstallTime ?: Long.MIN_VALUE
                lastUpdateTime = this@InstalledApp.lastUpdateTime ?: Long.MAX_VALUE
            }.build()

    companion object : ToObject<DataProtos.InstalledApp, InstalledApp> {
        override fun toObject(proto: DataProtos.InstalledApp): InstalledApp =
                InstalledApp(
                        name = proto.name,
                        packageName = proto.packageName,
                        isSystemApp = proto.isSystemApp,
                        isUpdatedSystemApp = proto.isUpdatedSystemApp,
                        firstInstallTime = proto.firstInstallTime,
                        lastUpdateTime = proto.lastUpdateTime
                )
    }
}

@Serializable
data class KeyLog(
        val name: String? = null,
        val packageName: String? = null,
        val isSystemApp: Boolean? = null,
        val isUpdatedSystemApp: Boolean? = null,
        val distance: Float? = null,
        val timeTaken: Long? = null,
        val keyboardType: String? = null,
        val prevKey: String? = null,
        val currentKey: String? = null,
        val prevKeyType: String? = null,
        val currentKeyType: String? = null
) : Value("key_log"), ToProto<DataProtos.KeyLog> {
    override fun toProto(): DataProtos.KeyLog =
            DataProtos.KeyLog.newBuilder().apply {
                name = this@KeyLog.name ?: ""
                packageName = this@KeyLog.packageName ?: ""
                isSystemApp = this@KeyLog.isSystemApp ?: false
                isUpdatedSystemApp = this@KeyLog.isUpdatedSystemApp ?: false
                distance = this@KeyLog.distance ?: Float.NaN
                timeTaken = this@KeyLog.timeTaken ?: Long.MIN_VALUE
                keyboardType = this@KeyLog.keyboardType ?: ""
                prevKey = this@KeyLog.prevKey ?: ""
                currentKey = this@KeyLog.currentKey ?: ""
                prevKeyType = this@KeyLog.prevKeyType ?: ""
                currentKeyType = this@KeyLog.currentKeyType ?: ""
            }.build()

    companion object : ToObject<DataProtos.KeyLog, KeyLog> {
        override fun toObject(proto: DataProtos.KeyLog): KeyLog =
                KeyLog(
                        name = proto.name,
                        packageName = proto.packageName,
                        isSystemApp = proto.isSystemApp,
                        isUpdatedSystemApp = proto.isUpdatedSystemApp,
                        distance = proto.distance,
                        timeTaken = proto.timeTaken,
                        keyboardType = proto.keyboardType,
                        prevKey = proto.prevKey,
                        currentKey = proto.currentKey,
                        prevKeyType = proto.prevKeyType,
                        currentKeyType = proto.currentKeyType
                )
    }
}

@Serializable
data class Location(
        val latitude: Double? = null,
        val longitude: Double? = null,
        val altitude: Double? = null,
        val accuracy: Float? = null,
        val speed: Float? = null
) : Value("location"), ToProto<DataProtos.Location> {
    override fun toProto(): DataProtos.Location =
            DataProtos.Location.newBuilder().apply {
                latitude = this@Location.latitude ?: Double.NaN
                longitude = this@Location.longitude ?: Double.NaN
                altitude = this@Location.altitude ?: Double.NaN
                accuracy = this@Location.accuracy ?: Float.NaN
                speed = this@Location.speed ?: Float.NaN
            }.build()

    companion object : ToObject<DataProtos.Location, Location> {
        override fun toObject(proto: DataProtos.Location): Location =
                Location(
                        latitude = proto.latitude,
                        longitude = proto.longitude,
                        altitude = proto.altitude,
                        accuracy = proto.accuracy,
                        speed = proto.speed
                )
    }
}

@Serializable
data class Media(
        val mimeType: String? = null
) : Value("media"), ToProto<DataProtos.Media> {
    override fun toProto(): DataProtos.Media =
            DataProtos.Media.newBuilder().apply {
                mimeType = this@Media.mimeType ?: ""
            }.build()

    companion object : ToObject<DataProtos.Media, Media> {
        override fun toObject(proto: DataProtos.Media): Media =
                Media(
                        mimeType = proto.mimeType
                )
    }
}

@Serializable
data class Message(
        val number: String? = null,
        val messageClass: String? = null,
        val messageBox: String? = null,
        val contactType: String? = null,
        val isStarred: Boolean? = null,
        val isPinned: Boolean? = null
) : Value("message"), ToProto<DataProtos.Message> {
    override fun toProto(): DataProtos.Message =
            DataProtos.Message.newBuilder().apply {
                number = this@Message.number ?: ""
                messageClass = this@Message.messageClass ?: ""
                messageBox = this@Message.messageBox ?: ""
                contactType = this@Message.contactType ?: ""
                isStarred = this@Message.isStarred ?: false
                isPinned = this@Message.isPinned ?: false
            }.build()

    companion object : ToObject<DataProtos.Message, Message> {
        override fun toObject(proto: DataProtos.Message): Message =
                Message(
                        number = proto.number,
                        messageClass = proto.messageClass,
                        messageBox = proto.messageBox,
                        contactType = proto.contactType,
                        isStarred = proto.isStarred,
                        isPinned = proto.isPinned
                )
    }
}

@Serializable
data class Notification(
        val name: String? = null,
        val packageName: String? = null,
        val isSystemApp: Boolean? = null,
        val isUpdatedSystemApp: Boolean? = null,
        val title: String? = null,
        val visibility: String? = null,
        val category: String? = null,
        val vibrate: String? = null,
        val sound: String? = null,
        val lightColor: String? = null,
        val isPosted: Boolean? = null
) : Value("notification"), ToProto<DataProtos.Notification> {
    override fun toProto(): DataProtos.Notification =
            DataProtos.Notification.newBuilder().apply {
                name = this@Notification.name ?: ""
                packageName = this@Notification.packageName ?: ""
                isSystemApp = this@Notification.isSystemApp ?: false
                isUpdatedSystemApp = this@Notification.isUpdatedSystemApp ?: false
                title = this@Notification.title ?: ""
                visibility = this@Notification.visibility ?: ""
                category = this@Notification.category ?: ""
                vibrate = this@Notification.vibrate ?: ""
                sound = this@Notification.sound ?: ""
                lightColor = this@Notification.lightColor ?: ""
                isPosted = this@Notification.isPosted ?: false
            }.build()

    companion object : ToObject<DataProtos.Notification, Notification> {
        override fun toObject(proto: DataProtos.Notification): Notification =
                Notification(
                        name = proto.name,
                        packageName = proto.packageName,
                        isSystemApp = proto.isSystemApp,
                        isUpdatedSystemApp = proto.isUpdatedSystemApp,
                        title = proto.title,
                        visibility = proto.visibility,
                        category = proto.category,
                        vibrate = proto.vibrate,
                        sound = proto.sound,
                        lightColor = proto.lightColor,
                        isPosted = proto.isPosted
                )
    }
}

@Serializable
data class PhysicalStat(
        val type: String? = null,
        val startTime: Long? = null,
        val endTime: Long? = null,
        val value: Float? = null
) : Value("physical_stat"), ToProto<DataProtos.PhysicalStat> {
    override fun toProto(): DataProtos.PhysicalStat =
            DataProtos.PhysicalStat.newBuilder().apply {
                type = this@PhysicalStat.type ?: ""
                startTime = this@PhysicalStat.startTime ?: Long.MIN_VALUE
                endTime = this@PhysicalStat.endTime ?: Long.MIN_VALUE
                value = this@PhysicalStat.value ?: Float.NaN
            }.build()

    companion object : ToObject<DataProtos.PhysicalStat, PhysicalStat> {
        override fun toObject(proto: DataProtos.PhysicalStat): PhysicalStat =
                PhysicalStat(
                        type = proto.type,
                        startTime = proto.startTime,
                        endTime = proto.endTime,
                        value = proto.value
                )
    }
}


@Serializable
data class Survey(
        val title: String? = null,
        val message: String? = null,
        val timeoutPolicy: String? = null,
        val timeoutSec: Long? = null,
        val deliveredTime: Long? = null,
        val reactionTime: Long? = null,
        val responseTime: Long? = null,
        val items: List<Item> = emptyList()
) : Value("survey"), ToProto<DataProtos.Survey> {
    @Serializable
    data class Item(
            val question: String? = null,
            val response: String? = null
    )

    override fun toProto(): DataProtos.Survey =
            DataProtos.Survey.newBuilder().apply {
                title = this@Survey.title ?: ""
                message = this@Survey.message ?: ""
                timeoutPolicy = this@Survey.timeoutPolicy ?: ""
                timeoutSec = this@Survey.timeoutSec ?: Long.MIN_VALUE
                deliveredTime = this@Survey.deliveredTime ?: Long.MIN_VALUE
                reactionTime = this@Survey.reactionTime ?: Long.MIN_VALUE
                responseTime = this@Survey.responseTime ?: Long.MIN_VALUE
                addAllItems(this@Survey.items.map {
                    DataProtos.Survey.Item.newBuilder().apply {
                        question = it.question
                        response = it.response
                    }.build()
                })
            }.build()

    companion object : ToObject<DataProtos.Survey, Survey> {
        override fun toObject(proto: DataProtos.Survey): Survey =
                Survey(
                        title = proto.title,
                        message = proto.message,
                        timeoutPolicy = proto.timeoutPolicy,
                        timeoutSec = proto.timeoutSec,
                        deliveredTime = proto.deliveredTime,
                        reactionTime = proto.reactionTime,
                        responseTime = proto.responseTime,
                        items = proto.itemsList.map {
                            Item(
                                    question = it.question,
                                    response = it.response
                            )
                        }
                )
    }
}

@Serializable
data class DataTraffic(
        val fromTime: Long? = null,
        val toTime: Long? = null,
        val rxBytes: Long? = null,
        val txBytes: Long? = null,
        val mobileRxBytes: Long? = null,
        val mobileTxBytes: Long? = null
) : Value("data_traffic"), ToProto<DataProtos.DataTraffic> {
    override fun toProto(): DataProtos.DataTraffic =
            DataProtos.DataTraffic.newBuilder().apply {
                fromTime = this@DataTraffic.fromTime ?: Long.MIN_VALUE
                toTime = this@DataTraffic.toTime ?: Long.MIN_VALUE
                rxBytes = this@DataTraffic.rxBytes ?: Long.MIN_VALUE
                txBytes = this@DataTraffic.txBytes ?: Long.MIN_VALUE
                mobileRxBytes = this@DataTraffic.mobileRxBytes ?: Long.MIN_VALUE
                mobileTxBytes = this@DataTraffic.mobileTxBytes ?: Long.MIN_VALUE
            }.build()

    companion object : ToObject<DataProtos.DataTraffic, DataTraffic> {
        override fun toObject(proto: DataProtos.DataTraffic): DataTraffic =
                DataTraffic(
                        fromTime = proto.fromTime,
                        toTime = proto.toTime,
                        rxBytes = proto.rxBytes,
                        txBytes = proto.txBytes,
                        mobileRxBytes = proto.mobileRxBytes,
                        mobileTxBytes = proto.mobileTxBytes
                )
    }
}

@Serializable
data class Wifi(
        val bssid: String? = null,
        val ssid: String? = null,
        val frequency: Int? = null,
        val rssi: Int? = null
) : Value("wifi"), ToProto<DataProtos.Wifi> {
    override fun toProto(): DataProtos.Wifi =
            DataProtos.Wifi.newBuilder().apply {
                bssid = this@Wifi.bssid ?: ""
                ssid = this@Wifi.ssid ?: ""
                frequency = this@Wifi.frequency ?: Int.MIN_VALUE
                rssi = this@Wifi.rssi ?: Int.MIN_VALUE
            }.build()

    companion object : ToObject<DataProtos.Wifi, Wifi> {
        override fun toObject(proto: DataProtos.Wifi): Wifi =
                Wifi(
                        bssid = proto.bssid,
                        ssid = proto.ssid,
                        frequency = proto.frequency,
                        rssi = proto.rssi
                )
    }
}
