package kaist.iclab.abclogger

import kaist.iclab.abclogger.DB.Companion.SCHEMA_NAME
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column


open class BaseTable(name: String) : LongIdTable(name = name) {
    val timestamp: Column<Long> = long("timestamp")
    val utcOffset: Column<Float> = float("utc_offset")
    val subjectEmail: Column<String> = varchar("subject_email", 256)
    val deviceInfo: Column<String> = varchar("device_info", 256)
    val uploadTime: Column<Long> = long("upload_time")
}

object PhysicalActivityTransitions : BaseTable("$SCHEMA_NAME.physical_activity_transition") {
    val type: Column<String> = varchar("type", 512)
    val isEntered: Column<Boolean> = bool("is_entered")
}

object PhysicalActivities : BaseTable("$SCHEMA_NAME.physical_activity") {
    val type: Column<String> = varchar("type", 512)
    val confidence: Column<Int> = integer("confidence")
}

object AppUsageEvents : BaseTable("$SCHEMA_NAME.app_usage_event") {
    val name: Column<String> = varchar("name", 256)
    val packageName: Column<String> = varchar("package_name", 1024)
    val type: Column<String> = varchar("type", 256)
    val isSystemApp: Column<Boolean> = bool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = bool("is_updated_system_app")
}

object Batteries : BaseTable("$SCHEMA_NAME.battery") {
    val level: Column<Int> = integer("level")
    val scale: Column<Int> = integer("scale")
    val temperature: Column<Int> = integer("temperature")
    val voltage: Column<Int> = integer("voltage")
    val health: Column<String> = varchar("health", 256)
    val pluggedType: Column<String> = varchar("plugged_type", 256)
    val status: Column<String> = varchar("status", 256)
}

object Bluetoothes : BaseTable("$SCHEMA_NAME.bluetooth") {
    val deviceName: Column<String> = varchar("device_name", 256)
    val address: Column<String> = varchar("address", 256)
    val rssi: Column<Int> = integer("rssi")
}

object CallLogs : BaseTable("$SCHEMA_NAME.call_log") {
    val duration: Column<Long> = long("duration")
    val number: Column<String> = varchar("number", 512)
    val type: Column<String> = varchar("type", 256)
    val dataUsage: Column<Long> = long("data_usage")
    val presentation: Column<String> = varchar("presentation", 256)
    val contactType: Column<String> = varchar("contact_type", 256)
    val isStarred: Column<Boolean> = bool("is_starred")
    val isPinned: Column<Boolean> = bool("is_pinned")
}

object DeviceEvents : BaseTable("$SCHEMA_NAME.device_event") {
    val type: Column<String> = varchar("type", 512)
}

object ExternalSensors : BaseTable("$SCHEMA_NAME.external_sensor") {
    val sensorId: Column<String> = varchar("sensor_id", 512)
    val name: Column<String> = varchar("name", 512)
    val description: Column<String> = varchar("description", 1024)
    val firstValue: Column<Float> = float("first_value")
    val secondValue: Column<Float> = float("second_value")
    val thirdValue: Column<Float> = float("third_value")
    val fourthValue: Column<Float> = float("fourth_value")
}

object InstalledApps : BaseTable("$SCHEMA_NAME.installed_app") {
    val name: Column<String> = varchar("name", 256)
    val packageName: Column<String> = varchar("package_name", 1024)
    val isSystemApp: Column<Boolean> = bool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = bool("is_updated_system_app")
    val firstInstallTime: Column<Long> = long("first_install_time")
    val lastUpdateTime: Column<Long> = long("last_update_time")
}

object InternalSensors : BaseTable("$SCHEMA_NAME.internal_sensor") {
    val type: Column<String> = varchar("sensor_id", 256)
    val accuracy: Column<String> = varchar("name", 256)
    val firstValue: Column<Float> = float("first_value")
    val secondValue: Column<Float> = float("second_value")
    val thirdValue: Column<Float> = float("third_value")
    val fourthValue: Column<Float> = float("fourth_value")
}

object KeyLogs : BaseTable("$SCHEMA_NAME.key_log") {
    val name: Column<String> = varchar("name", 256)
    val packageName: Column<String> = varchar("package_name", 1024)
    val isSystemApp: Column<Boolean> = bool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = bool("is_updated_system_app")
    val distance: Column<Float> = float("distance")
    val timeTaken: Column<Long> = long("time_taken")
    val keyboardType: Column<String> = varchar("keyboard_type", 512)
    val prevKey: Column<String> = varchar("prev_key", 16)
    val currentKey: Column<String> = varchar("current_key", 16)
    val prevKeyType: Column<String> = varchar("prev_key_type", 128)
    val currentKeyType: Column<String> = varchar("current_key_type", 128)
}

object Locations : BaseTable("$SCHEMA_NAME.location") {
    val latitude: Column<Double> = double("latitude")
    val longitude: Column<Double> = double("longitude")
    val altitude: Column<Double> = double("altitude")
    val accuracy: Column<Float> = float("accuracy")
    val speed: Column<Float> = float("speed")
}

object Medias : BaseTable("$SCHEMA_NAME.media") {
    val mimeType: Column<String> = varchar("mime_type", 256)
}

object Messages : BaseTable("$SCHEMA_NAME.message") {
    val number: Column<String> = varchar("number", 512)
    val messageClass: Column<String> = varchar("message_class", 256)
    val messageBox: Column<String> = varchar("message_box", 256)
    val contactType: Column<String> = varchar("contact_type", 256)
    val isStarred: Column<Boolean> = bool("is_starred")
    val isPinned: Column<Boolean> = bool("is_pinned")
}

object Notifications : BaseTable("$SCHEMA_NAME.notification") {
    val name: Column<String> = varchar("name", 256)
    val packageName: Column<String> = varchar("package_name", 1024)
    val isSystemApp: Column<Boolean> = bool("is_system_app")
    val isUpdatedSystemApp: Column<Boolean> = bool("is_updated_system_app")
    val title: Column<String> = varchar("title", 1024)
    val visibility: Column<String> = varchar("visibility", 256)
    val category: Column<String> = varchar("category", 256)
    val vibrate: Column<String> = varchar("vibrate", 256)
    val sound: Column<String> = varchar("sound", 512)
    val lightColor: Column<String> = varchar("light_color", 256)
    val isPosted: Column<Boolean> = bool("is_posted")
}

object PhysicalStats : BaseTable("$SCHEMA_NAME.physical_stat") {
    val type: Column<String> = varchar("type", 256)
    val startTime: Column<Long> = long("start_time")
    val endTime: Column<Long> = long("end_time")
    val value: Column<Float> = float("value")
}

object Surveys : BaseTable("$SCHEMA_NAME.survey") {
    val title: Column<String> = text("title")
    val message: Column<String> = text("message")
    val timeoutPolicy: Column<String> = varchar("timeout_policy", 256)
    val timeoutSec: Column<Long> = long("timeout_sec")
    val deliveredTime: Column<Long> = long("delivered_time")
    val reactionTime: Column<Long> = long("reaction_time")
    val responseTime: Column<Long> = long("response_time")
    val json: Column<String> = text("json")
}

object DataTraffics : BaseTable("$SCHEMA_NAME.data_traffic") {
    val fromTime: Column<Long> = long("from_time")
    val toTime: Column<Long> = long("to_time")
    val rxBytes: Column<Long> = long("rx_bytes")
    val txBytes: Column<Long> = long("tx_bytes")
    val mobileRxBytes: Column<Long> = long("mobile_rx_bytes")
    val mobileTxBytes: Column<Long> = long("mobile_tx_bytes")
}

object Wifis: BaseTable("$SCHEMA_NAME.wifi") {
    val bssid: Column<String> = varchar("bssid", 1024)
    val ssid: Column<String> = varchar("ssid", 1024)
    val frequency: Column<Int> = integer("frequency")
    val rssi: Column<Int> = integer("rrsi")
}

