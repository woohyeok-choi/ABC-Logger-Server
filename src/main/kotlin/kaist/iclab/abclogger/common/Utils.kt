package kaist.iclab.abclogger.common

import com.google.protobuf.GeneratedMessageV3
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineAggregatePublisher
import org.litote.kmongo.coroutine.CoroutineCollection
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun toOffsetDateTime(timeMillis: Long, offsetSec: Int? = null): OffsetDateTime {
    val zoneOffset = if (offsetSec != null) {
        ZoneOffset.ofTotalSeconds(offsetSec)
    } else {
        OffsetDateTime.now().offset
    }
    val localDateTime = LocalDateTime.ofEpochSecond(timeMillis / 1000, 0, zoneOffset)
    return OffsetDateTime.of(
            localDateTime, zoneOffset
    )
}

fun toMd5Hash(value: String): String =
    MessageDigest.getInstance("MD5").digest(value.toByteArray()).joinToString("") {
        it.toInt().and(0xFF).toString(16).padStart(2, '0')
    }

