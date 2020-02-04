package kaist.iclab.abclogger

import kaist.iclab.abclogger.grpc.DatumProto
import org.koin.dsl.module

val serverModule = module {
    fun protoToTableName(proto: DatumProto.Datum): String? =
            when {
                proto.hasPhysicalActivityTransition() -> PhysicalActivityTransitions.tableName
                proto.hasPhysicalActivity() -> PhysicalActivities.tableName
                proto.hasAppUsageEvent() -> AppUsageEvents.tableName
                proto.hasBattery() -> Batteries.tableName
                proto.hasBluetooth() -> Bluetoothes.tableName
                proto.hasCallLog() -> CallLogs.tableName
                proto.hasDeviceEvent() -> DeviceEvents.tableName
                proto.hasExternalSensor() -> ExternalSensors.tableName
                proto.hasInstalledApp() -> InstalledApps.tableName
                proto.hasKeyLog() -> KeyLogs.tableName
                proto.hasLocation() -> Locations.tableName
                proto.hasMedia() -> Medias.tableName
                proto.hasMessage() -> Messages.tableName
                proto.hasNotification() -> Notifications.tableName
                proto.hasPhysicalStat() -> PhysicalStats.tableName
                proto.hasInternalSensor() -> InternalSensors.tableName
                proto.hasSurvey() -> Surveys.tableName
                proto.hasDataTraffic() -> DataTraffics.tableName
                proto.hasWifi() -> Wifis.tableName
                else -> null
            }

    fun typeToTable(type: DatumProto.Datum.Type) =
        when (type) {
            DatumProto.Datum.Type.PHYSICAL_ACTIVITY_TRANSITION -> PhysicalActivityTransitions
            DatumProto.Datum.Type.PHYSICAL_ACTIVITY -> PhysicalActivities
            DatumProto.Datum.Type.APP_USAGE_EVENT -> AppUsageEvents
            DatumProto.Datum.Type.BATTERY -> Batteries
            DatumProto.Datum.Type.BLUETOOTH -> Bluetoothes
            DatumProto.Datum.Type.CALL_LOG -> CallLogs
            DatumProto.Datum.Type.DEVICE_EVENT -> DeviceEvents
            DatumProto.Datum.Type.EXTERNAL_SENSOR -> ExternalSensors
            DatumProto.Datum.Type.INSTALLED_APP -> InstalledApps
            DatumProto.Datum.Type.KEY_LOG -> KeyLogs
            DatumProto.Datum.Type.LOCATION -> Locations
            DatumProto.Datum.Type.MEDIA -> Medias
            DatumProto.Datum.Type.MESSAGE -> Messages
            DatumProto.Datum.Type.NOTIFICATION -> Notifications
            DatumProto.Datum.Type.PHYSICAL_STAT -> PhysicalStats
            DatumProto.Datum.Type.INTERNAL_SENSOR -> InternalSensors
            DatumProto.Datum.Type.SURVEY -> Surveys
            DatumProto.Datum.Type.DATA_TRAFFIC -> DataTraffics
            DatumProto.Datum.Type.WIFI -> Wifis
            else -> null
        }

    val tables = arrayOf(
            PhysicalActivityTransitions,
            PhysicalActivities,
            AppUsageEvents,
            Batteries,
            Bluetoothes,
            CallLogs,
            DeviceEvents,
            ExternalSensors,
            InstalledApps,
            InternalSensors,
            KeyLogs,
            Locations,
            Medias,
            Messages,
            Notifications,
            PhysicalStats,
            Surveys,
            DataTraffics,
            Wifis
    )

    single {
        DB(tables)
    }
    single {
        WriteBuffer(tables)
    }
    single {
        DataOperationService(get()) {
            protoToTableName(it)
        }
    }
    single { DBWriter(get<DB>().writeOnlyDb, get(), tables) }
}