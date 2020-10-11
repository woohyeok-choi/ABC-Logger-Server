package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.common.toOffsetDateTime
import kaist.iclab.abclogger.grpc.proto.DatumProtos
import kaist.iclab.abclogger.grpc.proto.SubjectProtos
import kotlinx.serialization.*
import java.time.OffsetDateTime


@Polymorphic
@Serializable
open class Value


@Serializable
data class Datum(
    val timestamp: Long? = null,
    val utcOffsetSec: Int? = null,
    val subject: Subject? = null,
    val uploadTime: Long? = null,
    val value: Value? = null,
    val datumType: String? = null,
    @Contextual
    val offsetTimestamp: OffsetDateTime? = null,
    @Contextual
    val offsetUploadTime: OffsetDateTime? = null,
) {
    companion object : TwoWaySerializer<Datum, DatumProtos.Datum> {
        override fun toProto(o: Datum, isMd5Hashed: Boolean): DatumProtos.Datum =
            DatumProtos.Datum.newBuilder().apply {
                timestamp = o.timestamp ?: Long.MIN_VALUE
                utcOffsetSec = o.utcOffsetSec ?: Int.MIN_VALUE
                subject = o.subject?.let { Subject.toProto(it, isMd5Hashed) }
                    ?: SubjectProtos.Subject.getDefaultInstance()
                uploadTime = o.uploadTime ?: Long.MIN_VALUE

                when (o.value) {
                    is PhysicalActivityTransition -> physicalActivityTransition = PhysicalActivityTransition.toProto(o.value, isMd5Hashed)
                    is PhysicalActivity -> physicalActivity = PhysicalActivity.toProto(o.value, isMd5Hashed)
                    is AppUsageEvent -> appUsageEvent = AppUsageEvent.toProto(o.value, isMd5Hashed)
                    is Battery -> battery = Battery.toProto(o.value, isMd5Hashed)
                    is Bluetooth -> bluetooth = Bluetooth.toProto(o.value, isMd5Hashed)
                    is CallLog -> callLog = CallLog.toProto(o.value, isMd5Hashed)
                    is DeviceEvent -> deviceEvent = DeviceEvent.toProto(o.value, isMd5Hashed)
                    is EmbeddedSensor -> embeddedSensor = EmbeddedSensor.toProto(o.value, isMd5Hashed)
                    is ExternalSensor -> externalSensor = ExternalSensor.toProto(o.value, isMd5Hashed)
                    is InstalledApp -> installedApp = InstalledApp.toProto(o.value, isMd5Hashed)
                    is KeyLog -> keyLog = KeyLog.toProto(o.value, isMd5Hashed)
                    is Location -> location = Location.toProto(o.value, isMd5Hashed)
                    is Media -> media = Media.toProto(o.value, isMd5Hashed)
                    is Message -> message = Message.toProto(o.value, isMd5Hashed)
                    is Notification -> notification = Notification.toProto(o.value, isMd5Hashed)
                    is Fitness -> fitness = Fitness.toProto(o.value, isMd5Hashed)
                    is Survey -> survey = Survey.toProto(o.value, isMd5Hashed)
                    is DataTraffic -> dataTraffic = DataTraffic.toProto(o.value, isMd5Hashed)
                    is Wifi -> wifi = Wifi.toProto(o.value, isMd5Hashed)
                }
            }.build()

        override fun toObject(p: DatumProtos.Datum): Datum =
            with(p) {
                Datum(
                    timestamp = timestamp,
                    utcOffsetSec = utcOffsetSec,
                    subject = subject?.let { Subject.toObject(it) },
                    uploadTime = uploadTime,
                    datumType = dataCaseToDataType(p.dataCase).name,
                    value = when {
                        hasPhysicalActivityTransition() -> PhysicalActivityTransition.toObject(physicalActivityTransition)
                        hasPhysicalActivity() -> PhysicalActivity.toObject(physicalActivity)
                        hasAppUsageEvent() -> AppUsageEvent.toObject(appUsageEvent)
                        hasBattery() -> Battery.toObject(battery)
                        hasBluetooth() -> Bluetooth.toObject(bluetooth)
                        hasCallLog() -> CallLog.toObject(callLog)
                        hasDeviceEvent() -> DeviceEvent.toObject(deviceEvent)
                        hasEmbeddedSensor() -> EmbeddedSensor.toObject(embeddedSensor)
                        hasExternalSensor() -> ExternalSensor.toObject(externalSensor)
                        hasInstalledApp() -> InstalledApp.toObject(installedApp)
                        hasKeyLog() -> KeyLog.toObject(keyLog)
                        hasLocation() -> Location.toObject(location)
                        hasMedia() -> Media.toObject(media)
                        hasMessage() -> Message.toObject(message)
                        hasNotification() -> Notification.toObject(notification)
                        hasFitness() -> Fitness.toObject(fitness)
                        hasSurvey() -> Survey.toObject(survey)
                        hasDataTraffic() -> DataTraffic.toObject(dataTraffic)
                        hasWifi() -> Wifi.toObject(wifi)
                        else -> null
                    },
                    offsetTimestamp = toOffsetDateTime(timestamp, utcOffsetSec),
                    offsetUploadTime = toOffsetDateTime(uploadTime, utcOffsetSec),
                )
            }
    }
}


@Serializable
data class PhysicalActivityTransition(
    val type: String? = null,
    val isEntered: Boolean? = null
) : Value() {
    companion object : TwoWaySerializer<PhysicalActivityTransition, DatumProtos.PhysicalActivityTransition> {
        override fun toProto(o: PhysicalActivityTransition, isMd5Hashed: Boolean): DatumProtos.PhysicalActivityTransition =
            DatumProtos.PhysicalActivityTransition.newBuilder().apply {
                type = o.type ?: UNKNOWN_STRING
                isEntered = o.isEntered ?: UNKNOWN_BOOLEAN
            }.build()


        override fun toObject(p: DatumProtos.PhysicalActivityTransition): PhysicalActivityTransition =
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
    val activity: List<Activity>? = null
) : Value() {
    @Serializable
    data class Activity(
        val type: String? = null,
        val confidence: Int? = null
    )

    companion object : TwoWaySerializer<PhysicalActivity, DatumProtos.PhysicalActivity> {
        override fun toProto(o: PhysicalActivity, isMd5Hashed: Boolean): DatumProtos.PhysicalActivity =
            DatumProtos.PhysicalActivity.newBuilder().apply {
                val activityProto = o.activity?.map { activity ->
                    DatumProtos.PhysicalActivity.Activity.newBuilder().apply {
                        type = activity.type ?: UNKNOWN_STRING
                        confidence = activity.confidence ?: UNKNOWN_INT
                    }.build()
                } ?: listOf()
                addAllActivity(activityProto)
            }.build()

        override fun toObject(p: DatumProtos.PhysicalActivity): PhysicalActivity =
            with(p) {
                PhysicalActivity(
                    activity = activityList.map {
                        Activity(
                            type = it.type,
                            confidence = it.confidence
                        )
                    }
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
    companion object : TwoWaySerializer<AppUsageEvent, DatumProtos.AppUsageEvent> {
        override fun toProto(o: AppUsageEvent, isMd5Hashed: Boolean): DatumProtos.AppUsageEvent =
            DatumProtos.AppUsageEvent.newBuilder().apply {
                name = o.name ?: UNKNOWN_STRING
                packageName = o.packageName ?: UNKNOWN_STRING
                type = o.type ?: UNKNOWN_STRING
                isSystemApp = o.isSystemApp ?: UNKNOWN_BOOLEAN
                isUpdatedSystemApp = o.isUpdatedSystemApp ?: UNKNOWN_BOOLEAN
            }.build()

        override fun toObject(p: DatumProtos.AppUsageEvent): AppUsageEvent =
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
    val status: String? = null,
    val capacity: Int? = null,
    val chargeCounter: Int? = null,
    val currentAverage: Int? = null,
    val currentNow: Int? = null,
    val energyCounter: Long? = null,
    val technology: String? = null
) : Value() {
    companion object : TwoWaySerializer<Battery, DatumProtos.Battery> {
        override fun toProto(o: Battery, isMd5Hashed: Boolean): DatumProtos.Battery =
            DatumProtos.Battery.newBuilder().apply {
                level = o.level ?: UNKNOWN_INT
                scale = o.scale ?: UNKNOWN_INT
                temperature = o.temperature ?: UNKNOWN_INT
                voltage = o.voltage ?: UNKNOWN_INT
                health = o.health ?: UNKNOWN_STRING
                pluggedType = o.pluggedType ?: UNKNOWN_STRING
                status = o.status ?: UNKNOWN_STRING
                capacity = o.capacity ?: UNKNOWN_INT
                chargeCounter = o.chargeCounter ?: UNKNOWN_INT
                currentAverage = o.currentAverage ?: UNKNOWN_INT
                currentNow = o.currentNow ?: UNKNOWN_INT
                energyCounter = o.energyCounter ?: UNKNOWN_LONG
                technology = o.technology ?: UNKNOWN_STRING
            }.build()

        override fun toObject(p: DatumProtos.Battery): Battery =
            with(p) {
                Battery(
                    level = level,
                    scale = scale,
                    temperature = temperature,
                    voltage = voltage,
                    health = health,
                    pluggedType = pluggedType,
                    status = status,
                    capacity = capacity,
                    chargeCounter = chargeCounter,
                    currentAverage = currentAverage,
                    currentNow = currentNow,
                    energyCounter = energyCounter,
                    technology = technology
                )
            }
    }
}

@Serializable
data class Bluetooth(
    val name: String? = null,
    val alias: String? = null,
    val address: String? = null,
    val bondState: String? = null,
    val deviceType: String? = null,
    val classType: String? = null,
    val rssi: Int? = null,
    val isLowEnergy: Boolean? = null
) : Value() {
    companion object : TwoWaySerializer<Bluetooth, DatumProtos.Bluetooth> {
        override fun toProto(o: Bluetooth, isMd5Hashed: Boolean): DatumProtos.Bluetooth =
            DatumProtos.Bluetooth.newBuilder().apply {
                name = o.name ?: UNKNOWN_STRING
                alias = o.alias ?: UNKNOWN_STRING
                address = o.address ?: UNKNOWN_STRING
                bondState = o.bondState ?: UNKNOWN_STRING
                deviceType = o.deviceType ?: UNKNOWN_STRING
                classType = o.classType ?: UNKNOWN_STRING
                rssi = o.rssi ?: UNKNOWN_INT
                isLowEnergy = o.isLowEnergy ?: UNKNOWN_BOOLEAN
            }.build()


        override fun toObject(p: DatumProtos.Bluetooth): Bluetooth =
            with(p) {
                Bluetooth(
                    name = name,
                    alias = alias,
                    address = address,
                    bondState = bondState,
                    deviceType = deviceType,
                    classType = classType,
                    rssi = rssi,
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
    val presentation: String? = null,
    val dataUsage: Long? = null,
    val contactType: String? = null,
    val isStarred: Boolean? = null,
    val isPinned: Boolean? = null
) : Value() {
    companion object : TwoWaySerializer<CallLog, DatumProtos.CallLog> {
        override fun toProto(o: CallLog, isMd5Hashed: Boolean): DatumProtos.CallLog =
            DatumProtos.CallLog.newBuilder().apply {
                duration = o.duration ?: UNKNOWN_LONG
                number = o.number ?: UNKNOWN_STRING
                type = o.type ?: UNKNOWN_STRING
                dataUsage = o.dataUsage ?: UNKNOWN_LONG
                presentation = o.presentation ?: UNKNOWN_STRING
                contactType = o.contactType ?: UNKNOWN_STRING
                isStarred = o.isStarred ?: UNKNOWN_BOOLEAN
                isPinned = o.isPinned ?: UNKNOWN_BOOLEAN
            }.build()

        override fun toObject(p: DatumProtos.CallLog): CallLog =
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
    val type: String? = null,
    val extra: Map<String, String>? = null
) : Value() {
    companion object : TwoWaySerializer<DeviceEvent, DatumProtos.DeviceEvent> {
        override fun toProto(o: DeviceEvent, isMd5Hashed: Boolean): DatumProtos.DeviceEvent =
            DatumProtos.DeviceEvent.newBuilder().apply {
                type = o.type ?: UNKNOWN_STRING
                putAllExtra(o.extra ?: mapOf())
            }.build()

        override fun toObject(p: DatumProtos.DeviceEvent): DeviceEvent =
            with(p) {
                DeviceEvent(
                    type = type,
                    extra = extraMap
                )
            }
    }
}

@Serializable
data class EmbeddedSensor(
    val valueType: String? = null,
    val status: Map<String, String>? = null,
    val valueFormat: String? = null,
    val valueUnit: String? = null,
    val value: List<String> = emptyList()
) : Value() {
    companion object : TwoWaySerializer<EmbeddedSensor, DatumProtos.EmbeddedSensor> {
        override fun toProto(o: EmbeddedSensor, isMd5Hashed: Boolean): DatumProtos.EmbeddedSensor =
            DatumProtos.EmbeddedSensor.newBuilder().apply {
                valueType = o.valueType ?: UNKNOWN_STRING
                putAllStatus(o.status ?: mapOf())
                valueFormat = o.valueFormat ?: UNKNOWN_STRING
                valueUnit = o.valueUnit ?: UNKNOWN_STRING
                addAllValue(o.value)
            }.build()

        override fun toObject(p: DatumProtos.EmbeddedSensor): EmbeddedSensor =
            with(p) {
                EmbeddedSensor(
                    valueType = valueType,
                    status = statusMap,
                    valueFormat = valueFormat,
                    valueUnit = valueUnit,
                    value = valueList
                )
            }
    }
}

@Serializable
data class ExternalSensor(
    val deviceType: String? = null,
    val valueType: String? = null,
    val identifier: String? = null,
    val others: Map<String, String>? = null,
    val valueFormat: String? = null,
    val valueUnit: String? = null,
    val value: List<String> = emptyList()
) : Value() {
    companion object : TwoWaySerializer<ExternalSensor, DatumProtos.ExternalSensor> {
        override fun toProto(o: ExternalSensor, isMd5Hashed: Boolean): DatumProtos.ExternalSensor =
            DatumProtos.ExternalSensor.newBuilder().apply {
                deviceType = o.deviceType ?: UNKNOWN_STRING
                valueType = o.valueType ?: UNKNOWN_STRING
                identifier = o.identifier ?: UNKNOWN_STRING
                putAllOthers(o.others ?: mapOf())
                valueFormat = o.valueFormat ?: UNKNOWN_STRING
                valueUnit = o.valueUnit ?: UNKNOWN_STRING
                addAllValue(o.value)
            }.build()

        override fun toObject(p: DatumProtos.ExternalSensor): ExternalSensor =
            with(p) {
                ExternalSensor(
                    deviceType = deviceType,
                    valueType = valueType,
                    identifier = identifier,
                    others = othersMap,
                    valueFormat = valueFormat,
                    valueUnit = valueUnit,
                    value = valueList
                )
            }

    }
}

@Serializable
data class InstalledApp(
    val app: List<App>? = null
) : Value() {
    @Serializable
    data class App(
        val name: String? = null,
        val packageName: String? = null,
        val isSystemApp: Boolean? = null,
        val isUpdatedSystemApp: Boolean? = null,
        val firstInstallTime: Long? = null,
        val lastUpdateTime: Long? = null
    )

    companion object : TwoWaySerializer<InstalledApp, DatumProtos.InstalledApp> {
        override fun toProto(o: InstalledApp, isMd5Hashed: Boolean): DatumProtos.InstalledApp =
            DatumProtos.InstalledApp.newBuilder().apply {
                val app = o.app?.map {
                    DatumProtos.InstalledApp.App.newBuilder().apply {
                        name = it.name ?: UNKNOWN_STRING
                        packageName = it.packageName ?: UNKNOWN_STRING
                        isSystemApp = it.isSystemApp ?: UNKNOWN_BOOLEAN
                        isUpdatedSystemApp = it.isUpdatedSystemApp ?: UNKNOWN_BOOLEAN
                        firstInstallTime = it.firstInstallTime ?: UNKNOWN_LONG
                        lastUpdateTime = it.lastUpdateTime ?: UNKNOWN_LONG
                    }.build()
                } ?: listOf()
                addAllApp(app)
            }.build()

        override fun toObject(p: DatumProtos.InstalledApp): InstalledApp =
            with(p) {
                InstalledApp(
                    app = appList.map {
                        App(
                            name = it.name,
                            packageName = it.packageName,
                            isSystemApp = it.isSystemApp,
                            isUpdatedSystemApp = it.isUpdatedSystemApp,
                            firstInstallTime = it.firstInstallTime,
                            lastUpdateTime = it.lastUpdateTime
                        )
                    }
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
    companion object : TwoWaySerializer<KeyLog, DatumProtos.KeyLog> {
        override fun toProto(o: KeyLog, isMd5Hashed: Boolean): DatumProtos.KeyLog =
            DatumProtos.KeyLog.newBuilder().apply {
                name = o.name ?: UNKNOWN_STRING
                packageName = o.packageName ?: UNKNOWN_STRING
                isSystemApp = o.isSystemApp ?: UNKNOWN_BOOLEAN
                isUpdatedSystemApp = o.isUpdatedSystemApp ?: UNKNOWN_BOOLEAN
                distance = o.distance ?: UNKNOWN_FLOAT
                timeTaken = o.timeTaken ?: UNKNOWN_LONG
                keyboardType = o.keyboardType ?: UNKNOWN_STRING
                prevKey = o.prevKey ?: UNKNOWN_STRING
                currentKey = o.currentKey ?: UNKNOWN_STRING
                prevKeyType = o.prevKeyType ?: UNKNOWN_STRING
                currentKeyType = o.currentKeyType ?: UNKNOWN_STRING
            }.build()


        override fun toObject(p: DatumProtos.KeyLog): KeyLog =
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
    companion object : TwoWaySerializer<Location, DatumProtos.Location> {
        override fun toProto(o: Location, isMd5Hashed: Boolean): DatumProtos.Location =
            DatumProtos.Location.newBuilder().apply {
                latitude = o.latitude ?: UNKNOWN_DOUBLE
                longitude = o.longitude ?: UNKNOWN_DOUBLE
                altitude = o.altitude ?: UNKNOWN_DOUBLE
                accuracy = o.accuracy ?: UNKNOWN_FLOAT
                speed = o.speed ?: UNKNOWN_FLOAT
            }.build()

        override fun toObject(p: DatumProtos.Location): Location =
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
    companion object : TwoWaySerializer<Media, DatumProtos.Media> {
        override fun toProto(o: Media, isMd5Hashed: Boolean): DatumProtos.Media =
            DatumProtos.Media.newBuilder().apply {
                mimeType = o.mimeType ?: UNKNOWN_STRING
            }.build()

        override fun toObject(p: DatumProtos.Media): Media =
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
    companion object : TwoWaySerializer<Message, DatumProtos.Message> {
        override fun toProto(o: Message, isMd5Hashed: Boolean): DatumProtos.Message =
            DatumProtos.Message.newBuilder().apply {
                number = o.number ?: UNKNOWN_STRING
                messageClass = o.messageClass ?: UNKNOWN_STRING
                messageBox = o.messageBox ?: UNKNOWN_STRING
                contactType = o.contactType ?: UNKNOWN_STRING
                isStarred = o.isStarred ?: UNKNOWN_BOOLEAN
                isPinned = o.isPinned ?: UNKNOWN_BOOLEAN
            }.build()

        override fun toObject(p: DatumProtos.Message): Message =
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
    val key: String? = null,
    val groupKey: String? = null,
    val notificationId: Int? = null,
    val tag: String? = null,
    val isClearable: Boolean? = null,
    val isOngoing: Boolean? = null,
    val title: String? = null,
    val bigTitle: String? = null,
    val text: String? = null,
    val subText: String? = null,
    val bigText: String? = null,
    val summaryText: String? = null,
    val infoText: String? = null,
    val visibility: String? = null,
    val category: String? = null,
    val priority: String? = null,
    val vibrate: String? = null,
    val sound: String? = null,
    val lightColor: String? = null,
    val isPosted: Boolean? = null
) : Value() {
    companion object : TwoWaySerializer<Notification, DatumProtos.Notification> {
        override fun toProto(o: Notification, isMd5Hashed: Boolean): DatumProtos.Notification =
            DatumProtos.Notification.newBuilder().apply {
                name = o.name ?: UNKNOWN_STRING
                packageName = o.packageName ?: UNKNOWN_STRING
                isSystemApp = o.isSystemApp ?: UNKNOWN_BOOLEAN
                isUpdatedSystemApp = o.isUpdatedSystemApp ?: UNKNOWN_BOOLEAN
                key = o.key ?: UNKNOWN_STRING
                groupKey = o.groupKey ?: UNKNOWN_STRING
                notificationId = o.notificationId ?: UNKNOWN_INT
                tag = o.tag ?: UNKNOWN_STRING
                isClearable = o.isClearable ?: UNKNOWN_BOOLEAN
                isOngoing = o.isOngoing ?: UNKNOWN_BOOLEAN
                title = o.title ?: UNKNOWN_STRING
                bigTitle = o.bigTitle ?: UNKNOWN_STRING
                text = o.text ?: UNKNOWN_STRING
                subText = o.subText ?: UNKNOWN_STRING
                bigText = o.bigTitle ?: UNKNOWN_STRING
                summaryText = o.summaryText ?: UNKNOWN_STRING
                infoText = o.infoText ?: UNKNOWN_STRING
                visibility = o.visibility ?: UNKNOWN_STRING
                category = o.category ?: UNKNOWN_STRING
                priority = o.priority ?: UNKNOWN_STRING
                vibrate = o.vibrate ?: UNKNOWN_STRING
                sound = o.sound ?: UNKNOWN_STRING
                lightColor = o.lightColor ?: UNKNOWN_STRING
                isPosted = o.isPosted ?: UNKNOWN_BOOLEAN
            }.build()


        override fun toObject(p: DatumProtos.Notification): Notification =
            with(p) {
                Notification(
                    name = name,
                    packageName = packageName,
                    isSystemApp = isSystemApp,
                    isUpdatedSystemApp = isUpdatedSystemApp,
                    key = key,
                    groupKey = groupKey,
                    notificationId = notificationId,
                    tag = tag,
                    isClearable = isClearable,
                    isOngoing = isOngoing,
                    title = title,
                    bigTitle = bigTitle,
                    text = text,
                    subText = subText,
                    bigText = bigText,
                    summaryText = summaryText,
                    infoText = infoText,
                    visibility = visibility,
                    category = category,
                    priority = priority,
                    vibrate = vibrate,
                    sound = sound,
                    lightColor = lightColor,
                    isPosted = isPosted
                )
            }
    }
}

@Serializable
data class Fitness(
    val type: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val value: String? = null,
    val fitnessDeviceModel: String? = null,
    val fitnessDeviceManufacturer: String? = null,
    val fitnessDeviceType: String? = null,
    val dataSourceName: String? = null,
    val dataSourcePackageName: String? = null
) : Value() {
    companion object : TwoWaySerializer<Fitness, DatumProtos.Fitness> {
        override fun toProto(o: Fitness, isMd5Hashed: Boolean): DatumProtos.Fitness =
            DatumProtos.Fitness.newBuilder().apply {
                type = o.type ?: UNKNOWN_STRING
                startTime = o.startTime ?: UNKNOWN_LONG
                endTime = o.endTime ?: UNKNOWN_LONG
                value = o.value ?: UNKNOWN_STRING
                fitnessDeviceModel = o.fitnessDeviceModel ?: UNKNOWN_STRING
                fitnessDeviceManufacturer = o.fitnessDeviceManufacturer ?: UNKNOWN_STRING
                fitnessDeviceType = o.fitnessDeviceType ?: UNKNOWN_STRING
                dataSourceName = o.dataSourceName ?: UNKNOWN_STRING
                dataSourcePackageName = o.dataSourcePackageName ?: UNKNOWN_STRING
            }.build()

        override fun toObject(p: DatumProtos.Fitness): Fitness =
            with(p) {
                Fitness(
                    type = type,
                    startTime = startTime,
                    endTime = endTime,
                    value = value,
                    fitnessDeviceModel = fitnessDeviceModel,
                    fitnessDeviceManufacturer = fitnessDeviceManufacturer,
                    fitnessDeviceType = fitnessDeviceType,
                    dataSourceName = dataSourceName,
                    dataSourcePackageName = dataSourcePackageName
                )
            }
    }
}


@Serializable
data class Survey(
    val eventTime: Long? = null,
    val eventName: String? = null,
    val intendedTriggerTime: Long? = null,
    val actualTriggerTime: Long? = null,
    val reactionTime: Long? = null,
    val responseTime: Long? = null,
    val url: String? = null,
    val title: String? = null,
    val altTitle: String? = null,
    val message: String? = null,
    val altMessage: String? = null,
    val instruction: String? = null,
    val altInstruction: String? = null,
    val timeoutUntil: Long? = null,
    val timeoutAction: String? = null,
    val response: List<Response>? = null
) : Value() {
    @Serializable
    data class Response(
        val index: Int? = null,
        val type: String? = null,
        val question: String? = null,
        val altQuestion: String? = null,
        val answer: List<String>? = null
    )

    companion object : TwoWaySerializer<Survey, DatumProtos.Survey> {
        override fun toProto(o: Survey, isMd5Hashed: Boolean): DatumProtos.Survey =
            DatumProtos.Survey.newBuilder().apply {
                eventTime = o.eventTime ?: UNKNOWN_LONG
                eventName = o.eventName ?: UNKNOWN_STRING
                intendedTriggerTime = o.intendedTriggerTime ?: UNKNOWN_LONG
                actualTriggerTime = o.actualTriggerTime ?: UNKNOWN_LONG
                reactionTime = o.reactionTime ?: UNKNOWN_LONG
                responseTime = o.responseTime ?: UNKNOWN_LONG
                url = o.url ?: UNKNOWN_STRING
                title = o.title ?: UNKNOWN_STRING
                altTitle = o.altTitle ?: UNKNOWN_STRING
                message = o.message ?: UNKNOWN_STRING
                altMessage = o.altMessage ?: UNKNOWN_STRING
                instruction = o.instruction ?: UNKNOWN_STRING
                altInstruction = o.altInstruction ?: UNKNOWN_STRING
                timeoutUntil = o.timeoutUntil ?: UNKNOWN_LONG
                timeoutAction = o.timeoutAction ?: UNKNOWN_STRING

                val response = o.response?.map {
                    DatumProtos.Survey.Response.newBuilder().apply {
                        index = it.index ?: UNKNOWN_INT
                        type = it.type ?: UNKNOWN_STRING
                        question = it.question ?: UNKNOWN_STRING
                        altQuestion = it.altQuestion ?: UNKNOWN_STRING
                        addAllAnswer(it.answer)
                    }.build()
                } ?: listOf()
                addAllResponse(response)
            }.build()


        override fun toObject(p: DatumProtos.Survey): Survey =
            with(p) {
                Survey(
                    eventTime = eventTime,
                    eventName = eventName,
                    intendedTriggerTime = intendedTriggerTime,
                    actualTriggerTime = actualTriggerTime,
                    reactionTime = reactionTime,
                    responseTime = responseTime,
                    url = url,
                    title = title,
                    altTitle = altTitle,
                    message = message,
                    altMessage = altMessage,
                    instruction = instruction,
                    altInstruction = altInstruction,
                    timeoutUntil = timeoutUntil,
                    timeoutAction = timeoutAction,
                    response = responseList.map {
                        Response(
                            index = it.index,
                            type = it.type,
                            question = it.question,
                            altQuestion = it.altQuestion,
                            answer = it.answerList
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
    companion object : TwoWaySerializer<DataTraffic, DatumProtos.DataTraffic> {
        override fun toProto(o: DataTraffic, isMd5Hashed: Boolean): DatumProtos.DataTraffic =
            DatumProtos.DataTraffic.newBuilder().apply {
                fromTime = o.fromTime ?: UNKNOWN_LONG
                toTime = o.toTime ?: UNKNOWN_LONG
                rxBytes = o.rxBytes ?: UNKNOWN_LONG
                txBytes = o.txBytes ?: UNKNOWN_LONG
                mobileRxBytes = o.mobileRxBytes ?: UNKNOWN_LONG
                mobileTxBytes = o.mobileTxBytes ?: UNKNOWN_LONG
            }.build()

        override fun toObject(p: DatumProtos.DataTraffic): DataTraffic =
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
    val accessPoint: List<AccessPoint>? = null
) : Value() {

    @Serializable
    data class AccessPoint(
        val bssid: String? = null,
        val ssid: String? = null,
        val frequency: Int? = null,
        val rssi: Int? = null
    )

    companion object : TwoWaySerializer<Wifi, DatumProtos.Wifi> {
        override fun toProto(o: Wifi, isMd5Hashed: Boolean): DatumProtos.Wifi =
            DatumProtos.Wifi.newBuilder().apply {
                val accessPoint = o.accessPoint?.map {
                    DatumProtos.Wifi.AccessPoint.newBuilder().apply {
                        bssid = it.bssid ?: UNKNOWN_STRING
                        ssid = it.ssid ?: UNKNOWN_STRING
                        frequency = it.frequency ?: UNKNOWN_INT
                        rssi = it.rssi ?: UNKNOWN_INT
                    }.build()
                }
                addAllAccessPoint(accessPoint)
            }.build()

        override fun toObject(p: DatumProtos.Wifi): Wifi =
            with(p) {
                Wifi(
                    accessPoint = accessPointList.map {
                        AccessPoint(
                            bssid = it.bssid,
                            ssid = it.ssid,
                            frequency = it.frequency,
                            rssi = it.rssi
                        )
                    }
                )
            }
    }
}
