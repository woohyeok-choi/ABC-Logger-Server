package kaist.iclab.abclogger.db

import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.common.aggregate
import kaist.iclab.abclogger.schema.*
import org.bson.conversions.Bson
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineAggregatePublisher
import org.litote.kmongo.coroutine.CoroutineFindPublisher

class DatabaseReader(private val database: Database) {
    private fun buildDataFilter(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String,
            email: String,
            deviceInfo: String,
            deviceId: String
    ): Bson {
        val timeRangeFilter = if (fromTimestamp != toTimestamp) {
            and(Datum::timestamp gte fromTimestamp, Datum::timestamp lt toTimestamp)
        } else {
            and(Datum::timestamp gte 0, Datum::timestamp lt Long.MAX_VALUE)
        }

        val dataTypeFilter = if (dataType.isNotBlank()) {
            Datum::dataType eq dataType
        } else {
            null
        }

        val emailFilter = if (email.isNotBlank()) {
            Datum::email eq email
        } else {
            null
        }

        val deviceInfoFilter = if (deviceInfo.isNotBlank()) {
            Datum::deviceInfo eq deviceInfo
        } else {
            null
        }

        val deviceIdFilter = if (deviceId.isNotBlank()) {
            Datum::deviceId eq deviceId
        } else {
            null
        }

        return and(
                listOfNotNull(
                        timeRangeFilter,
                        dataTypeFilter,
                        emailFilter,
                        deviceInfoFilter,
                        deviceIdFilter
                )
        )
    }

    fun readData(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String,
            email: String,
            deviceInfo: String,
            deviceId: String,
            limit: Int,
            isAscending: Boolean
    ): CoroutineFindPublisher<Datum> = try {
        val filter = buildDataFilter(
                fromTimestamp, toTimestamp, dataType, email, deviceInfo, deviceId
        )
        val sort = if (isAscending) {
            ascending(Datum::timestamp)
        } else {
            descending(Datum::timestamp)
        }

        database.collection<Datum>().find(filter).limit(limit).sort(sort)
    } catch (e: Exception) {
        val params = mapOf(
                "fromTimestamp" to fromTimestamp,
                "toTimestamp" to toTimestamp,
                "dataType" to dataType,
                "email" to email,
                "deviceInfo" to deviceInfo,
                "deviceId" to deviceId,
                "limit" to limit,
                "isAscending" to isAscending
        )
        Log.error("DatabaseReader.readData(): $params", e)
        throw e
    }

    suspend fun countData(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String,
            email: String,
            deviceInfo: String,
            deviceId: String
    ): Long = try {
        val filter = buildDataFilter(
                fromTimestamp, toTimestamp, dataType, email, deviceInfo, deviceId
        )
        database.collection<Datum>().countDocuments(filter)
    } catch (e: Exception) {
        val params = mapOf(
                "fromTimestamp" to fromTimestamp,
                "toTimestamp" to toTimestamp,
                "dataType" to dataType,
                "email" to email,
                "deviceInfo" to deviceInfo,
                "deviceId" to deviceId
        )
        Log.error("DatabaseReader.countData(): $params", e)
        throw e
    }

    private fun buildHeartBeatsFilter(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String,
            email: String,
            deviceInfo: String,
            deviceId: String
    ): Bson {
        val timeRangeFilter = if (fromTimestamp != toTimestamp) {
            and(HeartBeat::timestamp gte fromTimestamp, HeartBeat::timestamp lt toTimestamp)
        } else {
            and(HeartBeat::timestamp gte 0, HeartBeat::timestamp lt Long.MAX_VALUE)
        }

        val dataTypeFilter = if (dataType.isNotBlank()) {
            HeartBeat::status elemMatch (Status::dataType eq dataType)
        } else {
            null
        }

        val emailFilter = if (email.isNotBlank()) {
            HeartBeat::email eq email
        } else {
            null
        }

        val deviceInfoFilter = if (deviceInfo.isNotBlank()) {
            HeartBeat::deviceInfo eq deviceInfo
        } else {
            null
        }

        val deviceIdFilter = if (deviceId.isNotBlank()) {
            HeartBeat::deviceId eq deviceId
        } else {
            null
        }

        return and(
                listOfNotNull(
                        timeRangeFilter,
                        dataTypeFilter,
                        emailFilter,
                        deviceInfoFilter,
                        deviceIdFilter
                )
        )
    }

    fun readHeartBeats(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String,
            email: String,
            deviceInfo: String,
            deviceId: String,
            limit: Int,
            isAscending: Boolean
    ): CoroutineFindPublisher<HeartBeat> = try {
        val filter = buildHeartBeatsFilter(
                fromTimestamp, toTimestamp, dataType, email, deviceInfo, deviceId
        )

        val sort = if (isAscending) {
            ascending(HeartBeat::timestamp)
        } else {
            descending(HeartBeat::timestamp)
        }

        val publisher = database.collection<HeartBeat>().find(filter).limit(limit).sort(sort)

        val dataTypeFilter = if (dataType.isNotBlank()) {
            HeartBeat::status elemMatch (Status::dataType eq dataType)
        } else {
            null
        }

        if (dataTypeFilter != null) {
            publisher.projection(dataTypeFilter)
        } else {
            publisher
        }
    } catch (e : Exception) {
        val params = mapOf(
                "fromTimestamp" to fromTimestamp,
                "toTimestamp" to toTimestamp,
                "dataType" to dataType,
                "email" to email,
                "deviceInfo" to deviceInfo,
                "deviceId" to deviceId,
                "limit" to limit,
                "isAscending" to isAscending
        )
        Log.error("DatabaseReader.readHeartBeats() - $params", e)
        throw e
    }

    suspend fun countHeartBeats(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String,
            email: String,
            deviceInfo: String,
            deviceId: String
    ): Long = try {
        val filter = buildHeartBeatsFilter(
                fromTimestamp, toTimestamp, dataType, email, deviceInfo, deviceId
        )

        database.collection<HeartBeat>().countDocuments(filter)
    } catch (e : Exception) {
        val params = mapOf(
                "fromTimestamp" to fromTimestamp,
                "toTimestamp" to toTimestamp,
                "dataType" to dataType,
                "email" to email,
                "deviceInfo" to deviceInfo,
                "deviceId" to deviceId
        )
        Log.error("DatabaseReader.countHeartBeats() - $params", e)
        throw e
    }

    private fun buildSubjectsFilter(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String
    ): Bson {
        val timeRangeFilter = if (fromTimestamp != toTimestamp) {
            and(Datum::timestamp gte fromTimestamp, Datum::timestamp lt toTimestamp)
        } else {
            and(Datum::timestamp gte 0, Datum::timestamp lt Long.MAX_VALUE)
        }
        val dataTypeFilter = if (dataType.isNotBlank()) {
            Datum::dataType eq dataType
        } else {
            null
        }

        return and(
                listOfNotNull(
                        timeRangeFilter,
                        dataTypeFilter
                )
        )
    }


    fun readSubjects(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String,
            limit: Int,
            isAscending: Boolean
    ): CoroutineAggregatePublisher<Subject> = try {
        val filter = buildSubjectsFilter(fromTimestamp, toTimestamp, dataType)

        val sort = if (isAscending) {
            ascending(Datum::timestamp)
        } else {
            descending(Datum::timestamp)
        }

        database.collection<Datum>().aggregate(
                match(filter),
                group(
                        document(
                                Datum::email from Datum::email,
                                Datum::deviceInfo from Datum::deviceInfo,
                                Datum::deviceId from Datum::deviceId
                        ),
                        Subject::email first Datum::email,
                        Subject::deviceInfo first Datum::deviceInfo,
                        Subject::deviceId first Datum::deviceId
                ),
                limit(limit),
                sort(sort)
        )

    } catch (e: Exception) {
        val params = mapOf(
                "fromTimestamp" to fromTimestamp,
                "toTimestamp" to toTimestamp,
                "dataType" to dataType,
                "limit" to limit,
                "isAscending" to isAscending
        )
        Log.error("DatabaseReader.readSubjects() - $params", e)
        throw e
    }

    suspend fun countSubjects(
            fromTimestamp: Long,
            toTimestamp: Long,
            dataType: String
    ): Long = try {
        val filter = buildSubjectsFilter(fromTimestamp, toTimestamp, dataType)
        database.collection<Datum>().aggregate<Subject>(
                match(filter),
                group(
                        document(
                                Datum::email from Datum::email,
                                Datum::deviceInfo from Datum::deviceInfo,
                                Datum::deviceId from Datum::deviceId
                        )
                )
        ).toList().size.toLong()
    } catch (e: Exception) {
        val params = mapOf(
                "fromTimestamp" to fromTimestamp,
                "toTimestamp" to toTimestamp,
                "dataType" to dataType
        )
        Log.error("DatabaseReader.readSubjects() - $params", e)
        throw e
    }

}