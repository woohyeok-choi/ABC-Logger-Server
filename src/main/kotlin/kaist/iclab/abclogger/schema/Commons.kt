package kaist.iclab.abclogger.schema

import kaist.iclab.abclogger.grpc.proto.DatumProtos

private val DATA_CASE_TO_DATA_TYPE = mapOf(
    DatumProtos.Datum.DataCase.PHYSICAL_ACTIVITY_TRANSITION to DatumProtos.DatumType.PHYSICAL_ACTIVITY_TRANSITION,
    DatumProtos.Datum.DataCase.PHYSICAL_ACTIVITY to DatumProtos.DatumType.PHYSICAL_ACTIVITY,
    DatumProtos.Datum.DataCase.APP_USAGE_EVENT to DatumProtos.DatumType.APP_USAGE_EVENT,
    DatumProtos.Datum.DataCase.BATTERY to DatumProtos.DatumType.BATTERY,
    DatumProtos.Datum.DataCase.BLUETOOTH to DatumProtos.DatumType.BLUETOOTH,
    DatumProtos.Datum.DataCase.CALL_LOG to DatumProtos.DatumType.CALL_LOG,
    DatumProtos.Datum.DataCase.DEVICE_EVENT to DatumProtos.DatumType.DEVICE_EVENT,
    DatumProtos.Datum.DataCase.EMBEDDED_SENSOR to DatumProtos.DatumType.EMBEDDED_SENSOR,
    DatumProtos.Datum.DataCase.EXTERNAL_SENSOR to DatumProtos.DatumType.EXTERNAL_SENSOR,
    DatumProtos.Datum.DataCase.INSTALLED_APP to DatumProtos.DatumType.INSTALLED_APP,
    DatumProtos.Datum.DataCase.KEY_LOG to DatumProtos.DatumType.KEY_LOG,
    DatumProtos.Datum.DataCase.LOCATION to DatumProtos.DatumType.LOCATION,
    DatumProtos.Datum.DataCase.MEDIA to DatumProtos.DatumType.MEDIA,
    DatumProtos.Datum.DataCase.MESSAGE to DatumProtos.DatumType.MESSAGE,
    DatumProtos.Datum.DataCase.NOTIFICATION to DatumProtos.DatumType.NOTIFICATION,
    DatumProtos.Datum.DataCase.FITNESS to DatumProtos.DatumType.FITNESS,
    DatumProtos.Datum.DataCase.SURVEY to DatumProtos.DatumType.SURVEY,
    DatumProtos.Datum.DataCase.DATA_TRAFFIC to DatumProtos.DatumType.DATA_TRAFFIC,
    DatumProtos.Datum.DataCase.WIFI to DatumProtos.DatumType.WIFI,
)

private val DATA_TYPE_TO_DATA_CASE = DATA_CASE_TO_DATA_TYPE.entries.associate { (k, v) -> v to k }

fun dataCaseToDataType(dataCase: DatumProtos.Datum.DataCase): DatumProtos.DatumType =
    DATA_CASE_TO_DATA_TYPE.getOrDefault(dataCase, DatumProtos.DatumType.UNRECOGNIZED)

fun dataTypeToDataCase(dataType: DatumProtos.DatumType): DatumProtos.Datum.DataCase =
    DATA_TYPE_TO_DATA_CASE.getOrDefault(dataType, DatumProtos.Datum.DataCase.DATA_NOT_SET)

inline fun <reified E : Enum<E>> safeEnumValuesOf(str: String?, default: E) = try {
    enumValueOf(str ?: "")
} catch (e: Exception) {
    default
}
