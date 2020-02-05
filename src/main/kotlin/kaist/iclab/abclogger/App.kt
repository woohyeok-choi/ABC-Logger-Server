package kaist.iclab.abclogger

import io.grpc.Server
import io.grpc.ServerBuilder

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

    private val db = DB(allTables, serverName, dbPortNumber, dbName)
    private val dbReader = DBReader(db.readOnlyDb)
    private val dbWriter = DBWriter(db.writeOnlyDb, allTables)
    private val dataOperation = DataOperationService(dbReader, dbWriter)

    private val server: Server = ServerBuilder.forPort(portNumber)
            .addService(dataOperation)
            .build()

    init {
        Log.bind(logPath)
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
