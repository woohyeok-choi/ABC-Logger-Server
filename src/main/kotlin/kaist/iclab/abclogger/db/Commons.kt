package kaist.iclab.abclogger.db

import com.mongodb.client.model.UnwindOptions
import kaist.iclab.abclogger.schema.DataStatus
import kaist.iclab.abclogger.schema.Datum
import kaist.iclab.abclogger.schema.HeartBeat
import kaist.iclab.abclogger.schema.Subject
import org.bson.conversions.Bson
import org.litote.kmongo.*
import kotlin.reflect.KProperty

infix fun KProperty<Long?>.between(range: LongRange) = if (range.first != range.last) {
    and(
        this gte range.first,
        this lte range.last.coerceAtLeast(range.first)
    )
} else {
    and(
        this gte 0,
        this lt Long.MAX_VALUE
    )
}

infix fun KProperty<String?>.nullableIn(values: Collection<String?>) = if (values.filterNot { it.isNullOrBlank() }.isEmpty()) null else this `in` values

fun dataFilter(
    fromTimestamp: Long,
    toTimestamp: Long,
    dataTypes: List<String> = listOf(),
    groupNames: List<String> = listOf(),
    emails: List<String> = listOf(),
    instanceIds: List<String> = listOf(),
    sources: List<String> = listOf(),
    deviceManufacturers: List<String> = listOf(),
    deviceModels: List<String> = listOf(),
    deviceVersion: List<String> = listOf(),
    deviceOses: List<String> = listOf(),
    appIds: List<String> = listOf(),
    appVersions: List<String> = listOf(),
    isMd5Encrypted: Boolean
): Bson {
    val filterTimeRange = Datum::timestamp between (fromTimestamp until toTimestamp)

    val filterDataType = Datum::datumType nullableIn dataTypes

    val filterSubjects = and(
        Datum::subject / Subject::groupName nullableIn groupNames,
        if (isMd5Encrypted) {
            Datum::subject / Subject::hashedEmail nullableIn emails
        } else {
            Datum::subject / Subject::email nullableIn emails
        },
        Datum::subject / Subject::instanceId nullableIn instanceIds,
        Datum::subject / Subject::source nullableIn sources,
        Datum::subject / Subject::deviceManufacturer nullableIn deviceManufacturers,
        Datum::subject / Subject::deviceModel nullableIn deviceModels,
        Datum::subject / Subject::deviceVersion nullableIn deviceVersion,
        Datum::subject / Subject::deviceOs nullableIn deviceOses,
        Datum::subject / Subject::appId nullableIn appIds,
        Datum::subject / Subject::appVersion nullableIn appVersions,
    )

    return and(filterTimeRange, filterDataType, filterSubjects)
}

fun heartBeatFilter(
    fromTimestamp: Long,
    toTimestamp: Long,
    dataTypes: List<String> = listOf(),
    groupNames: List<String> = listOf(),
    emails: List<String> = listOf(),
    instanceIds: List<String> = listOf(),
    sources: List<String> = listOf(),
    deviceManufacturers: List<String> = listOf(),
    deviceModels: List<String> = listOf(),
    deviceVersion: List<String> = listOf(),
    deviceOses: List<String> = listOf(),
    appIds: List<String> = listOf(),
    appVersions: List<String> = listOf(),
    isMd5Encrypted: Boolean
): Bson {
    val filterTimeRange = HeartBeat::timestamp between (fromTimestamp until toTimestamp)

    val filterDataType = HeartBeat::dataStatus / DataStatus::datumType nullableIn  dataTypes

    val filterSubjects = and(
        HeartBeat::subject / Subject::groupName nullableIn groupNames,
        if (isMd5Encrypted) {
            HeartBeat::subject / Subject::hashedEmail nullableIn emails
        } else {
            HeartBeat::subject / Subject::email nullableIn emails
        },
        HeartBeat::subject / Subject::instanceId nullableIn instanceIds,
        HeartBeat::subject / Subject::source nullableIn sources,
        HeartBeat::subject / Subject::deviceManufacturer nullableIn deviceManufacturers,
        HeartBeat::subject / Subject::deviceModel nullableIn deviceModels,
        HeartBeat::subject / Subject::deviceVersion nullableIn deviceVersion,
        HeartBeat::subject / Subject::deviceOs nullableIn deviceOses,
        HeartBeat::subject / Subject::appId nullableIn appIds,
        HeartBeat::subject / Subject::appVersion nullableIn appVersions
    )

    return and(filterTimeRange, filterDataType, filterSubjects)
}

infix fun <T> KProperty<T>.unwind(unwindOptions: UnwindOptions?): Bson =
    unwind(projection, unwindOptions ?: UnwindOptions())
