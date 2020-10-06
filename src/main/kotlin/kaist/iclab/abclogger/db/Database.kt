package kaist.iclab.abclogger.db


import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCommandException
import com.mongodb.MongoTimeoutException
import com.mongodb.client.model.IndexOptions
import com.mongodb.event.CommandFailedEvent
import com.mongodb.event.CommandListener
import com.mongodb.event.CommandStartedEvent
import com.mongodb.event.CommandSucceededEvent
import kaist.iclab.abclogger.common.Log
import kaist.iclab.abclogger.schema.Datum
import org.bson.Document
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.index
import org.litote.kmongo.reactivestreams.KMongo
import java.util.concurrent.atomic.AtomicBoolean


class Database(
    private val serverName: String,
    private val portNumber: Int,
    private val dbName: String,
    private val rootUserName: String,
    private val rootPassword: String,
    private val writerUserName: String = rootUserName,
    private val writerUserPassword: String = rootPassword,
    private val readUsers: Map<String, String> = mapOf()
) {
    private var isBound: AtomicBoolean = AtomicBoolean(false)
    private lateinit var client: CoroutineClient

    val database: CoroutineDatabase by lazy {
        if (!isBound.get()) throw IllegalStateException("The server is not bound to MongoDB.")

        client.getDatabase(dbName)
    }

    inline fun <reified T : Any> collection() = database.getCollection<T>()

    suspend fun bind() {
        Log.info("Database.bind() - begin binding.")

        val rootConnStr = "mongodb://$rootUserName:$rootPassword@$serverName:$portNumber"
        val writerConnStr = "mongodb://$writerUserName:$writerUserPassword@$serverName:$portNumber/$dbName"

        KMongo.createClient(rootConnStr).coroutine.use { client ->
            val isConnected = checkConnection(client = client, dbName = dbName)

            if (!isConnected) throw IllegalStateException("MongoDb server is not instantiated.")

            createOrUpdateUser(
                client = client,
                dbName = dbName,
                userName = writerUserName,
                password = writerUserPassword,
                isReadOnly = false
            )

            readUsers.entries.forEach { (userName, password) ->
                createOrUpdateUser(
                    client = client,
                    dbName = dbName,
                    userName = userName,
                    password = password,
                    isReadOnly = true
                )
            }
        }

        val setting = MongoClientSettings.builder().apply {
            applyConnectionString(ConnectionString(writerConnStr))
            addCommandListener(object : CommandListener {
                override fun commandStarted(event: CommandStartedEvent?) {
                    Log.info(event)
                }

                override fun commandFailed(event: CommandFailedEvent?) {
                    Log.error(event)
                }

                override fun commandSucceeded(event: CommandSucceededEvent?) {
                    Log.info(event)
                }
            })
        }.build()

        val writerClient = KMongo.createClient(setting).coroutine
        val isConnected = checkConnection(client = writerClient, dbName = dbName)

        if (!isConnected) throw IllegalStateException("MongoDb server is not instantiated.")

        client = writerClient
        isBound.set(true)

        Log.info("Database.bind() - complete binding.")
    }

    suspend inline fun <reified T: Any> createIndex(index: Bson) {
        collection<T>().ensureIndex(index)
    }

    private suspend fun checkConnection(
        client: CoroutineClient,
        dbName: String,
        nTries: Int = 10
    ): Boolean {
        for (i in (0 until nTries)) {
            try {
                client.getDatabase(dbName).runCommand<Document>(
                    """
                        {
                            ping: 1
                        }
                        """.trimIndent()
                )
                return true
            } catch (e: MongoTimeoutException) {
                e.printStackTrace()
            }
        }
        return false
    }

    private suspend fun createOrUpdateUser(
        client: CoroutineClient,
        dbName: String,
        userName: String,
        password: String,
        isReadOnly: Boolean
    ) {
        val database = client.getDatabase(dbName)

        try {
            database.runCommand<Document>(
                """
                    {
                        createUser: "$userName",
                        pwd: "$password",
                        roles: [
                            {
                                role: "${if(isReadOnly) "read" else "readWrite"}",
                                db: "$dbName"
                            }
                        ]
                    }
                    """.trimIndent()
            )
        } catch (e: MongoCommandException) {
            /**
             * Here, if a user exists, then grant its role.
             */
            database.runCommand<Document>(
                """
                        {
                            grantRolesToUser: "$userName",
                            roles: [
                                {
                                    role: "${if(isReadOnly) "read" else "readWrite"}",
                                    db: "$dbName"
                                }
                            ]
                        }
                    """.trimIndent()
            )
        }
    }
}