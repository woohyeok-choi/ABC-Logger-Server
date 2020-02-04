package kaist.iclab.abclogger

import io.grpc.Status
import kaist.iclab.abclogger.grpc.DatumProto
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DBReader(private val db: Database, private val mapper: (DatumProto.Datum.Type) -> BaseTable?) {
    suspend fun readSubjects(dataType: DatumProto.Datum.Type,
                             fromTime: Long,
                             toTime: Long,
                             limit: Int,
                             isDescending: Boolean): List<String> {
        val table = mapper(dataType)
                ?: throw Status.INVALID_ARGUMENT.withDescription("a field, 'dataType' is invalid: ${dataType.name}").asRuntimeException()

        return newSuspendedTransaction(context = Dispatchers.IO, db = db) {
            table.slice(table.subjectEmail)
                    .select { (table.timestamp greaterEq fromTime) and (table.timestamp less toTime) }
                    .limit(limit)
                    .orderBy(table.timestamp, if (isDescending) SortOrder.DESC else SortOrder.ASC)
                    .withDistinct()
                    .map { row -> row[table.subjectEmail] }
        }
    }

    suspend fun readData(dataType: DatumProto.Datum.Type,
                         subjectEmail: String,
                         fromTime: Long,
                         toTime: Long,
                         limit: Int,
                         isDescending: Boolean) {
    }

    private fun dataMapper(tableName: String, row: ResultRow): DatumProto.Datum? =
            when (tableName) {
                PhysicalActivityTransitions.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[PhysicalActivityTransitions.timestamp]
                        utcOffset = row[PhysicalActivityTransitions.utcOffset]
                        subjectEmail = row[PhysicalActivityTransitions.subjectEmail]
                        deviceInfo = row[PhysicalActivityTransitions.deviceInfo]
                        uploadTime = row[PhysicalActivityTransitions.uploadTime]
                        physicalActivityTransition = DatumProto.Datum.PhysicalActivityTransition.newBuilder().apply {
                            type = row[PhysicalActivityTransitions.type]
                            isEntered = row[PhysicalActivityTransitions.isEntered]
                        }.build()
                    }.build()
                }
                PhysicalActivities.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[PhysicalActivities.timestamp]
                        utcOffset = row[PhysicalActivities.utcOffset]
                        subjectEmail = row[PhysicalActivities.subjectEmail]
                        deviceInfo = row[PhysicalActivities.deviceInfo]
                        uploadTime = row[PhysicalActivities.uploadTime]
                        physicalActivity = DatumProto.Datum.PhysicalActivity.newBuilder().apply {
                            type = row[PhysicalActivities.type]
                            confidence = row[PhysicalActivities.confidence]
                        }.build()
                    }.build()
                }
                AppUsageEvents.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[AppUsageEvents.timestamp]
                        utcOffset = row[AppUsageEvents.utcOffset]
                        subjectEmail = row[AppUsageEvents.subjectEmail]
                        deviceInfo = row[AppUsageEvents.deviceInfo]
                        uploadTime = row[AppUsageEvents.uploadTime]
                        appUsageEvent = DatumProto.Datum.AppUsageEvent.newBuilder().apply {
                            name = row[AppUsageEvents.name]
                            packageName = row[AppUsageEvents.packageName]
                            type = row[AppUsageEvents.type]
                            isSystemApp = row[AppUsageEvents.isSystemApp]
                            isUpdatedSystemApp = row[AppUsageEvents.isUpdatedSystemApp]
                        }.build()
                    }.build()
                }
                Batteries.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Batteries.timestamp]
                        utcOffset = row[Batteries.utcOffset]
                        subjectEmail = row[Batteries.subjectEmail]
                        deviceInfo = row[Batteries.deviceInfo]
                        uploadTime = row[Batteries.uploadTime]
                        battery = DatumProto.Datum.Battery.newBuilder().apply {
                            level = row[Batteries.level]
                            scale = row[Batteries.scale]
                            temperature = row[Batteries.temperature]
                            voltage = row[Batteries.voltage]
                            health = row[Batteries.health]
                            pluggedType = row[Batteries.pluggedType]
                            status = row[Batteries.status]
                        }.build()
                    }.build()
                }
                Bluetoothes.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Bluetoothes.timestamp]
                        utcOffset = row[Bluetoothes.utcOffset]
                        subjectEmail = row[Bluetoothes.subjectEmail]
                        deviceInfo = row[Bluetoothes.deviceInfo]
                        uploadTime = row[Bluetoothes.uploadTime]
                        bluetooth = DatumProto.Datum.Bluetooth.newBuilder().apply {
                            deviceName = row[Bluetoothes.deviceName]
                            address = row[Bluetoothes.address]
                            rssi = row[Bluetoothes.rssi]
                        }.build()
                    }.build()
                }
                CallLogs.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[CallLogs.timestamp]
                        utcOffset = row[CallLogs.utcOffset]
                        subjectEmail = row[CallLogs.subjectEmail]
                        deviceInfo = row[CallLogs.deviceInfo]
                        uploadTime = row[CallLogs.uploadTime]
                        callLog = DatumProto.Datum.CallLog.newBuilder().apply {
                            duration = row[CallLogs.duration]
                            number = row[CallLogs.number]
                            type = row[CallLogs.type]
                            presentation = row[CallLogs.presentation]
                            dataUsage = row[CallLogs.dataUsage]
                            contactType = row[CallLogs.contactType]
                            isStarred = row[CallLogs.isStarred]
                            isPinned = row[CallLogs.isPinned]
                        }.build()
                    }.build()
                }
                DeviceEvents.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[DeviceEvents.timestamp]
                        utcOffset = row[DeviceEvents.utcOffset]
                        subjectEmail = row[DeviceEvents.subjectEmail]
                        deviceInfo = row[DeviceEvents.deviceInfo]
                        uploadTime = row[DeviceEvents.uploadTime]
                        deviceEvent = DatumProto.Datum.DeviceEvent.newBuilder().apply {
                            type = row[DeviceEvents.type]
                        }.build()
                    }.build()
                }
                ExternalSensors.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[ExternalSensors.timestamp]
                        utcOffset = row[ExternalSensors.utcOffset]
                        subjectEmail = row[ExternalSensors.subjectEmail]
                        deviceInfo = row[ExternalSensors.deviceInfo]
                        uploadTime = row[ExternalSensors.uploadTime]
                        externalSensor = DatumProto.Datum.ExternalSensor.newBuilder().apply {
                            sensorId = row[ExternalSensors.sensorId]
                            name = row[ExternalSensors.name]
                            description = row[ExternalSensors.description]
                            firstValue = row[ExternalSensors.firstValue]
                            secondValue = row[ExternalSensors.secondValue]
                            thirdValue = row[ExternalSensors.thirdValue]
                            fourthValue = row[ExternalSensors.fourthValue]
                        }.build()
                    }.build()
                }
                InstalledApps.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[InstalledApps.timestamp]
                        utcOffset = row[InstalledApps.utcOffset]
                        subjectEmail = row[InstalledApps.subjectEmail]
                        deviceInfo = row[InstalledApps.deviceInfo]
                        uploadTime = row[InstalledApps.uploadTime]
                        installedApp = DatumProto.Datum.InstalledApp.newBuilder().apply {
                            name = row[InstalledApps.name]
                            packageName = row[InstalledApps.packageName]
                            isSystemApp = row[InstalledApps.isSystemApp]
                            isUpdatedSystemApp = row[InstalledApps.isUpdatedSystemApp]
                            firstInstallTime = row[InstalledApps.firstInstallTime]
                            lastUpdateTime = row[InstalledApps.lastUpdateTime]
                        }.build()
                    }.build()
                }
                InternalSensors.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[InternalSensors.timestamp]
                        utcOffset = row[InternalSensors.utcOffset]
                        subjectEmail = row[InternalSensors.subjectEmail]
                        deviceInfo = row[InternalSensors.deviceInfo]
                        uploadTime = row[InternalSensors.uploadTime]
                        internalSensor = DatumProto.Datum.InternalSensor.newBuilder().apply {
                            type = row[InternalSensors.type]
                            accuracy = row[InternalSensors.accuracy]
                            firstValue = row[InternalSensors.firstValue]
                            secondValue = row[InternalSensors.secondValue]
                            thirdValue = row[InternalSensors.thirdValue]
                            fourthValue = row[InternalSensors.fourthValue]
                        }.build()
                    }.build()
                }
                KeyLogs.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[KeyLogs.timestamp]
                        utcOffset = row[KeyLogs.utcOffset]
                        subjectEmail = row[KeyLogs.subjectEmail]
                        deviceInfo = row[KeyLogs.deviceInfo]
                        uploadTime = row[KeyLogs.uploadTime]
                        keyLog = DatumProto.Datum.KeyLog.newBuilder().apply {
                            name = row[KeyLogs.name]
                            packageName = row[KeyLogs.packageName]
                            isSystemApp = row[KeyLogs.isSystemApp]
                            isUpdatedSystemApp = row[KeyLogs.isUpdatedSystemApp]
                            distance = row[KeyLogs.distance]
                            timeTaken = row[KeyLogs.timeTaken]
                            keyboardType = row[KeyLogs.keyboardType]
                            prevKey = row[KeyLogs.prevKey]
                            currentKey = row[KeyLogs.currentKey]
                            prevKeyType = row[KeyLogs.prevKeyType]
                            currentKeyType = row[KeyLogs.currentKeyType]
                        }.build()
                    }.build()
                }
                Locations.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Locations.timestamp]
                        utcOffset = row[Locations.utcOffset]
                        subjectEmail = row[Locations.subjectEmail]
                        deviceInfo = row[Locations.deviceInfo]
                        uploadTime = row[Locations.uploadTime]
                        location = DatumProto.Datum.Location.newBuilder().apply {
                            latitude = row[Locations.latitude]
                            longitude = row[Locations.longitude]
                            altitude = row[Locations.altitude]
                            accuracy = row[Locations.accuracy]
                            speed = row[Locations.speed]
                        }.build()
                    }.build()
                }
                Medias.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Medias.timestamp]
                        utcOffset = row[Medias.utcOffset]
                        subjectEmail = row[Medias.subjectEmail]
                        deviceInfo = row[Medias.deviceInfo]
                        uploadTime = row[Medias.uploadTime]
                        media = DatumProto.Datum.Media.newBuilder().apply {
                            mimeType = row[Medias.mimeType]
                        }.build()
                    }.build()
                }
                Messages.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Messages.timestamp]
                        utcOffset = row[Messages.utcOffset]
                        subjectEmail = row[Messages.subjectEmail]
                        deviceInfo = row[Messages.deviceInfo]
                        uploadTime = row[Messages.uploadTime]
                        message = DatumProto.Datum.Message.newBuilder().apply {
                            number = row[Messages.number]
                            messageClass = row[Messages.messageClass]
                            messageBox = row[Messages.messageBox]
                            contactType = row[Messages.contactType]
                            isStarred = row[Messages.isStarred]
                            isPinned = row[Messages.isPinned]
                        }.build()
                    }.build()
                }
                Notifications.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Notifications.timestamp]
                        utcOffset = row[Notifications.utcOffset]
                        subjectEmail = row[Notifications.subjectEmail]
                        deviceInfo = row[Notifications.deviceInfo]
                        uploadTime = row[Notifications.uploadTime]
                        notification = DatumProto.Datum.Notification.newBuilder().apply {
                            name = row[Notifications.name]
                            packageName = row[Notifications.packageName]
                            isSystemApp = row[Notifications.isSystemApp]
                            isUpdatedSystemApp = row[Notifications.isUpdatedSystemApp]
                            title = row[Notifications.title]
                            visibility = row[Notifications.visibility]
                            category = row[Notifications.category]
                            vibrate = row[Notifications.vibrate]
                            sound = row[Notifications.sound]
                            lightColor = row[Notifications.lightColor]
                            isPosted = row[Notifications.isPosted]
                        }.build()
                    }.build()
                }
                PhysicalStats.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[PhysicalStats.timestamp]
                        utcOffset = row[PhysicalStats.utcOffset]
                        subjectEmail = row[PhysicalStats.subjectEmail]
                        deviceInfo = row[PhysicalStats.deviceInfo]
                        uploadTime = row[PhysicalStats.uploadTime]
                        physicalStat = DatumProto.Datum.PhysicalStat.newBuilder().apply {
                            type = row[PhysicalStats.type]
                            startTime = row[PhysicalStats.startTime]
                            endTime = row[PhysicalStats.endTime]
                            value = row[PhysicalStats.value]
                        }.build()
                    }.build()
                }
                Surveys.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Surveys.timestamp]
                        utcOffset = row[Surveys.utcOffset]
                        subjectEmail = row[Surveys.subjectEmail]
                        deviceInfo = row[Surveys.deviceInfo]
                        uploadTime = row[Surveys.uploadTime]
                        survey = DatumProto.Datum.Survey.newBuilder().apply {
                            title = row[Surveys.title]
                            message = row[Surveys.message]
                            timeoutPolicy = row[Surveys.timeoutPolicy]
                            timeoutSec = row[Surveys.timeoutSec]
                            deliveredTime = row[Surveys.deliveredTime]
                            reactionTime = row[Surveys.reactionTime]
                            responseTime = row[Surveys.responseTime]
                            json = row[Surveys.json]
                        }.build()
                    }.build()
                }
                DataTraffics.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[DataTraffics.timestamp]
                        utcOffset = row[DataTraffics.utcOffset]
                        subjectEmail = row[DataTraffics.subjectEmail]
                        deviceInfo = row[DataTraffics.deviceInfo]
                        uploadTime = row[DataTraffics.uploadTime]
                        dataTraffic = DatumProto.Datum.DataTraffic.newBuilder().apply {
                            fromTime = row[DataTraffics.fromTime]
                            toTime = row[DataTraffics.toTime]
                            rxBytes = row[DataTraffics.rxBytes]
                            txBytes = row[DataTraffics.txBytes]
                            mobileRxBytes = row[DataTraffics.mobileRxBytes]
                            mobileTxBytes = row[DataTraffics.mobileTxBytes]
                        }.build()
                    }.build()
                }
                Wifis.tableName -> {
                    DatumProto.Datum.newBuilder().apply {
                        timestamp = row[Wifis.timestamp]
                        utcOffset = row[Wifis.utcOffset]
                        subjectEmail = row[Wifis.subjectEmail]
                        deviceInfo = row[Wifis.deviceInfo]
                        uploadTime = row[Wifis.uploadTime]
                        wifi = DatumProto.Datum.Wifi.newBuilder().apply {
                            bssid = row[Wifis.bssid]
                            ssid = row[Wifis.ssid]
                            frequency = row[Wifis.frequency]
                            rssi = row[Wifis.rssi]
                        }.build()
                    }.build()
                }
                else -> null
            }

}