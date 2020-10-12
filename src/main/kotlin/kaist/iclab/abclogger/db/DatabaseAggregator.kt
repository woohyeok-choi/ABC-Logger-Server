package kaist.iclab.abclogger.db

import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.schema.*
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineAggregatePublisher
import org.litote.kmongo.coroutine.aggregate

class DatabaseAggregator(private val database: Database, private val batchSize: Int) {
    fun countData(
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
    ): CoroutineAggregatePublisher<Group> = try {
        val filter = dataFilter(
            fromTimestamp,
            toTimestamp,
            dataTypes,
            groupNames,
            emails,
            instanceIds,
            sources,
            deviceManufacturers,
            deviceModels,
            deviceVersion,
            deviceOses,
            appIds,
            appVersions
        )

        database.collection<Datum>().aggregate<Group>(
            match(filter),
            group(
                id = document(
                    Group::subject from Datum::subject,
                    Group::datumType from Datum::datumType
                ),
                fieldAccumulators = arrayOf(
                    Group::subject first Datum::subject,
                    Group::datumType first Datum::datumType,
                    Group::value sum 1.0,
                    Group::firstTimestamp first Datum::timestamp,
                    Group::lastTimestamp last Datum::timestamp
                )
            )
        ).allowDiskUse(true).batchSize(batchSize)
    } catch (e: Exception) {
        Log.error("DatabaseReader.countData()", e)
        throw e
    }

    fun countSubjects(
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
    ): CoroutineAggregatePublisher<Group> = try {
        val filter = dataFilter(
            fromTimestamp,
            toTimestamp,
            dataTypes,
            groupNames,
            emails,
            instanceIds,
            sources,
            deviceManufacturers,
            deviceModels,
            deviceVersion,
            deviceOses,
            appIds,
            appVersions
        )

        database.collection<Datum>().aggregate<Group>(
            match(filter),
            group(
                id = document(
                    Group::subject from Datum::subject,
                    Group::datumType from Datum::datumType
                ),
                fieldAccumulators = arrayOf(
                    Group::subject first Group::subject,
                    Group::datumType first Group::datumType,
                    Group::firstTimestamp first Datum::timestamp,
                    Group::lastTimestamp last Datum::timestamp
                )
            ),
            group(
                id = Group::datumType,
                fieldAccumulators = arrayOf(
                    Group::datumType first Group::datumType,
                    Group::value sum 1.0,
                    Group::firstTimestamp first Group::firstTimestamp,
                    Group::lastTimestamp last Group::lastTimestamp
                )
            )
        ).allowDiskUse(true).batchSize(batchSize)
    } catch (e: Exception) {
        Log.error("DatabaseReader.countSubjects()", e)
        throw e
    }
}