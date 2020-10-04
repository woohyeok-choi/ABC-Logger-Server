package kaist.iclab.abclogger.db

import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.schema.*

import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineAggregatePublisher
import org.litote.kmongo.coroutine.CoroutineFindPublisher
import org.litote.kmongo.coroutine.aggregate

class DatabaseReader(private val database: Database, private val batchSize: Int) {
    fun readData(
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
        limit: Int,
        isAscending: Boolean
    ): CoroutineFindPublisher<Datum> = try {
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

        val sort = if (isAscending) {
            ascending(Datum::timestamp)
        } else {
            descending(Datum::timestamp)
        }

        database.collection<Datum>()
            .find(filter)
            .batchSize(batchSize)
            .limit(limit)
            .sort(sort)
    } catch (e: Exception) {
        Log.error("DatabaseReader.readData()", e)
        throw e
    }

    fun readHeartBeats(
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
        limit: Int,
        isAscending: Boolean
    ): CoroutineFindPublisher<HeartBeat> = try {
        val filter = heartBeatFilter(
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

        val sort = if (isAscending) {
            ascending(HeartBeat::timestamp)
        } else {
            descending(HeartBeat::timestamp)
        }
        database.collection<HeartBeat>()
            .find(filter)
            .batchSize(batchSize)
            .limit(limit)
            .sort(sort)
    } catch (e: Exception) {
        Log.error("DatabaseReader.readHeartBeats() ", e)
        throw e
    }

    fun readSubjects(
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
        limit: Int,
        isAscending: Boolean
    ): CoroutineAggregatePublisher<Subject> = try {
        val filter = heartBeatFilter(
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

        val sort = if (isAscending) {
            ascending(HeartBeat::timestamp)
        } else {
            descending(HeartBeat::timestamp)
        }

        database.collection<HeartBeat>().aggregate<Subject>(
            match(filter),
            group(
                id = HeartBeat::subject,
                fieldAccumulators = arrayOf(
                    Subject::groupName first HeartBeat::subject / Subject::groupName,
                    Subject::email first HeartBeat::subject / Subject::email,
                    Subject::instanceId first HeartBeat::subject / Subject::instanceId,
                    Subject::source first HeartBeat::subject / Subject::source,
                    Subject::deviceManufacturer first HeartBeat::subject / Subject::deviceManufacturer,
                    Subject::deviceModel first HeartBeat::subject / Subject::deviceModel,
                    Subject::deviceVersion first HeartBeat::subject / Subject::deviceVersion,
                    Subject::deviceOs first HeartBeat::subject / Subject::deviceOs,
                    Subject::appId first HeartBeat::subject / Subject::appId,
                    Subject::appVersion first HeartBeat::subject / Subject::appVersion
                )
            ),
            limit(limit),
            sort(sort)
        ).allowDiskUse(true).batchSize(batchSize)
    } catch (e: Exception) {
        Log.error("DatabaseReader.readSubjects()", e)
        throw e
    }
}