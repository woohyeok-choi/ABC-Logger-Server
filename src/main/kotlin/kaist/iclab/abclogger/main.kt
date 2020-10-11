package kaist.iclab.abclogger

import kotlinx.coroutines.ExperimentalCoroutinesApi

const val KEY_MONGO_SERVER_NAME = "MONGO_SERVER_NAME"
const val KEY_MONGO_DB_NAME = "MONGO_DB_NAME"
const val KEY_MONGO_ROOT_USER = "MONGO_ROOT_USER"
const val KEY_MONGO_ROOT_PASSWORD = "MONGO_ROOT_PASSWORD"
const val KEY_MONGO_WRITE_USER = "MONGO_WRITE_USER"
const val KEY_MONGO_WRITE_PASSWORD = "MONGO_WRITE_PASSWORD"
const val KEY_MONGO_READ_USERS = "MONGO_READ_USERS"
const val KEY_MONGO_READ_PASSWORDS = "MONGO_READ_PASSWORDS"
const val KEY_ADMIN_EMAIL = "ADMIN_EMAIL"
const val KEY_ADMIN_PASSWORD = "ADMIN_PASSWORD"
const val KEY_ROOT_AUTH_TOKENS = "ROOT_AUTH_TOKENS"
const val KEY_READ_ONLY_AUTH_TOKENS = "READ_ONLY_AUTH_TOKENS"
const val KEY_ERROR_RECIPIENTS = "ERROR_RECIPIENTS"
const val KEY_LOG_PATH = "LOG_PATH"
const val KEY_LEGACY_DATA_PATH = "LEGACY_DATA_PATH"


@ExperimentalCoroutinesApi
fun main() {
    val dbServerName = System.getenv(KEY_MONGO_SERVER_NAME) ?: "localhost"
    val dbName = System.getenv(KEY_MONGO_DB_NAME) ?: "data"

    val dbUserName = System.getenv(KEY_MONGO_ROOT_USER) ?: "mongo"
    val dbPassword = System.getenv(KEY_MONGO_ROOT_PASSWORD) ?: "mongo"

    val dbWriteUserName = System.getenv(KEY_MONGO_WRITE_USER) ?: dbUserName
    val dbWritePassword = System.getenv(KEY_MONGO_WRITE_PASSWORD) ?: dbPassword

    val dbReadUserNames = System.getenv(KEY_MONGO_READ_USERS)?.split(";") ?: emptyList()
    val dbReadUserPasswords = System.getenv(KEY_MONGO_READ_PASSWORDS)?.split("l") ?: emptyList()

    val dbReadUsers: Map<String, String> =
        when {
            dbReadUserNames.isEmpty() || dbReadUserPasswords.isEmpty() ->
                mapOf()
            dbReadUserNames.size != dbReadUserPasswords.size ->
                dbReadUserNames.associateWith { dbReadUserPasswords.firstOrNull() ?: dbPassword }
            else -> dbReadUserNames.zip(dbReadUserPasswords).toMap()
        }

    val adminEmail = System.getenv(KEY_ADMIN_EMAIL) ?: ""
    val adminPassword = System.getenv(KEY_ADMIN_PASSWORD) ?: ""
    val rootTokens = System.getenv(KEY_ROOT_AUTH_TOKENS)?.split(";") ?: emptyList()
    val readOnlyTokens = System.getenv(KEY_READ_ONLY_AUTH_TOKENS)?.split(";") ?: emptyList()

    val errorRecipients = System.getenv(KEY_ERROR_RECIPIENTS)?.split(";") ?: emptyList()

    val logPath = System.getenv(KEY_LOG_PATH) ?: "/home/abclogger/logs"
    val app = App()

    app.start(
        portNumber = 50051,
        dbServerName = dbServerName,
        dbName = dbName,
        dbRootUserName = dbUserName,
        dbRootPassword = dbPassword,
        dbWriterUserName = dbWriteUserName,
        dbWriterUserPassword = dbWritePassword,
        dbReadUsers = dbReadUsers,
        adminEmail = adminEmail,
        adminPassword = adminPassword,
        recipients = errorRecipients,
        logPath = logPath,
        rootTokens = rootTokens,
        readOnlyTokens = readOnlyTokens
    )
    app.await()
}