package kaist.iclab.abclogger

import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.schema.*

class DatabaseReader(private val database: Database) {
    private fun associateIntoTable(type: CommonProtos.DataType?) = when (type) {
        CommonProtos.DataType.PHYSICAL_ACTIVITY_TRANSITION -> PhysicalActivityTransition
        CommonProtos.DataType.PHYSICAL_ACTIVITY -> PhysicalActivity
        CommonProtos.DataType.APP_USAGE_EVENT -> AppUsageEvent
        CommonProtos.DataType.BATTERY -> Battery
        CommonProtos.DataType.BLUETOOTH -> Bluetooth
        CommonProtos.DataType.CALL_LOG -> CallLog
        CommonProtos.DataType.DEVICE_EVENT -> DeviceEvent
        CommonProtos.DataType.SENSOR -> Sensor
        CommonProtos.DataType.INSTALLED_APP -> InstalledApp
        CommonProtos.DataType.KEY_LOG -> KeyLog
        CommonProtos.DataType.LOCATION -> Location
        CommonProtos.DataType.MEDIA -> Media
        CommonProtos.DataType.MESSAGE -> Message
        CommonProtos.DataType.NOTIFICATION -> Notification
        CommonProtos.DataType.PHYSICAL_STAT -> PhysicalStat
        CommonProtos.DataType.SURVEY -> Survey
        CommonProtos.DataType.DATA_TRAFFIC -> DataTraffic
        CommonProtos.DataType.WIFI -> Wifi
        else -> null
    }

    fun readData(fromTimestamp: Long?, toTimestamp: Long?) {
        database.collection<Datum>().find().
    }


}