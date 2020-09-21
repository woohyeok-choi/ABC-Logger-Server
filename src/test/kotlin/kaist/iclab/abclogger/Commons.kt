package kaist.iclab.abclogger

import com.google.protobuf.ByteString
import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Message
import io.grpc.*
import kaist.iclab.abclogger.grpc.proto.CommonProtos
import kaist.iclab.abclogger.grpc.proto.DataProtos
import kaist.iclab.abclogger.grpc.proto.HeartBeatProtos
import kaist.iclab.abclogger.schema.UNKNOWN_BOOLEAN
import kaist.iclab.abclogger.schema.UNKNOWN_LONG
import kaist.iclab.abclogger.schema.UNKNOWN_STRING
import java.time.OffsetDateTime

fun buildDatum(
        dataType: CommonProtos.DataType,
        timestamp: Long,
        email: String,
        deviceInfo: String = email,
        deviceId: String = email
): DataProtos.Datum {
    val datumField = when (dataType) {
        CommonProtos.DataType.PHYSICAL_ACTIVITY_TRANSITION -> DataProtos.Datum.PHYSICAL_ACTIVITY_TRANSITION_FIELD_NUMBER
        CommonProtos.DataType.PHYSICAL_ACTIVITY -> DataProtos.Datum.PHYSICAL_ACTIVITY_FIELD_NUMBER
        CommonProtos.DataType.APP_USAGE_EVENT -> DataProtos.Datum.APP_USAGE_EVENT_FIELD_NUMBER
        CommonProtos.DataType.BATTERY -> DataProtos.Datum.BATTERY_FIELD_NUMBER
        CommonProtos.DataType.BLUETOOTH -> DataProtos.Datum.BLUETOOTH_FIELD_NUMBER
        CommonProtos.DataType.CALL_LOG -> DataProtos.Datum.CALL_LOG_FIELD_NUMBER
        CommonProtos.DataType.DEVICE_EVENT -> DataProtos.Datum.DEVICE_EVENT_FIELD_NUMBER
        CommonProtos.DataType.EMBEDDED_SENSOR -> DataProtos.Datum.EMBEDDED_SENSOR_FIELD_NUMBER
        CommonProtos.DataType.EXTERNAL_SENSOR -> DataProtos.Datum.EXTERNAL_SENSOR_FIELD_NUMBER
        CommonProtos.DataType.INSTALLED_APP -> DataProtos.Datum.INSTALLED_APP_FIELD_NUMBER
        CommonProtos.DataType.KEY_LOG -> DataProtos.Datum.KEY_LOG_FIELD_NUMBER
        CommonProtos.DataType.LOCATION -> DataProtos.Datum.LOCATION_FIELD_NUMBER
        CommonProtos.DataType.MEDIA -> DataProtos.Datum.MEDIA_FIELD_NUMBER
        CommonProtos.DataType.MESSAGE -> DataProtos.Datum.MESSAGE_FIELD_NUMBER
        CommonProtos.DataType.NOTIFICATION -> DataProtos.Datum.NOTIFICATION_FIELD_NUMBER
        CommonProtos.DataType.PHYSICAL_STAT -> DataProtos.Datum.PHYSICAL_STAT_FIELD_NUMBER
        CommonProtos.DataType.SURVEY -> DataProtos.Datum.SURVEY_FIELD_NUMBER
        CommonProtos.DataType.DATA_TRAFFIC -> DataProtos.Datum.DATA_TRAFFIC_FIELD_NUMBER
        CommonProtos.DataType.WIFI -> DataProtos.Datum.WIFI_FIELD_NUMBER
        else -> null
    }?.let {
        DataProtos.Datum.getDescriptor().findFieldByNumber(it)
    }
    val datum = getDefaultDatumInstance(dataType)?.let { fillData(it) }

    return DataProtos.Datum.newBuilder().apply {
        this.timestamp = timestamp
        this.utcOffsetSec = OffsetDateTime.now().offset.totalSeconds
        this.email = email
        this.deviceInfo = deviceInfo
        this.deviceId = deviceId

        if (datumField != null && datum != null) this.setField(datumField, datum)
    }.build()
}

private fun getDefaultDatumInstance(dataType: CommonProtos.DataType) = when (dataType) {
    CommonProtos.DataType.PHYSICAL_ACTIVITY_TRANSITION ->
        DataProtos.PhysicalActivityTransition.getDefaultInstance()
    CommonProtos.DataType.PHYSICAL_ACTIVITY ->
        DataProtos.PhysicalActivity.getDefaultInstance()
    CommonProtos.DataType.APP_USAGE_EVENT ->
        DataProtos.AppUsageEvent.getDefaultInstance()
    CommonProtos.DataType.BATTERY ->
        DataProtos.Battery.getDefaultInstance()
    CommonProtos.DataType.BLUETOOTH ->
        DataProtos.Bluetooth.getDefaultInstance()
    CommonProtos.DataType.CALL_LOG ->
        DataProtos.CallLog.getDefaultInstance()
    CommonProtos.DataType.DEVICE_EVENT ->
        DataProtos.DeviceEvent.getDefaultInstance()
    CommonProtos.DataType.EMBEDDED_SENSOR ->
        DataProtos.EmbeddedSensor.getDefaultInstance()
    CommonProtos.DataType.EXTERNAL_SENSOR ->
        DataProtos.ExternalSensor.getDefaultInstance()
    CommonProtos.DataType.INSTALLED_APP ->
        DataProtos.InstalledApp.getDefaultInstance()
    CommonProtos.DataType.KEY_LOG ->
        DataProtos.KeyLog.getDefaultInstance()
    CommonProtos.DataType.LOCATION ->
        DataProtos.Location.getDefaultInstance()
    CommonProtos.DataType.MEDIA ->
        DataProtos.Media.getDefaultInstance()
    CommonProtos.DataType.MESSAGE ->
        DataProtos.Message.getDefaultInstance()
    CommonProtos.DataType.NOTIFICATION ->
        DataProtos.Notification.getDefaultInstance()
    CommonProtos.DataType.PHYSICAL_STAT ->
        DataProtos.PhysicalStat.getDefaultInstance()
    CommonProtos.DataType.SURVEY ->
        DataProtos.Survey.getDefaultInstance()
    CommonProtos.DataType.DATA_TRAFFIC ->
        DataProtos.DataTraffic.getDefaultInstance()
    CommonProtos.DataType.WIFI ->
        DataProtos.Wifi.getDefaultInstance()
    else -> null
}

fun buildHeartBeat(
        timestamp: Long,
        email: String,
        deviceInfo: String = email,
        deviceId: String = email
): HeartBeatProtos.HeartBeat {
    return HeartBeatProtos.HeartBeat.newBuilder().apply {
        this.timestamp = timestamp
        this.utcOffsetSec = OffsetDateTime.now().offset.totalSeconds
        this.email = email
        this.deviceInfo = deviceInfo
        this.deviceId = deviceId

        this.addAllStatus(CommonProtos.DataType.values().filter {
            it != CommonProtos.DataType.UNRECOGNIZED && it != CommonProtos.DataType.NOT_SPECIFIED
        }.map { dataType ->
            HeartBeatProtos.Status.newBuilder().apply {
                this.dataType = dataType
                this.lastTimeWritten = timestamp
                this.recordsRemained = 100
            }.build()
        })
    }.build()
}

private fun <T : GeneratedMessageV3> fillData(message: T): Message {
    val builder = message.newBuilderForType()
    message.descriptorForType.fields.forEach { fieldDescriptor ->
        val value: Any? = when (fieldDescriptor.javaType) {
            Descriptors.FieldDescriptor.JavaType.STRING -> "TEST_STRING"
            Descriptors.FieldDescriptor.JavaType.BYTE_STRING -> ByteString.copyFromUtf8("TEST_STRING")
            Descriptors.FieldDescriptor.JavaType.BOOLEAN -> true
            Descriptors.FieldDescriptor.JavaType.FLOAT -> 10.0F
            Descriptors.FieldDescriptor.JavaType.INT -> 50
            Descriptors.FieldDescriptor.JavaType.LONG -> 150L
            else -> null
        }

        if (value != null) {
            if (fieldDescriptor.isRepeated) {
                (0 until 10).forEach { builder.addRepeatedField(fieldDescriptor, "$it-th $value") }
            } else {
                builder.setField(fieldDescriptor, value)
            }
        }
    }
    return builder.build()
}

fun getHeaderClientInterceptor(authKey: String, authToken: String) = object : ClientInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?): ClientCall<ReqT, RespT> {
        val newCall = next!!.newCall(method, callOptions)

        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(newCall) {
            override fun start(responseListener: Listener<RespT>?, headers: Metadata?) {
                headers?.put(Metadata.Key.of(authKey, Metadata.ASCII_STRING_MARSHALLER), authToken)
                super.start(responseListener, headers)
            }
        }
    }
}
