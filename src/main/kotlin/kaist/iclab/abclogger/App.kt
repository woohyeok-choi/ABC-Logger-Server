package kaist.iclab.abclogger

import io.grpc.Server
import io.grpc.ServerBuilder
import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.db.Database
import kaist.iclab.abclogger.db.DatabaseAggregator
import kaist.iclab.abclogger.db.DatabaseReader
import kaist.iclab.abclogger.db.DatabaseWriter
import kaist.iclab.abclogger.schema.*
import kaist.iclab.abclogger.service.DataOperations
import kaist.iclab.abclogger.interceptor.AuthInterceptor
import kaist.iclab.abclogger.interceptor.ErrorInterceptor
import kaist.iclab.abclogger.service.AggregateOperations
import kaist.iclab.abclogger.service.HeartBeatsOperations
import kaist.iclab.abclogger.service.SubjectsOperations
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.serialization.registerModule
import java.util.concurrent.Executors

class App {
    private var server: Server? = null

    fun start(
        portNumber: Int,
        dbServerName: String,
        dbPortNumber: Int,
        dbName: String,
        dbRootUserName: String,
        dbRootPassword: String,
        dbWriterUserName: String,
        dbWriterUserPassword: String,
        dbReadUsers: Map<String, String>,
        adminEmail: String,
        adminPassword: String,
        authTokens: List<String>,
        recipients: List<String>,
        logPath: String
    ) {
        if (logPath.isNotBlank()) Log.enableFileAppender(logPath)
        if (adminEmail.isNotBlank() && adminPassword.isNotBlank() && recipients.isNotEmpty()) {
            Log.enableGMailAppender(
                email = adminEmail,
                password = adminPassword,
                recipients = recipients
            )
        }

        try {
            registerModule(Config.serializersModule)

            val database = Database(
                serverName = dbServerName,
                portNumber = dbPortNumber,
                dbName = dbName,
                readUsers = dbReadUsers,
                rootUserName = dbRootUserName,
                rootPassword = dbRootPassword,
                writerUserName = dbWriterUserName,
                writerUserPassword = dbWriterUserPassword
            )

            runBlocking {
                database.bind()

                Config.datumIndices.forEach { index ->
                    database.createIndex<Datum>(index)
                }

                Config.heartBeatsIndices.forEach { index ->
                    database.createIndex<HeartBeat>(index)
                }
            }

            val writer = DatabaseWriter(database)
            val reader = DatabaseReader(database, 50)
            val aggregator = DatabaseAggregator(database, 50)


            val bulkSize = 5000
            val dispatcher = Executors.newFixedThreadPool(32).asCoroutineDispatcher()

            val dataOperations = DataOperations(
                reader = reader,
                writer = writer,
                bulkSize = bulkSize,
                context = dispatcher
            )
            val heartBeatsOperations = HeartBeatsOperations(
                reader = reader,
                writer = writer,
                bulkSize = bulkSize,
                context = dispatcher
            )
            val subjectOperations = SubjectsOperations(
                reader = reader,
                bulkSize = bulkSize,
                context = dispatcher
            )
            val aggregateOperations = AggregateOperations(
                aggregator = aggregator,
                context = dispatcher
            )

            server = ServerBuilder.forPort(portNumber)
                .directExecutor()
                .intercept(ErrorInterceptor())
                .intercept(AuthInterceptor(authTokens.toSet()))
                .addService(dataOperations)
                .addService(heartBeatsOperations)
                .addService(subjectOperations)
                .addService(aggregateOperations)
                .build()


            server?.start()

            Runtime.getRuntime().addShutdownHook(
                Thread {
                    Log.info("JVM is shutting down, so a server will be also shutting down.")
                    server?.shutdown()
                    Log.info("A server shuts down.")
                }
            )
            Log.info("A server is started..")
        } catch (e: Exception) {
            Log.error("A server cannot be started.", e)
        }
    }

    fun await() {
        server?.awaitTermination()
    }

    fun stop() {
        server?.shutdownNow()?.awaitTermination()
    }
}
