package kaist.iclab.abclogger.common

import com.mongodb.event.CommandFailedEvent
import com.mongodb.event.CommandStartedEvent
import com.mongodb.event.CommandSucceededEvent
import org.apache.log4j.*
import org.apache.log4j.spi.LoggingEvent
import java.text.SimpleDateFormat
import java.util.*

object Log {
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss XXX")
    private val layout = PatternLayout("[%c] %d{HH:mm:ss.SSS} [%t] [%-3p] %m%n")

    private val generalLogger = Logger.getLogger("General").apply {
        level = Level.INFO
        addAppender(ConsoleAppender(layout))
    }

    private val errorLogger = Logger.getLogger("Error").apply {
        level = Level.ERROR
        addAppender(ConsoleAppender(layout))
    }

    private val mongoLogger = Logger.getLogger("Mongo").apply {
        level = Level.DEBUG
        addAppender(ConsoleAppender(layout))
    }

    fun enableFileAppender(basePath: String) {
        generalLogger.addAppender(DailyRollingFileAppender(layout, "$basePath.general", "'.'yyyy-MM-dd"))
        errorLogger.addAppender(DailyRollingFileAppender(layout, "$basePath.error", "'.'yyyy-MM-dd"))
        mongoLogger.addAppender(DailyRollingFileAppender(layout, "$basePath.mongo", "'.'yyyy-MM-dd"))
    }

    fun enableGMailAppender(email: String, password: String, recipients: List<String>) {
        val gmailSender = GMailSender(email = email, password = password, recipients = recipients)

        val gmailAppender = object : AppenderSkeleton() {
            override fun requiresLayout(): Boolean = false

            override fun append(event: LoggingEvent?) {
                val formattedTime = (event?.getTimeStamp() ?: System.currentTimeMillis()).let {
                    val calendar = GregorianCalendar.getInstance()
                    calendar.timeInMillis = it
                    formatter.format(calendar.time)
                }

                val isImportant = event?.getLevel() == Level.ERROR
                if (!isImportant) return

                val message = event?.renderedMessage
                val throwable = event?.throwableStrRep?.joinToString(System.lineSeparator())

                if (message?.isNotEmpty() == true || throwable?.isNotEmpty() == true) {
                    val subject = "[ABC Logger] Error report at $formattedTime"
                    val content = "${message ?: ""}${System.lineSeparator()}----------${throwable ?: ""}"
                    info("$subject - $content")
                    try {
                        gmailSender.send(subject, content)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun close() { }
        }

        errorLogger.addAppender(gmailAppender)
    }

    fun error(msg: String, throwable: Throwable? = null) {
        if (throwable != null) errorLogger.error(msg, throwable) else errorLogger.error(msg)
    }

    fun info(msg: String, throwable: Throwable? = null) {
        if (throwable != null) generalLogger.info(msg, throwable) else generalLogger.info(msg)
    }

    fun info(event: CommandStartedEvent?) {
        if (event == null) return
        val command = if ("insert" in event.commandName) {
            ""
        } else {
            event.command.toJson()
        }

        val base = "${event.requestId}/${event.commandName}:$command} " +
                "on ${event.connectionDescription.serverAddress}.${event.databaseName} - ${event.connectionDescription.connectionId}"
        mongoLogger.info(base)
    }

    fun info(event: CommandSucceededEvent?) {
        if (event == null) return
        val base = "${event.requestId}/${event.commandName} " +
                "on ${event.connectionDescription.serverAddress} - ${event.connectionDescription.connectionId}"

        mongoLogger.info(base)
    }

    fun error(event: CommandFailedEvent?) {
        if (event == null) return
        val base = "${event.requestId}/${event.commandName} " +
                "on ${event.connectionDescription.serverAddress} - ${event.connectionDescription.connectionId}"
        if (event.throwable != null) mongoLogger.error(base, event.throwable) else mongoLogger.error(base)
    }

}