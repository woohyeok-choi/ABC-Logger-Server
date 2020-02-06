package kaist.iclab.abclogger

import kaist.iclab.abclogger.DB.Companion.SCHEMA_NAME
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

private fun Table.defLong(name: String, def: Long = 0) = long(name).default(def)
private fun Table.defInteger(name: String, def: Int = 0) = integer(name).default(def)
private fun Table.defFloat(name: String, def: Float = 0F) = float(name).default(def)
private fun Table.defDouble(name: String, def: Double = 0.0) = double(name).default(def)
private fun Table.defBool(name: String, def: Boolean = false) = bool(name).default(def)
private fun Table.defText(name: String, collate: String? = null, def: String = "") = text(name, collate).default(def)
private fun Table.defVarchar(name: String, length: Int, collate: String? = null, def: String = "") = varchar(name, length, collate).default(def)

open class BaseTable(name: String) : LongIdTable(name = name) {
    val timestamp: Column<Long> = defLong("timestamp")
    val utcOffset: Column<Float> = defFloat("utc_offset")
    val subjectEmail: Column<String> = defVarchar("subject_email", 256)
    val deviceInfo: Column<String> = defVarchar("device_info", 256)
    val uploadTime: Column<Long> = defLong("upload_time")
}

object PhysicalActivityTransitions : BaseTable("$SCHEMA_NAME.physical_activity_transition") {
    val type: Column<String> = defVarchar("type", 512)
    val isEntered: Column<Boolean> = defBool("is_entered").default(false)
}

object PhysicalActivities : BaseTable("$SCHEMA_NAME.physical_activity") {
    val type: Column<String> = defVarchar("type", 512)
    val confidence: Column<Int> = defInteger("confidence").default(0)
}

object AppUsageEvents : BaseTable("$SCHEMA_NAME.app_usage_event") {
    val name: Column<String> = defVarchar("name", 256)
    val packageName: Column<String> = defVarchar("package_name", 1024)
    val type: Column<String> = defVarchar("type", 256)
    val isSystemApp: Column<Boolean> = defBool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = defBool("is_updated_system_app")
}

object Batteries : BaseTable("$SCHEMA_NAME.battery") {
    val level: Column<Int> = defInteger("level")
    val scale: Column<Int> = defInteger("scale")
    val temperature: Column<Int> = defInteger("temperature")
    val voltage: Column<Int> = defInteger("voltage")
    val health: Column<String> = defVarchar("health", 256)
    val pluggedType: Column<String> = defVarchar("plugged_type", 256)
    val status: Column<String> = defVarchar("status", 256)
}

object Bluetoothes : BaseTable("$SCHEMA_NAME.bluetooth") {
    val deviceName: Column<String> = defVarchar("device_name", 256)
    val address: Column<String> = defVarchar("address", 256)
    val rssi: Column<Int> = defInteger("rssi")
}

object CallLogs : BaseTable("$SCHEMA_NAME.call_log") {
    val duration: Column<Long> = defLong("duration")
    val number: Column<String> = defVarchar("number", 512)
    val type: Column<String> = defVarchar("type", 256)
    val dataUsage: Column<Long> = defLong("data_usage")
    val presentation: Column<String> = defVarchar("presentation", 256)
    val contactType: Column<String> = defVarchar("contact_type", 256)
    val isStarred: Column<Boolean> = defBool("is_starred")
    val isPinned: Column<Boolean> = defBool("is_pinned")
}

object DeviceEvents : BaseTable("$SCHEMA_NAME.device_event") {
    val type: Column<String> = defVarchar("type", 512)
}

object ExternalSensors : BaseTable("$SCHEMA_NAME.external_sensor") {
    val sensorId: Column<String> = defVarchar("sensor_id", 512)
    val name: Column<String> = defVarchar("name", 512)
    val description: Column<String> = defVarchar("description", 1024)
    val firstValue: Column<Float> = defFloat("first_value")
    val secondValue: Column<Float> = defFloat("second_value")
    val thirdValue: Column<Float> = defFloat("third_value")
    val fourthValue: Column<Float> = defFloat("fourth_value")
    val collection: Column<String> = defText("collection")
}

object InstalledApps : BaseTable("$SCHEMA_NAME.installed_app") {
    val name: Column<String> = defVarchar("name", 256)
    val packageName: Column<String> = defVarchar("package_name", 1024)
    val isSystemApp: Column<Boolean> = defBool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = defBool("is_updated_system_app")
    val firstInstallTime: Column<Long> = defLong("first_install_time")
    val lastUpdateTime: Column<Long> = defLong("last_update_time")
}

object InternalSensors : BaseTable("$SCHEMA_NAME.internal_sensor") {
    val type: Column<String> = defVarchar("sensor_id", 256)
    val accuracy: Column<String> = defVarchar("accuracy", 256)
    val firstValue: Column<Float> = defFloat("first_value")
    val secondValue: Column<Float> = defFloat("second_value")
    val thirdValue: Column<Float> = defFloat("third_value")
    val fourthValue: Column<Float> = defFloat("fourth_value")
}

object KeyLogs : BaseTable("$SCHEMA_NAME.key_log") {
    val name: Column<String> = defVarchar("name", 256)
    val packageName: Column<String> = defVarchar("package_name", 1024)
    val isSystemApp: Column<Boolean> = defBool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = defBool("is_updated_system_app")
    val distance: Column<Float> = defFloat("distance")
    val timeTaken: Column<Long> = defLong("time_taken")
    val keyboardType: Column<String> = defVarchar("keyboard_type", 512)
    val prevKey: Column<String> = defVarchar("prev_key", 16)
    val currentKey: Column<String> = defVarchar("current_key", 16)
    val prevKeyType: Column<String> = defVarchar("prev_key_type", 128)
    val currentKeyType: Column<String> = defVarchar("current_key_type", 128)
}

object Locations : BaseTable("$SCHEMA_NAME.location") {
    val latitude: Column<Double> = defDouble("latitude")
    val longitude: Column<Double> = defDouble("longitude")
    val altitude: Column<Double> = defDouble("altitude")
    val accuracy: Column<Float> = defFloat("accuracy")
    val speed: Column<Float> = defFloat("speed")
}

object Medias : BaseTable("$SCHEMA_NAME.media") {
    val mimeType: Column<String> = defVarchar("mime_type", 256)
}

object Messages : BaseTable("$SCHEMA_NAME.message") {
    val number: Column<String> = defVarchar("number", 512)
    val messageClass: Column<String> = defVarchar("message_class", 256)
    val messageBox: Column<String> = defVarchar("message_box", 256)
    val contactType: Column<String> = defVarchar("contact_type", 256)
    val isStarred: Column<Boolean> = defBool("is_starred")
    val isPinned: Column<Boolean> = defBool("is_pinned")
}

object Notifications : BaseTable("$SCHEMA_NAME.notification") {
    val name: Column<String> = defVarchar("name", 256)
    val packageName: Column<String> = defVarchar("package_name", 1024)
    val isSystemApp: Column<Boolean> = defBool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = defBool("is_updated_system_app")
    val title: Column<String> = defVarchar("title", 1024)
    val visibility: Column<String> = defVarchar("visibility", 256)
    val category: Column<String> = defVarchar("category", 256)
    val vibrate: Column<String> = defVarchar("vibrate", 256)
    val sound: Column<String> = defVarchar("sound", 512)
    val lightColor: Column<String> = defVarchar("light_color", 256)
    val isPosted: Column<Boolean> = defBool("is_posted")
}

object PhysicalStats : BaseTable("$SCHEMA_NAME.physical_stat") {
    val type: Column<String> = defVarchar("type", 256)
    val startTime: Column<Long> = defLong("start_time")
    val endTime: Column<Long> = defLong("end_time")
    val value: Column<Float> = defFloat("value")
}

object Surveys : BaseTable("$SCHEMA_NAME.survey") {
    val title: Column<String> = defText("title")
    val message: Column<String> = defText("message")
    val timeoutPolicy: Column<String> = defVarchar("timeout_policy", 256)
    val timeoutSec: Column<Long> = defLong("timeout_sec")
    val deliveredTime: Column<Long> = defLong("delivered_time")
    val reactionTime: Column<Long> = defLong("reaction_time")
    val responseTime: Column<Long> = defLong("response_time")
    val json: Column<String> = defText("json")
}

object DataTraffics : BaseTable("$SCHEMA_NAME.data_traffic") {
    val fromTime: Column<Long> = defLong("from_time")
    val toTime: Column<Long> = defLong("to_time")
    val rxBytes: Column<Long> = defLong("rx_bytes")
    val txBytes: Column<Long> = defLong("tx_bytes")
    val mobileRxBytes: Column<Long> = defLong("mobile_rx_bytes")
    val mobileTxBytes: Column<Long> = defLong("mobile_tx_bytes")
}

object Wifis: BaseTable("$SCHEMA_NAME.wifi") {
    val bssid: Column<String> = defVarchar("bssid", 1024)
    val ssid: Column<String> = defVarchar("ssid", 1024)
    val frequency: Column<Int> = defInteger("frequency")
    val rssi: Column<Int> = defInteger("rrsi")
}

