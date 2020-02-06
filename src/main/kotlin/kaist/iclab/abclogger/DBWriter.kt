package kaist.iclab.abclogger

import io.grpc.Status
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kaist.iclab.abclogger.grpc.DatumProto
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.TimeUnit

class DBWriter(private val database: Database,
               private val tables: Array<BaseTable>) {

    private val compositeDisposable = CompositeDisposable()

    private val buffers : Map<BaseTable, Subject<DatumProto.Datum>> = tables.associate { table -> table to PublishSubject.create<DatumProto.Datum>() }

    private fun associateToTable(proto: DatumProto.Datum): BaseTable? = when {
        proto.hasPhysicalActivityTransition() -> PhysicalActivityTransitions
        proto.hasPhysicalActivity() -> PhysicalActivities
        proto.hasAppUsageEvent() -> AppUsageEvents
        proto.hasBattery() -> Batteries
        proto.hasBluetooth() -> Bluetoothes
        proto.hasCallLog() -> CallLogs
        proto.hasDeviceEvent() -> DeviceEvents
        proto.hasExternalSensor() -> ExternalSensors
        proto.hasInstalledApp() -> InstalledApps
        proto.hasKeyLog() -> KeyLogs
        proto.hasLocation() -> Locations
        proto.hasMedia() -> Medias
        proto.hasMessage() -> Messages
        proto.hasNotification() -> Notifications
        proto.hasPhysicalStat() -> PhysicalStats
        proto.hasInternalSensor() -> InternalSensors
        proto.hasSurvey() -> Surveys
        proto.hasDataTraffic() -> DataTraffics
        proto.hasWifi() -> Wifis
        else -> null
    }

    private fun subscribe(table: BaseTable, subscriber: (List<DatumProto.Datum>) -> Unit) : Disposable? {
        return buffers[table]?.buffer(10, TimeUnit.SECONDS)?.subscribe(subscriber)
    }

    fun write(datum: DatumProto.Datum) {
        associateToTable(datum)?.let { table -> buffers[table]?.onNext(datum) }
                ?: throw Status.INVALID_ARGUMENT.withDescription("Invalid value of a field, 'data'.").asRuntimeException()
    }

    fun subscribe() {
        val disposables = tables.mapNotNull { table ->
            subscribe(table) { data ->
                transaction(database) {
                    try {
                        table.batchInsert(data = data, body = batchInsertStatement(table))
                    } catch (e: Exception) {
                        Log.error("Failed to batch insert for data: $data", e)
                    }
                }
            }
        }
        disposables.forEach { disposable -> compositeDisposable.add(disposable) }
    }

    fun unsubscribe() {
        compositeDisposable.clear()
    }

    private fun batchInsertStatement(table: BaseTable) : BatchInsertStatement.(DatumProto.Datum) -> Unit = {
        val uploadTime = System.currentTimeMillis()
        when (table) {
            is PhysicalActivityTransitions -> {
                this[PhysicalActivityTransitions.timestamp] = it.timestamp
                this[PhysicalActivityTransitions.utcOffset] = it.utcOffset
                this[PhysicalActivityTransitions.subjectEmail] = it.subjectEmail
                this[PhysicalActivityTransitions.deviceInfo] = it.deviceInfo
                this[PhysicalActivityTransitions.uploadTime] = uploadTime
                this[PhysicalActivityTransitions.type] = it.physicalActivityTransition.type
                this[PhysicalActivityTransitions.isEntered] = it.physicalActivityTransition.isEntered
            }
            is PhysicalActivities -> {
                this[PhysicalActivities.timestamp] = it.timestamp
                this[PhysicalActivities.utcOffset] = it.utcOffset
                this[PhysicalActivities.subjectEmail] = it.subjectEmail
                this[PhysicalActivities.deviceInfo] = it.deviceInfo
                this[PhysicalActivities.uploadTime] = uploadTime
                this[PhysicalActivities.type] = it.physicalActivity.type
                this[PhysicalActivities.confidence] = it.physicalActivity.confidence
            }
            is AppUsageEvents -> {
                this[AppUsageEvents.timestamp] = it.timestamp
                this[AppUsageEvents.utcOffset] = it.utcOffset
                this[AppUsageEvents.subjectEmail] = it.subjectEmail
                this[AppUsageEvents.deviceInfo] = it.deviceInfo
                this[AppUsageEvents.uploadTime] = uploadTime
                this[AppUsageEvents.name] = it.appUsageEvent.name
                this[AppUsageEvents.packageName] = it.appUsageEvent.packageName
                this[AppUsageEvents.type] = it.appUsageEvent.type
                this[AppUsageEvents.isSystemApp] = it.appUsageEvent.isSystemApp
                this[AppUsageEvents.isUpdatedSystemApp] = it.appUsageEvent.isUpdatedSystemApp
            }
            is Batteries -> {
                this[Batteries.timestamp] = it.timestamp
                this[Batteries.utcOffset] = it.utcOffset
                this[Batteries.subjectEmail] = it.subjectEmail
                this[Batteries.deviceInfo] = it.deviceInfo
                this[Batteries.uploadTime] = uploadTime
                this[Batteries.level] = it.battery.level
                this[Batteries.scale] = it.battery.scale
                this[Batteries.temperature] = it.battery.temperature
                this[Batteries.voltage] = it.battery.voltage
                this[Batteries.health] = it.battery.health
                this[Batteries.pluggedType] = it.battery.pluggedType
                this[Batteries.status] = it.battery.status
            }
            is Bluetoothes -> {
                this[Bluetoothes.timestamp] = it.timestamp
                this[Bluetoothes.utcOffset] = it.utcOffset
                this[Bluetoothes.subjectEmail] = it.subjectEmail
                this[Bluetoothes.deviceInfo] = it.deviceInfo
                this[Bluetoothes.uploadTime] = uploadTime
                this[Bluetoothes.deviceName] = it.bluetooth.deviceName
                this[Bluetoothes.address] = it.bluetooth.address
                this[Bluetoothes.rssi] = it.bluetooth.rssi
            }
            is CallLogs -> {
                this[CallLogs.timestamp] = it.timestamp
                this[CallLogs.utcOffset] = it.utcOffset
                this[CallLogs.subjectEmail] = it.subjectEmail
                this[CallLogs.deviceInfo] = it.deviceInfo
                this[CallLogs.uploadTime] = uploadTime
                this[CallLogs.duration] = it.callLog.duration
                this[CallLogs.number] = it.callLog.number
                this[CallLogs.type] = it.callLog.type
                this[CallLogs.presentation] = it.callLog.presentation
                this[CallLogs.dataUsage] = it.callLog.dataUsage
                this[CallLogs.contactType] = it.callLog.contactType
                this[CallLogs.isStarred] = it.callLog.isStarred
                this[CallLogs.isPinned] = it.callLog.isPinned
            }
            is DeviceEvents -> {
                this[DeviceEvents.timestamp] = it.timestamp
                this[DeviceEvents.utcOffset] = it.utcOffset
                this[DeviceEvents.subjectEmail] = it.subjectEmail
                this[DeviceEvents.deviceInfo] = it.deviceInfo
                this[DeviceEvents.uploadTime] = uploadTime
                this[DeviceEvents.type] = it.deviceEvent.type
            }
            is ExternalSensors -> {
                this[ExternalSensors.timestamp] = it.timestamp
                this[ExternalSensors.utcOffset] = it.utcOffset
                this[ExternalSensors.subjectEmail] = it.subjectEmail
                this[ExternalSensors.deviceInfo] = it.deviceInfo
                this[ExternalSensors.uploadTime] = uploadTime
                this[ExternalSensors.sensorId] = it.externalSensor.sensorId
                this[ExternalSensors.name] = it.externalSensor.name
                this[ExternalSensors.description] = it.externalSensor.description
                this[ExternalSensors.firstValue] = it.externalSensor.firstValue
                this[ExternalSensors.secondValue] = it.externalSensor.secondValue
                this[ExternalSensors.thirdValue] = it.externalSensor.thirdValue
                this[ExternalSensors.fourthValue] = it.externalSensor.fourthValue
                this[ExternalSensors.collection] = it.externalSensor.collection
            }
            is InstalledApps -> {
                this[InstalledApps.timestamp] = it.timestamp
                this[InstalledApps.utcOffset] = it.utcOffset
                this[InstalledApps.subjectEmail] = it.subjectEmail
                this[InstalledApps.deviceInfo] = it.deviceInfo
                this[InstalledApps.uploadTime] = uploadTime
                this[InstalledApps.name] = it.installedApp.name
                this[InstalledApps.packageName] = it.installedApp.packageName
                this[InstalledApps.isSystemApp] = it.installedApp.isSystemApp
                this[InstalledApps.isUpdatedSystemApp] = it.installedApp.isUpdatedSystemApp
                this[InstalledApps.firstInstallTime] = it.installedApp.firstInstallTime
                this[InstalledApps.lastUpdateTime] = it.installedApp.lastUpdateTime
            }
            is InternalSensors -> {
                this[InternalSensors.timestamp] = it.timestamp
                this[InternalSensors.utcOffset] = it.utcOffset
                this[InternalSensors.subjectEmail] = it.subjectEmail
                this[InternalSensors.deviceInfo] = it.deviceInfo
                this[InternalSensors.uploadTime] = uploadTime
                this[InternalSensors.type] = it.internalSensor.type
                this[InternalSensors.accuracy] = it.internalSensor.accuracy
                this[InternalSensors.firstValue] = it.internalSensor.firstValue
                this[InternalSensors.secondValue] = it.internalSensor.secondValue
                this[InternalSensors.thirdValue] = it.internalSensor.thirdValue
                this[InternalSensors.fourthValue] = it.internalSensor.fourthValue
            }
            is KeyLogs -> {
                this[KeyLogs.timestamp] = it.timestamp
                this[KeyLogs.utcOffset] = it.utcOffset
                this[KeyLogs.subjectEmail] = it.subjectEmail
                this[KeyLogs.deviceInfo] = it.deviceInfo
                this[KeyLogs.uploadTime] = uploadTime
                this[KeyLogs.name] = it.keyLog.name
                this[KeyLogs.packageName] = it.keyLog.packageName
                this[KeyLogs.isSystemApp] = it.keyLog.isSystemApp
                this[KeyLogs.isUpdatedSystemApp] = it.keyLog.isUpdatedSystemApp
                this[KeyLogs.distance] = it.keyLog.distance
                this[KeyLogs.timeTaken] = it.keyLog.timeTaken
                this[KeyLogs.keyboardType] = it.keyLog.keyboardType
                this[KeyLogs.prevKey] = it.keyLog.prevKey
                this[KeyLogs.currentKey] = it.keyLog.currentKey
                this[KeyLogs.prevKeyType] = it.keyLog.prevKeyType
                this[KeyLogs.currentKeyType] = it.keyLog.currentKeyType
            }
            is Locations -> {
                this[Locations.timestamp] = it.timestamp
                this[Locations.utcOffset] = it.utcOffset
                this[Locations.subjectEmail] = it.subjectEmail
                this[Locations.deviceInfo] = it.deviceInfo
                this[Locations.uploadTime] = uploadTime
                this[Locations.latitude] = it.location.latitude
                this[Locations.longitude] = it.location.longitude
                this[Locations.altitude] = it.location.altitude
                this[Locations.accuracy] = it.location.accuracy
                this[Locations.speed] = it.location.speed
            }
            is Medias -> {
                this[Medias.timestamp] = it.timestamp
                this[Medias.utcOffset] = it.utcOffset
                this[Medias.subjectEmail] = it.subjectEmail
                this[Medias.deviceInfo] = it.deviceInfo
                this[Medias.uploadTime] = uploadTime
                this[Medias.mimeType] = it.media.mimeType
            }
            is Messages -> {
                this[Messages.timestamp] = it.timestamp
                this[Messages.utcOffset] = it.utcOffset
                this[Messages.subjectEmail] = it.subjectEmail
                this[Messages.deviceInfo] = it.deviceInfo
                this[Messages.uploadTime] = uploadTime
                this[Messages.number] = it.message.number
                this[Messages.messageClass] = it.message.messageClass
                this[Messages.messageBox] = it.message.messageBox
                this[Messages.contactType] = it.message.contactType
                this[Messages.isStarred] = it.message.isStarred
                this[Messages.isPinned] = it.message.isPinned
            }
            is Notifications -> {
                this[Notifications.timestamp] = it.timestamp
                this[Notifications.utcOffset] = it.utcOffset
                this[Notifications.subjectEmail] = it.subjectEmail
                this[Notifications.deviceInfo] = it.deviceInfo
                this[Notifications.uploadTime] = uploadTime
                this[Notifications.name] = it.notification.name
                this[Notifications.packageName] = it.notification.packageName
                this[Notifications.isSystemApp] = it.notification.isSystemApp
                this[Notifications.isUpdatedSystemApp] = it.notification.isUpdatedSystemApp
                this[Notifications.title] = it.notification.title
                this[Notifications.visibility] = it.notification.visibility
                this[Notifications.category] = it.notification.category
                this[Notifications.vibrate] = it.notification.vibrate
                this[Notifications.sound] = it.notification.sound
                this[Notifications.lightColor] = it.notification.lightColor
                this[Notifications.isPosted] = it.notification.isPosted
            }
            is PhysicalStats -> {
                this[PhysicalStats.timestamp] = it.timestamp
                this[PhysicalStats.utcOffset] = it.utcOffset
                this[PhysicalStats.subjectEmail] = it.subjectEmail
                this[PhysicalStats.deviceInfo] = it.deviceInfo
                this[PhysicalStats.uploadTime] = uploadTime
                this[PhysicalStats.type] = it.physicalStat.type
                this[PhysicalStats.startTime] = it.physicalStat.startTime
                this[PhysicalStats.endTime] = it.physicalStat.endTime
                this[PhysicalStats.value] = it.physicalStat.value
            }
            is Surveys -> {
                this[Surveys.timestamp] = it.timestamp
                this[Surveys.utcOffset] = it.utcOffset
                this[Surveys.subjectEmail] = it.subjectEmail
                this[Surveys.deviceInfo] = it.deviceInfo
                this[Surveys.uploadTime] = uploadTime
                this[Surveys.title] = it.survey.title
                this[Surveys.message] = it.survey.message
                this[Surveys.timeoutPolicy] = it.survey.timeoutPolicy
                this[Surveys.timeoutSec] = it.survey.timeoutSec
                this[Surveys.deliveredTime] = it.survey.deliveredTime
                this[Surveys.reactionTime] = it.survey.reactionTime
                this[Surveys.responseTime] = it.survey.responseTime
                this[Surveys.json] = it.survey.json
            }
            is DataTraffics -> {
                this[DataTraffics.timestamp] = it.timestamp
                this[DataTraffics.utcOffset] = it.utcOffset
                this[DataTraffics.subjectEmail] = it.subjectEmail
                this[DataTraffics.deviceInfo] = it.deviceInfo
                this[DataTraffics.uploadTime] = uploadTime
                this[DataTraffics.fromTime] = it.dataTraffic.fromTime
                this[DataTraffics.toTime] = it.dataTraffic.toTime
                this[DataTraffics.rxBytes] = it.dataTraffic.rxBytes
                this[DataTraffics.txBytes] = it.dataTraffic.txBytes
                this[DataTraffics.mobileRxBytes] = it.dataTraffic.mobileRxBytes
                this[DataTraffics.mobileTxBytes] = it.dataTraffic.mobileTxBytes
            }
            is Wifis -> {
                this[Wifis.timestamp] = it.timestamp
                this[Wifis.utcOffset] = it.utcOffset
                this[Wifis.subjectEmail] = it.subjectEmail
                this[Wifis.deviceInfo] = it.deviceInfo
                this[Wifis.uploadTime] = uploadTime
                this[Wifis.bssid] = it.wifi.bssid
                this[Wifis.ssid] = it.wifi.ssid
                this[Wifis.frequency] = it.wifi.frequency
                this[Wifis.rssi] = it.wifi.rssi
            }
        }
    }
}
