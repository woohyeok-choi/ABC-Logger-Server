package kaist.iclab.abclogger

const val KEY_PORT_NUMBER = "PORT_NUMBER"
const val KEY_LOG_PATH = "LOG_PATH"
const val KEY_POSTGRE_USER = "POSTGRE_USER"
const val KEY_POSTGRE_PASSWORD = "POSTGRE_PASSWORD"
const val KEY_POSTGRE_SERVER_NAME = "POSTGRE_SERVER_NAME"
const val KEY_POSTGRE_PORT_NUMBER = "POSTGRE_PORT_NUMBER"
const val KEY_POSTGRE_DB_NAME = "POSTGRE_DB_NAME"

fun main() {
    val portNumber = System.getenv(KEY_PORT_NUMBER)?.toIntOrNull() ?: 50051
    val logPath = System.getenv(KEY_LOG_PATH) ?: "./log/abc.log"
    val dbServerName = System.getenv(KEY_POSTGRE_SERVER_NAME) ?: "localhost"
    val dbPortNumber = System.getenv(KEY_POSTGRE_PORT_NUMBER)?.toIntOrNull() ?: 5432
    val dbName = System.getenv(KEY_POSTGRE_DB_NAME) ?: "postgres"
    val dbUserName = System.getenv(KEY_POSTGRE_USER) ?: "postgre"
    val dbPassword = System.getenv(KEY_POSTGRE_PASSWORD) ?: "postgre"

    val app = App(
            portNumber = portNumber,
            logPath = logPath,
            dbServerName = dbServerName,
            dbPortNumber = dbPortNumber,
            dbName = dbName,
            dbUserName = dbUserName,
            dbPassword = dbPassword
    )

    app.start()
}