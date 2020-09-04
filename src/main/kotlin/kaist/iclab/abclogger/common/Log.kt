package kaist.iclab.abclogger

import org.apache.log4j.*


object Log {
    private lateinit var logger: Logger

    fun bind(basePath: String) {
        logger = Logger.getRootLogger()
        val layout = PatternLayout("%d{HH:mm:ss.SSS} [%t] [%-3p] [%l] %m%n")
        val fileAppender = DailyRollingFileAppender(layout, basePath, "'.'yyyy-MM-dd")
        val consoleAppender = ConsoleAppender(layout)
        logger.level = Level.INFO
        logger.addAppender(fileAppender)
        logger.addAppender(consoleAppender)
    }

    fun error(msg: String, throwable: Throwable? = null) {
        if (throwable != null) logger.error(msg, throwable) else logger.error(msg)
    }

    fun info(msg: String, throwable: Throwable? = null) {
        if (throwable != null) logger.info(msg, throwable) else logger.info(msg)
    }

    fun warn(msg: String, throwable: Throwable? = null) {
        if (throwable != null) logger.warn(msg, throwable) else logger.warn(msg)
    }
}