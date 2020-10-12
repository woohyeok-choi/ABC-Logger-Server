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
        this lte Long.MAX_VALUE
    )
}

infix fun KProperty<String?>.inOrNull(values: Collection<String?>) =
    if (values.filterNot { it.isNullOrBlank() }.isEmpty()) {
        null
    } else {
        this `in` values
    }

infix fun KProperty<String?>.inOrExists(values: Collection<String?>) =
    if (values.filterNot { it.isNullOrBlank() }.isEmpty()) {
        this exists true
    } else {
        this `in` values
    }

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
    appVersions: List<String> = listOf()
): Bson {
    val filterDataType = Datum::datumType inOrExists dataTypes
    val filterSubject = Datum::subject exists true
    val filterTimeRange = Datum::timestamp between (fromTimestamp..toTimestamp)
    val filterEmails = or(
        Datum::subject / Subject::email inOrNull emails,
        Datum::subject / Subject::hashedEmail inOrNull emails,
    )
    val filterCommons = and(
        Datum::subject / Subject::groupName inOrNull groupNames,
        Datum::subject / Subject::instanceId inOrNull instanceIds,
        Datum::subject / Subject::source inOrNull sources,
        Datum::subject / Subject::deviceManufacturer inOrNull deviceManufacturers,
        Datum::subject / Subject::deviceModel inOrNull deviceModels,
        Datum::subject / Subject::deviceVersion inOrNull deviceVersion,
        Datum::subject / Subject::deviceOs inOrNull deviceOses,
        Datum::subject / Subject::appId inOrNull appIds,
        Datum::subject / Subject::appVersion inOrNull appVersions,
    )

    return and(filterTimeRange, filterDataType, filterSubject, filterEmails, filterCommons)
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
    appVersions: List<String> = listOf()
): Bson {
    val filterDataType = HeartBeat::dataStatus / DataStatus::datumType inOrExists dataTypes
    val filterSubject = HeartBeat::subject exists true
    val filterTimeRange = HeartBeat::timestamp between (fromTimestamp..toTimestamp)
    val filterEmails = or(
        HeartBeat::subject / Subject::email inOrNull emails,
        HeartBeat::subject / Subject::hashedEmail inOrNull emails,
    )
    val filterCommons = and(
        HeartBeat::subject / Subject::groupName inOrNull groupNames,
        HeartBeat::subject / Subject::instanceId inOrNull instanceIds,
        HeartBeat::subject / Subject::source inOrNull sources,
        HeartBeat::subject / Subject::deviceManufacturer inOrNull deviceManufacturers,
        HeartBeat::subject / Subject::deviceModel inOrNull deviceModels,
        HeartBeat::subject / Subject::deviceVersion inOrNull deviceVersion,
        HeartBeat::subject / Subject::deviceOs inOrNull deviceOses,
        HeartBeat::subject / Subject::appId inOrNull appIds,
        HeartBeat::subject / Subject::appVersion inOrNull appVersions
    )

    return and(filterDataType, filterSubject, filterTimeRange, filterEmails, filterCommons)
}

infix fun <T> KProperty<T>.unwind(unwindOptions: UnwindOptions?): Bson =
    unwind(projection, unwindOptions ?: UnwindOptions())
