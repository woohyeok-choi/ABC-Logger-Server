package kaist.iclab.abclogger

import kaist.iclab.abclogger.schema.Datum

const val KEY_PORT_NUMBER = "PORT_NUMBER"
const val KEY_MONGO_SERVER_NAME = "MONGO_SERVER_NAME"
const val KEY_MONGO_PORT_NUMBER = "MONGO_PORT_NUMBER"
const val KEY_MONGO_DB_NAME = "MONGO_DB_NAME"
const val KEY_MONGO_ROOT_USER = "MONGO_ROOT_USER"
const val KEY_MONGO_ROOT_PASSWORD = "MONGO_ROOT_PASSWORD"
const val KEY_MONGO_WRITE_USER = "MONGO_WRITE_USER"
const val KEY_MONGO_WRITE_PASSWORD = "MONGO_WRITE_PASSWORD"
const val KEY_ADMIN_EMAIL = "ADMIN_EMAIL"
const val KEY_ADMIN_PASSWORD = "ADMIN_PASSWORD"
const val KEY_ERROR_RECIPIENTS = "ERROR_RECIPIENTS"
const val KEY_LOG_PATH = "LOG_PATH"

fun main() {
    val portNumber = System.getenv(KEY_PORT_NUMBER)?.toIntOrNull() ?: 50051

    val dbServerName = System.getenv(KEY_MONGO_SERVER_NAME) ?: "localhost"
    val dbPortNumber = System.getenv(KEY_MONGO_PORT_NUMBER)?.toIntOrNull() ?: 27017
    val dbName = System.getenv(KEY_MONGO_DB_NAME) ?: "data"

    val dbUserName = System.getenv(KEY_MONGO_ROOT_USER) ?: "mongo"
    val dbPassword = System.getenv(KEY_MONGO_ROOT_PASSWORD) ?: "mongo"

    val dbWriteUserName = System.getenv(KEY_MONGO_WRITE_USER) ?: dbUserName
    val dbWritePassword = System.getenv(KEY_MONGO_WRITE_PASSWORD) ?: dbPassword

    val adminEmail = System.getenv(KEY_ADMIN_EMAIL) ?: ""
    val adminPassword = System.getenv(KEY_ADMIN_PASSWORD) ?: ""
    val errorRecipients = System.getenv(KEY_ERROR_RECIPIENTS)?.split(";") ?: emptyList()

    val logPath = System.getenv(KEY_LOG_PATH) ?: "/home/abclogger/logs"

    val app = App()

    app.start(
            portNumber = portNumber,
            dbServerName = dbServerName,
            dbPortNumber = dbPortNumber,
            dbName = dbName,
            dbRootUserName = dbUserName,
            dbRootPassword = dbPassword,
            dbWriterUserName = dbWriteUserName,
            dbWriterUserPassword = dbWritePassword,
            adminEmail = adminEmail,
            adminPassword = adminPassword,
            recipients = errorRecipients,
            logPath = logPath
    )
    app.await()
}