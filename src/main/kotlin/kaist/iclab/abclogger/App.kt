package kaist.iclab.abclogger

import io.grpc.Server
import io.grpc.ServerBuilder
import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.db.Database
import kaist.iclab.abclogger.db.DatabaseReader
import kaist.iclab.abclogger.db.DatabaseWriter
import kaist.iclab.abclogger.schema.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.modules.SerializersModule
import org.litote.kmongo.div
import org.litote.kmongo.index
import org.litote.kmongo.serialization.registerModule

class App {
    private lateinit var server: Server

    fun start(
            portNumber: Int,
            dbServerName: String,
            dbPortNumber: Int,
            dbName: String,
            dbRootUserName: String,
            dbRootPassword: String,
            dbWriterUserName: String,
            dbWriterUserPassword: String,
            adminEmail: String,
            adminPassword: String,
            recipients: List<String>,
            logPath: String
    ) {
        try {
            Log.enableFileAppender(logPath)

            Log.enableGMailAppender(
                    email = adminEmail,
                    password = adminPassword,
                    recipients = recipients
            )

            val serialModule = SerializersModule {
                polymorphic<Value> {
                    PhysicalActivityTransition::class with PhysicalActivityTransition.serializer()
                    PhysicalActivity::class with PhysicalActivity.serializer()
                    AppUsageEvent::class with AppUsageEvent.serializer()
                    Battery::class with Battery.serializer()
                    Bluetooth::class with Bluetooth.serializer()
                    CallLog::class with CallLog.serializer()
                    DeviceEvent::class with DeviceEvent.serializer()
                    Sensor::class with Sensor.serializer()
                    InstalledApp::class with InstalledApp.serializer()
                    KeyLog::class with KeyLog.serializer()
                    Location::class with Location.serializer()
                    Media::class with Media.serializer()
                    Message::class with Message.serializer()
                    Notification::class with Notification.serializer()
                    PhysicalStat::class with PhysicalStat.serializer()
                    Survey::class with Survey.serializer()
                    DataTraffic::class with DataTraffic.serializer()
                    Wifi::class with Wifi.serializer()
                }
            }

            registerModule(serialModule)

            val database = Database(
                    serverName = dbServerName,
                    portNumber = dbPortNumber,
                    dbName = dbName,
                    rootUserName = dbRootUserName,
                    rootPassword = dbRootPassword,
                    writerUserName = dbWriterUserName,
                    writerUserPassword = dbWriterUserPassword
            )

            runBlocking {
                database.bind()
                /**
                 * Create indices for Datum - here, assume we always use a timestamp field:
                 * - Compound index
                 * 1. timestamp, dataType, email, deviceId
                 * 2. timestamp, dataType, email, deviceInfo, deviceId
                 * 3. timestamp, email, deviceId
                 * 4. timestamp, email, deviceInfo, deviceId
                 * 5. timestamp, deviceId
                 * 6. timestamp, deviceInfo, deviceId
                 */
                with(database.collection<Datum>()) {
                    ensureIndex(
                            index(
                                    Datum::timestamp to true,
                                    Datum::dataType to true,
                                    Datum::email to true,
                                    Datum::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    Datum::timestamp to true,
                                    Datum::dataType to true,
                                    Datum::email to true,
                                    Datum::deviceInfo to true,
                                    Datum::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    Datum::timestamp to true,
                                    Datum::email to true,
                                    Datum::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    Datum::timestamp to true,
                                    Datum::email to true,
                                    Datum::deviceInfo to true,
                                    Datum::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    Datum::timestamp to true,
                                    Datum::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    Datum::timestamp to true,
                                    Datum::deviceInfo to true,
                                    Datum::deviceId to true
                            )
                    )
                }

                /**
                 * Create indices for Heartbeats - here, assume we always use a toTimestamp field:
                 * - Compound index
                 * 1. timestamp, dataType, email, deviceId
                 * 2. timestamp, dataType, email, deviceInfo, deviceId
                 * 3. timestamp, email, deviceId
                 * 4. timestamp, email, deviceInfo, deviceId
                 * 5. timestamp, deviceId
                 * 6. timestamp, deviceInfo, deviceId
                 */
                with(database.collection<HeartBeat>()) {
                    ensureIndex(
                            index(
                                    HeartBeat::timestamp to true,
                                    HeartBeat::status / Status::dataType to true,
                                    HeartBeat::email to true,
                                    HeartBeat::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    HeartBeat::timestamp to true,
                                    HeartBeat::status / Status::dataType to true,
                                    HeartBeat::email to true,
                                    HeartBeat::deviceInfo to true,
                                    HeartBeat::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    HeartBeat::timestamp to true,
                                    HeartBeat::email to true,
                                    HeartBeat::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    HeartBeat::timestamp to true,
                                    HeartBeat::email to true,
                                    HeartBeat::deviceInfo to true,
                                    HeartBeat::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    HeartBeat::timestamp to true,
                                    HeartBeat::deviceId to true
                            )
                    )

                    ensureIndex(
                            index(
                                    HeartBeat::timestamp to true,
                                    HeartBeat::deviceInfo to true,
                                    HeartBeat::deviceId to true
                            )
                    )
                }
            }

            val reader = DatabaseReader(database)
            val writer = DatabaseWriter(database)

            val service = DataOperationService(reader, writer)

            server = ServerBuilder.forPort(portNumber).addService(service).build()

            server.start()

            Runtime.getRuntime().addShutdownHook(
                    Thread {
                        Log.info("JVM is shutting down, so a server will be also shutting down.")
                        server.shutdown()
                        Log.info("A server shuts down.")
                    }
            )

            Log.info("A server is started..")
        } catch (e: Exception) {
            Log.error("A server cannot be started.", e)
        }
    }

    fun await() {
        server.awaitTermination()
    }

    fun stop() {
        server.shutdown()
    }
}
