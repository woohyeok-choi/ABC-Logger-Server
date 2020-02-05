package kaist.iclab.abclogger

import io.grpc.Server
import io.grpc.ServerBuilder
import java.util.concurrent.TimeUnit

class App(private val portNumber: Int,
          logPath: String,
          serverName: String,
          dbPortNumber: Int,
          dbName: String) {

    private val allTables = arrayOf(
            PhysicalActivityTransitions,
            PhysicalActivities,
            AppUsageEvents,
            Batteries,
            Bluetoothes,
            CallLogs,
            DeviceEvents,
            ExternalSensors,
            InstalledApps,
            InternalSensors,
            KeyLogs,
            Locations,
            Medias,
            Messages,
            Notifications,
            PhysicalStats,
            Surveys,
            DataTraffics,
            Wifis
    )

    private val db: DB
    private val dbReader: DBReader
    private val dbWriter: DBWriter
    private val dataOperation: DataOperationService

    private val server: Server

    init {
        Log.bind(logPath)

        var isBounded = false

        db = DB(allTables, serverName, dbPortNumber, dbName)

        for (i in 0 until 10) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10))
            try {
                db.bind()
                isBounded = true
            } catch (e: Exception) { }
        }

        if (!isBounded) throw RuntimeException(
                "No found DB connections for server: $serverName; port: $dbPortNumber; dbName: $dbName"
        )

        dbReader = DBReader(db.readOnlyDb)
        dbWriter = DBWriter(db.writeOnlyDb, allTables)
        dataOperation = DataOperationService(dbReader, dbWriter)

        server = ServerBuilder.forPort(portNumber)
                .addService(dataOperation)
                .build()
    }

    fun start() {
        Log.info("Server started with port: $portNumber")

        dbWriter.subscribe()
        server.start().awaitTermination()
    }

    fun stop() {
        Log.info("Server stopped.")

        dbWriter.unsubscribe()
        server.shutdown().awaitTermination()
    }
}
