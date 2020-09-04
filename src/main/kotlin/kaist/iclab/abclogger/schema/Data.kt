package kaist.iclab.abclogger.schema

import com.google.protobuf.Descriptors
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.DataProtos
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

@Polymorphic
@Serializable
open class Value

@Serializable
data class Datum(
        val timestamp: Long? = null,
        val utcOffsetSec: Int? = null,
        @ContextualSerialization
        val offsetDateTime: OffsetDateTime? = null,
        val email: String? = null,
        val deviceInfo: String? = null,
        val deviceId: String? = null,
        val uploadTime: Long? = null,
        @ContextualSerialization
        val offsetUploadDateTime: OffsetDateTime? = null,
        val value: Value? = null,
        val dataType: String? = null
) {
    companion object : ProtoSerializer<Datum, DataProtos.Datum> {
        private fun dataCaseToDataType(dataCase: DataProtos.Datum.DataCase): CommonProtos.DataType =
                when (dataCase) {
                    DataProtos.Datum.DataCase.PHYSICAL_ACTIVITY_TRANSITION -> CommonProtos.DataType.PHYSICAL_ACTIVITY_TRANSITION
                    DataProtos.Datum.DataCase.PHYSICAL_ACTIVITY -> CommonProtos.DataType.PHYSICAL_ACTIVITY
                    DataProtos.Datum.DataCase.APP_USAGE_EVENT -> CommonProtos.DataType.APP_USAGE_EVENT
                    DataProtos.Datum.DataCase.BATTERY -> CommonProtos.DataType.BATTERY
                    DataProtos.Datum.DataCase.BLUETOOTH -> CommonProtos.DataType.BLUETOOTH
                    DataProtos.Datum.DataCase.CALL_LOG -> CommonProtos.DataType.CALL_LOG
                    DataProtos.Datum.DataCase.DEVICE_EVENT -> CommonProtos.DataType.DEVICE_EVENT
                    DataProtos.Datum.DataCase.SENSOR -> CommonProtos.DataType.SENSOR
                    DataProtos.Datum.DataCase.INSTALLED_APP -> CommonProtos.DataType.INSTALLED_APP
                    DataProtos.Datum.DataCase.KEY_LOG -> CommonProtos.DataType.KEY_LOG
                    DataProtos.Datum.DataCase.LOCATION -> CommonProtos.DataType.LOCATION
                    DataProtos.Datum.DataCase.MEDIA -> CommonProtos.DataType.MEDIA
                    DataProtos.Datum.DataCase.MESSAGE -> CommonProtos.DataType.MESSAGE
                    DataProtos.Datum.DataCase.NOTIFICATION -> CommonProtos.DataType.NOTIFICATION
                    DataProtos.Datum.DataCase.PHYSICAL_STAT -> CommonProtos.DataType.PHYSICAL_STAT
                    DataProtos.Datum.DataCase.SURVEY -> CommonProtos.DataType.SURVEY
                    DataProtos.Datum.DataCase.DATA_TRAFFIC -> CommonProtos.DataType.DATA_TRAFFIC
                    DataProtos.Datum.DataCase.WIFI -> CommonProtos.DataType.WIFI
                    else -> CommonProtos.DataType.NOT_SPECIFIED
                }

        override fun toProto(o: Datum): DataProtos.Datum =
                with(o) {
                    DataProtos.Datum.newBuilder().apply {
                        timestamp = this@with.timestamp ?: Long.MIN_VALUE
                        utcOffsetSec = this@with.utcOffsetSec ?: Int.MIN_VALUE
                        email = this@with.email ?: ""
                        deviceInfo = this@with.deviceInfo ?: ""
                        deviceId = this@with.deviceId ?: ""
                        uploadTime = this@with.uploadTime ?: Long.MIN_VALUE

                        when (value) {
                            is PhysicalActivityTransition -> physicalActivityTransition = PhysicalActivityTransition.toProto(value)
                            is PhysicalActivity -> physicalActivity = PhysicalActivity.toProto(value)
                            is AppUsageEvent -> appUsageEvent = AppUsageEvent.toProto(value)
                            is Battery -> battery = Battery.toProto(value)
                            is Bluetooth -> bluetooth = Bluetooth.toProto(value)
                            is CallLog -> callLog = CallLog.toProto(value)
                            is DeviceEvent -> deviceEvent = DeviceEvent.toProto(value)
                            is Sensor -> sensor = Sensor.toProto(value)
                            is InstalledApp -> installedApp = InstalledApp.toProto(value)
                            is KeyLog -> keyLog = KeyLog.toProto(value)
                            is Location -> location = Location.toProto(value)
                            is Media -> media = Media.toProto(value)
                            is Message -> message = Message.toProto(value)
                            is Notification -> notification = Notification.toProto(value)
                            is PhysicalStat -> physicalStat = PhysicalStat.toProto(value)
                            is Survey -> survey = Survey.toProto(value)
                            is DataTraffic -> dataTraffic = DataTraffic.toProto(value)
                            is Wifi -> wifi = Wifi.toProto(value)
                        }
                    }.build()
                }

        override fun toObject(p: DataProtos.Datum): Datum =
                with(p) {
                    Datum(
                            timestamp = timestamp,
                            utcOffsetSec = utcOffsetSec,
                            email = email,
                            deviceInfo = deviceInfo,
                            deviceId = deviceId,
                            uploadTime = uploadTime,
                            dataType = dataCaseToDataType(p.dataCase).name,
                            value = when {
                                hasPhysicalActivityTransition() -> PhysicalActivityTransition.toObject(physicalActivityTransition)
                                hasPhysicalActivity() -> PhysicalActivity.toObject(physicalActivity)
                                hasAppUsageEvent() -> AppUsageEvent.toObject(appUsageEvent)
                                hasBattery() -> Battery.toObject(battery)
                                hasBluetooth() -> Bluetooth.toObject(bluetooth)
                                hasCallLog() -> CallLog.toObject(callLog)
                                hasDeviceEvent() -> DeviceEvent.toObject(deviceEvent)
                                hasSensor() -> Sensor.toObject(sensor)
                                hasInstalledApp() -> InstalledApp.toObject(installedApp)
                                hasKeyLog() -> KeyLog.toObject(keyLog)
                                hasLocation() -> Location.toObject(location)
                                hasMedia() -> Media.toObject(media)
                                hasMessage() -> Message.toObject(message)
                                hasNotification() -> Notification.toObject(notification)
                                hasPhysicalStat() -> PhysicalStat.toObject(physicalStat)
                                hasSurvey() -> Survey.toObject(survey)
                                hasDataTraffic() -> DataTraffic.toObject(dataTraffic)
                                hasWifi() -> Wifi.toObject(wifi)
                                else -> null
                            }
                    )
                }

    }
}


@Serializable
data class PhysicalActivityTransition(
        val type: String? = null,
        val isEntered: Boolean? = null
) : Value() {
    companion object : ProtoSerializer<PhysicalActivityTransition, DataProtos.PhysicalActivityTransition> {
        override fun toProto(o: PhysicalActivityTransition): DataProtos.PhysicalActivityTransition =
                with(o) {
                    DataProtos.PhysicalActivityTransition.newBuilder().apply {
                        type = this@with.type ?: ""
                        isEntered = this@with.isEntered ?: false
                    }.build()
                }

        override fun toObject(p: DataProtos.PhysicalActivityTransition): PhysicalActivityTransition =
                with(p) {
                    PhysicalActivityTransition(
                            type = type,
                            isEntered = isEntered
                    )
                }
    }
}

@Serializable
data class PhysicalActivity(
        val type: String? = null,
        val confidence: Int? = null
) : Value() {
    companion object : ProtoSerializer<PhysicalActivity, DataProtos.PhysicalActivity> {
        override fun toProto(o: PhysicalActivity): DataProtos.PhysicalActivity =
                with(o) {
                    DataProtos.PhysicalActivity.newBuilder().apply {
                        type = this@with.type ?: ""
                        confidence = this@with.confidence ?: Int.MIN_VALUE
                    }.build()
                }

        override fun toObject(p: DataProtos.PhysicalActivity): PhysicalActivity =
                with(p) {
                    PhysicalActivity(
                            type = type,
                            confidence = confidence
                    )
                }

    }
}


@Serializable
data class AppUsageEvent(
        val name: String? = null,
        val packageName: String? = null,
        val type: String? = null,
        val isSystemApp: Boolean? = null,
        val isUpdatedSystemApp: Boolean? = null
) : Value() {
    companion object : ProtoSerializer<AppUsageEvent, DataProtos.AppUsageEvent> {
        override fun toProto(o: AppUsageEvent): DataProtos.AppUsageEvent =
                with(o) {
                    DataProtos.AppUsageEvent.newBuilder().apply {
                        name = this@with.name ?: ""
                        packageName = this@with.packageName ?: ""
                        type = this@with.type ?: ""
                        isSystemApp = this@with.isSystemApp ?: false
                        isUpdatedSystemApp = this@with.isUpdatedSystemApp ?: false
                    }.build()
                }

        override fun toObject(p: DataProtos.AppUsageEvent): AppUsageEvent =
                with(p) {
                    AppUsageEvent(
                            name = name,
                            packageName = packageName,
                            type = type,
                            isSystemApp = isSystemApp,
                            isUpdatedSystemApp = isUpdatedSystemApp
                    )
                }
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
) : Value() {
    companion object : ProtoSerializer<Battery, DataProtos.Battery> {
        override fun toProto(o: Battery): DataProtos.Battery =
                with(o) {
                    DataProtos.Battery.newBuilder().apply {
                        level = this@with.level ?: Int.MIN_VALUE
                        scale = this@with.scale ?: Int.MIN_VALUE
                        temperature = this@with.temperature ?: Int.MIN_VALUE
                        voltage = this@with.voltage ?: Int.MIN_VALUE
                        health = this@with.health ?: ""
                        pluggedType = this@with.pluggedType ?: ""
                        status = this@with.status ?: ""
                    }.build()
                }

        override fun toObject(p: DataProtos.Battery): Battery =
                with(p) {
                    Battery(
                            level = level,
                            scale = scale,
                            temperature = temperature,
                            voltage = voltage,
                            health = health,
                            pluggedType = pluggedType,
                            status = status
                    )
                }
    }
}

@Serializable
data class Bluetooth(
        val deviceName: String? = null,
        val address: String? = null,
        val rssi: Int? = null,
        val bondState: String? = null,
        val deviceType: String? = null,
        val isLowEnergy: Boolean? = null
) : Value() {
    companion object : ProtoSerializer<Bluetooth, DataProtos.Bluetooth> {
        override fun toProto(o: Bluetooth): DataProtos.Bluetooth =
                with(o) {
                    DataProtos.Bluetooth.newBuilder().apply {
                        deviceName = this@with.deviceName ?: ""
                        address = this@with.address ?: ""
                        rssi = this@with.rssi ?: Int.MIN_VALUE
                        bondState = this@with.bondState ?: ""
                        deviceType = this@with.deviceType ?: ""
                        isLowEnergy = this@with.isLowEnergy ?: false
                    }.build()
                }

        override fun toObject(p: DataProtos.Bluetooth): Bluetooth =
                with(p) {
                    Bluetooth(
                            deviceName = deviceName,
                            address = address,
                            rssi = rssi,
                            bondState = bondState,
                            deviceType = deviceType,
                            isLowEnergy = isLowEnergy
                    )
                }
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
) : Value() {
    companion object : ProtoSerializer<CallLog, DataProtos.CallLog> {
        override fun toProto(o: CallLog): DataProtos.CallLog =
                with(o) {
                    DataProtos.CallLog.newBuilder().apply {
                        duration = this@with.duration ?: Long.MIN_VALUE
                        number = this@with.number ?: ""
                        type = this@with.type ?: ""
                        dataUsage = this@with.dataUsage ?: Long.MIN_VALUE
                        presentation = this@with.presentation ?: ""
                        contactType = this@with.contactType ?: ""
                        isStarred = this@with.isStarred ?: false
                        isPinned = this@with.isPinned ?: false
                    }.build()
                }

        override fun toObject(p: DataProtos.CallLog): CallLog =
                with(p) {
                    CallLog(
                            duration = duration,
                            number = number,
                            type = type,
                            dataUsage = dataUsage,
                            presentation = presentation,
                            contactType = contactType,
                            isStarred = isStarred,
                            isPinned = isPinned
                    )
                }
    }
}

@Serializable
data class DeviceEvent(
        val type: String? = null
) : Value() {
    companion object : ProtoSerializer<DeviceEvent, DataProtos.DeviceEvent> {
        override fun toProto(o: DeviceEvent): DataProtos.DeviceEvent =
                with(o) {
                    DataProtos.DeviceEvent.newBuilder().apply {
                        type = this@with.type ?: ""
                    }.build()
                }

        override fun toObject(p: DataProtos.DeviceEvent): DeviceEvent =
                with(p) {
                    DeviceEvent(
                            type = type
                    )
                }
    }
}

@Serializable
data class Sensor(
        val sensorId: String? = null,
        val name: String? = null,
        val type: String? = null,
        val description: String? = null,
        val value: List<String> = emptyList()
) : Value() {
    companion object : ProtoSerializer<Sensor, DataProtos.Sensor> {
        override fun toProto(o: Sensor): DataProtos.Sensor =
                with(o) {
                    DataProtos.Sensor.newBuilder().apply {
                        sensorId = this@with.sensorId ?: ""
                        name = this@with.name ?: ""
                        type = this@with.type ?: ""
                        description = this@with.description ?: ""
                        addAllValue(this@with.value)
                    }.build()

                }

        override fun toObject(p: DataProtos.Sensor): Sensor =
                with(p) {
                    Sensor(
                            sensorId = sensorId,
                            name = name,
                            type = type,
                            description = description,
                            value = valueList
                    )
                }

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
) : Value() {


    companion object : ProtoSerializer<InstalledApp, DataProtos.InstalledApp> {
        override fun toProto(o: InstalledApp): DataProtos.InstalledApp =
                with(o) {
                    DataProtos.InstalledApp.newBuilder().apply {
                        name = this@with.name ?: ""
                        packageName = this@with.packageName ?: ""
                        isSystemApp = this@with.isSystemApp ?: false
                        isUpdatedSystemApp = this@with.isUpdatedSystemApp ?: false
                        firstInstallTime = this@with.firstInstallTime ?: Long.MIN_VALUE
                        lastUpdateTime = this@with.lastUpdateTime ?: Long.MIN_VALUE
                    }.build()

                }

        override fun toObject(p: DataProtos.InstalledApp): InstalledApp =
                with(p) {
                    InstalledApp(
                            name = name,
                            packageName = packageName,
                            isSystemApp = isSystemApp,
                            isUpdatedSystemApp = isUpdatedSystemApp,
                            firstInstallTime = firstInstallTime,
                            lastUpdateTime = lastUpdateTime
                    )
                }
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
) : Value() {
    companion object : ProtoSerializer<KeyLog, DataProtos.KeyLog> {
        override fun toProto(o: KeyLog): DataProtos.KeyLog =
                with(o) {
                    DataProtos.KeyLog.newBuilder().apply {
                        name = this@with.name ?: ""
                        packageName = this@with.packageName ?: ""
                        isSystemApp = this@with.isSystemApp ?: false
                        isUpdatedSystemApp = this@with.isUpdatedSystemApp ?: false
                        distance = this@with.distance ?: Float.NaN
                        timeTaken = this@with.timeTaken ?: Long.MIN_VALUE
                        keyboardType = this@with.keyboardType ?: ""
                        prevKey = this@with.prevKey ?: ""
                        currentKey = this@with.currentKey ?: ""
                        prevKeyType = this@with.prevKeyType ?: ""
                        currentKeyType = this@with.currentKeyType ?: ""
                    }.build()
                }

        override fun toObject(p: DataProtos.KeyLog): KeyLog =
                with(p) {
                    KeyLog(
                            name = name,
                            packageName = packageName,
                            isSystemApp = isSystemApp,
                            isUpdatedSystemApp = isUpdatedSystemApp,
                            distance = distance,
                            timeTaken = timeTaken,
                            keyboardType = keyboardType,
                            prevKey = prevKey,
                            currentKey = currentKey,
                            prevKeyType = prevKeyType,
                            currentKeyType = currentKeyType
                    )
                }
    }
}

@Serializable
data class Location(
        val latitude: Double? = null,
        val longitude: Double? = null,
        val altitude: Double? = null,
        val accuracy: Float? = null,
        val speed: Float? = null
) : Value() {
    companion object : ProtoSerializer<Location, DataProtos.Location> {
        override fun toProto(o: Location): DataProtos.Location =
                with(o) {
                    DataProtos.Location.newBuilder().apply {
                        latitude = this@with.latitude ?: Double.NaN
                        longitude = this@with.longitude ?: Double.NaN
                        altitude = this@with.altitude ?: Double.NaN
                        accuracy = this@with.accuracy ?: Float.NaN
                        speed = this@with.speed ?: Float.NaN
                    }.build()
                }

        override fun toObject(p: DataProtos.Location): Location =
                with(p) {
                    Location(
                            latitude = latitude,
                            longitude = longitude,
                            altitude = altitude,
                            accuracy = accuracy,
                            speed = speed
                    )
                }
    }
}

@Serializable
data class Media(
        val mimeType: String? = null
) : Value() {
    companion object : ProtoSerializer<Media, DataProtos.Media> {
        override fun toProto(o: Media): DataProtos.Media =
                with(o) {
                    DataProtos.Media.newBuilder().apply {
                        mimeType = this@with.mimeType ?: ""
                    }.build()
                }

        override fun toObject(p: DataProtos.Media): Media =
                with(p) {
                    Media(
                            mimeType = mimeType
                    )
                }
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
) : Value() {
    companion object : ProtoSerializer<Message, DataProtos.Message> {
        override fun toProto(o: Message): DataProtos.Message =
                with(o) {
                    DataProtos.Message.newBuilder().apply {
                        number = this@with.number ?: ""
                        messageClass = this@with.messageClass ?: ""
                        messageBox = this@with.messageBox ?: ""
                        contactType = this@with.contactType ?: ""
                        isStarred = this@with.isStarred ?: false
                        isPinned = this@with.isPinned ?: false
                    }.build()
                }

        override fun toObject(p: DataProtos.Message): Message =
                with(p) {
                    Message(
                            number = number,
                            messageClass = messageClass,
                            messageBox = messageBox,
                            contactType = contactType,
                            isStarred = isStarred,
                            isPinned = isPinned
                    )
                }
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
) : Value() {
    companion object : ProtoSerializer<Notification, DataProtos.Notification> {
        override fun toProto(o: Notification): DataProtos.Notification =
                with(o) {
                    DataProtos.Notification.newBuilder().apply {
                        name = this@with.name ?: ""
                        packageName = this@with.packageName ?: ""
                        isSystemApp = this@with.isSystemApp ?: false
                        isUpdatedSystemApp = this@with.isUpdatedSystemApp ?: false
                        title = this@with.title ?: ""
                        visibility = this@with.visibility ?: ""
                        category = this@with.category ?: ""
                        vibrate = this@with.vibrate ?: ""
                        sound = this@with.sound ?: ""
                        lightColor = this@with.lightColor ?: ""
                        isPosted = this@with.isPosted ?: false
                    }.build()
                }

        override fun toObject(p: DataProtos.Notification): Notification =
                with(p) {
                    Notification(
                            name = name,
                            packageName = packageName,
                            isSystemApp = isSystemApp,
                            isUpdatedSystemApp = isUpdatedSystemApp,
                            title = title,
                            visibility = visibility,
                            category = category,
                            vibrate = vibrate,
                            sound = sound,
                            lightColor = lightColor,
                            isPosted = isPosted
                    )
                }
    }
}

@Serializable
data class PhysicalStat(
        val type: String? = null,
        val startTime: Long? = null,
        val endTime: Long? = null,
        val value: String? = null
) : Value() {
    companion object : ProtoSerializer<PhysicalStat, DataProtos.PhysicalStat> {
        override fun toProto(o: PhysicalStat): DataProtos.PhysicalStat =
                with(o) {
                    DataProtos.PhysicalStat.newBuilder().apply {
                        type = this@with.type ?: ""
                        startTime = this@with.startTime ?: Long.MIN_VALUE
                        endTime = this@with.endTime ?: Long.MIN_VALUE
                        value = this@with.value ?: ""
                    }.build()
                }

        override fun toObject(p: DataProtos.PhysicalStat): PhysicalStat =
                with(p) {
                    PhysicalStat(
                            type = type,
                            startTime = startTime,
                            endTime = endTime,
                            value = value
                    )
                }
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
        val item: List<Item> = emptyList()
) : Value() {
    @Serializable
    data class Item(
            val question: String? = null,
            val response: String? = null
    )

    companion object : ProtoSerializer<Survey, DataProtos.Survey> {
        override fun toProto(o: Survey): DataProtos.Survey =
                with(o) {
                    DataProtos.Survey.newBuilder().apply {
                        title = this@with.title ?: ""
                        message = this@with.message ?: ""
                        timeoutPolicy = this@with.timeoutPolicy ?: ""
                        timeoutSec = this@with.timeoutSec ?: Long.MIN_VALUE
                        deliveredTime = this@with.deliveredTime ?: Long.MIN_VALUE
                        reactionTime = this@with.reactionTime ?: Long.MIN_VALUE
                        responseTime = this@with.responseTime ?: Long.MIN_VALUE
                        addAllItem(this@with.item.map {
                            DataProtos.Survey.Item.newBuilder().apply {
                                question = it.question
                                response = it.response
                            }.build()
                        })
                    }.build()

                }

        override fun toObject(p: DataProtos.Survey): Survey =
                with(p) {
                    Survey(
                            title = title,
                            message = message,
                            timeoutPolicy = timeoutPolicy,
                            timeoutSec = timeoutSec,
                            deliveredTime = deliveredTime,
                            reactionTime = reactionTime,
                            responseTime = responseTime,
                            item = itemList.map {
                                Item(
                                        question = it.question,
                                        response = it.response
                                )
                            }
                    )
                }
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
) : Value() {
    companion object : ProtoSerializer<DataTraffic, DataProtos.DataTraffic> {
        override fun toProto(o: DataTraffic): DataProtos.DataTraffic =
                with(o) {
                    DataProtos.DataTraffic.newBuilder().apply {
                        fromTime = this@with.fromTime ?: Long.MIN_VALUE
                        toTime = this@with.toTime ?: Long.MIN_VALUE
                        rxBytes = this@with.rxBytes ?: Long.MIN_VALUE
                        txBytes = this@with.txBytes ?: Long.MIN_VALUE
                        mobileRxBytes = this@with.mobileRxBytes ?: Long.MIN_VALUE
                        mobileTxBytes = this@with.mobileTxBytes ?: Long.MIN_VALUE
                    }.build()
                }

        override fun toObject(p: DataProtos.DataTraffic): DataTraffic =
                with(p) {
                    DataTraffic(
                            fromTime = fromTime,
                            toTime = toTime,
                            rxBytes = rxBytes,
                            txBytes = txBytes,
                            mobileRxBytes = mobileRxBytes,
                            mobileTxBytes = mobileTxBytes
                    )
                }
    }
}

@Serializable
data class Wifi(
        val bssid: String? = null,
        val ssid: String? = null,
        val frequency: Int? = null,
        val rssi: Int? = null
) : Value() {
    companion object : ProtoSerializer<Wifi, DataProtos.Wifi> {
        override fun toProto(o: Wifi): DataProtos.Wifi =
                with(o) {
                    DataProtos.Wifi.newBuilder().apply {
                        bssid = this@with.bssid ?: ""
                        ssid = this@with.ssid ?: ""
                        frequency = this@with.frequency ?: Int.MIN_VALUE
                        rssi = this@with.rssi ?: Int.MIN_VALUE
                    }.build()
                }

        override fun toObject(p: DataProtos.Wifi): Wifi =
                with(p) {
                    Wifi(
                            bssid = bssid,
                            ssid = ssid,
                            frequency = frequency,
                            rssi = rssi
                    )
                }
    }
}
