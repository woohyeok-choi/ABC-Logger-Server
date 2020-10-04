package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.grpc.proto.DatumProtos

private val DATA_CASE_TO_DATA_TYPE = mapOf(
    DatumProtos.Datum.DataCase.PHYSICAL_ACTIVITY_TRANSITION to DatumProtos.Datum.Type.PHYSICAL_ACTIVITY_TRANSITION,
    DatumProtos.Datum.DataCase.PHYSICAL_ACTIVITY to DatumProtos.Datum.Type.PHYSICAL_ACTIVITY,
    DatumProtos.Datum.DataCase.APP_USAGE_EVENT to DatumProtos.Datum.Type.APP_USAGE_EVENT,
    DatumProtos.Datum.DataCase.BATTERY to DatumProtos.Datum.Type.BATTERY,
    DatumProtos.Datum.DataCase.BLUETOOTH to DatumProtos.Datum.Type.BLUETOOTH,
    DatumProtos.Datum.DataCase.CALL_LOG to DatumProtos.Datum.Type.CALL_LOG,
    DatumProtos.Datum.DataCase.DEVICE_EVENT to DatumProtos.Datum.Type.DEVICE_EVENT,
    DatumProtos.Datum.DataCase.EMBEDDED_SENSOR to DatumProtos.Datum.Type.EMBEDDED_SENSOR,
    DatumProtos.Datum.DataCase.EXTERNAL_SENSOR to DatumProtos.Datum.Type.EXTERNAL_SENSOR,
    DatumProtos.Datum.DataCase.INSTALLED_APP to DatumProtos.Datum.Type.INSTALLED_APP,
    DatumProtos.Datum.DataCase.KEY_LOG to DatumProtos.Datum.Type.KEY_LOG,
    DatumProtos.Datum.DataCase.LOCATION to DatumProtos.Datum.Type.LOCATION,
    DatumProtos.Datum.DataCase.MEDIA to DatumProtos.Datum.Type.MEDIA,
    DatumProtos.Datum.DataCase.MESSAGE to DatumProtos.Datum.Type.MESSAGE,
    DatumProtos.Datum.DataCase.NOTIFICATION to DatumProtos.Datum.Type.NOTIFICATION,
    DatumProtos.Datum.DataCase.FITNESS to DatumProtos.Datum.Type.FITNESS,
    DatumProtos.Datum.DataCase.SURVEY to DatumProtos.Datum.Type.SURVEY,
    DatumProtos.Datum.DataCase.DATA_TRAFFIC to DatumProtos.Datum.Type.DATA_TRAFFIC,
    DatumProtos.Datum.DataCase.WIFI to DatumProtos.Datum.Type.WIFI,
)

private val DATA_TYPE_TO_DATA_CASE = DATA_CASE_TO_DATA_TYPE.entries.associate { (k, v) -> v to k }

fun dataCaseToDataType(dataCase: DatumProtos.Datum.DataCase): DatumProtos.Datum.Type =
    DATA_CASE_TO_DATA_TYPE.getOrDefault(dataCase, DatumProtos.Datum.Type.UNRECOGNIZED)

fun dataTypeToDataCase(dataType: DatumProtos.Datum.Type): DatumProtos.Datum.DataCase =
    DATA_TYPE_TO_DATA_CASE.getOrDefault(dataType, DatumProtos.Datum.DataCase.DATA_NOT_SET)

inline fun <reified E : Enum<E>> safeEnumValuesOf(str: String?, default: E) = try {
    enumValueOf(str ?: "")
} catch (e: Exception) {
    default
}
