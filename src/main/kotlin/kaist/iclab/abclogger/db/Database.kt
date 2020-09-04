package kaist.iclab.abclogger


import com.mongodb.MongoTimeoutException
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo


class Database(
        private val serverName: String,
        private val portNumber: Int,
        private val dbName: String,
        private val rootUserName: String,
        private val rootPassword: String,
        private val writerUserName: String,
        private val writerUserPassword: String
) {
    private var isBound = false
    private lateinit var client: CoroutineClient

    val database: CoroutineDatabase by lazy {
        if (!isBound) throw IllegalStateException("The server is not bound to MongoDB.")

        client.getDatabase(dbName)
    }

    inline fun <reified T: Any> collection() = database.getCollection<T>()

    suspend fun bind() {
        val rootConnStr = "mongodb://$rootUserName:$rootPassword@$serverName:$portNumber"
        val writerConnStr = "mongodb://$rootUserName:$rootPassword@$serverName:$portNumber"

        KMongo.createClient(rootConnStr).coroutine.use { client ->
            val isConnected = checkConnection(client = client, dbName = dbName)

            if (!isConnected) throw IllegalStateException("MongoDb server is not instantiated.")


            createOrUpdateUser(
                    client = client,
                    dbName = dbName,
                    userName = writerUserName,
                    password = writerUserPassword
            )
        }
        val writerClient = KMongo.createClient(writerConnStr).coroutine
        val isConnected = checkConnection(client = writerClient, dbName = dbName)
        if (!isConnected) throw IllegalStateException("MongoDb server is not instantiated.")

        client = writerClient
        isBound = true
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
            password: String
    ) {

        val database = client.getDatabase(dbName)
        val user = database.runCommand<Document>(
                """
                {
                    userInfo: "$userName"
                }
                """.trimIndent()
        )?.getList("users", Document::class.java)?.firstOrNull()

        if (user == null) {
            database.runCommand<Document>(
                    """
                    {
                        createUser: "$userName",
                        pwd: "$password",
                        roles: [
                            {
                                role: "readWrite",
                                db: "$dbName"
                            }
                        ]
                    }
                    """.trimIndent()
            )
        } else {
            val isRoleConsistent = user.getList("roles", Document::class.java).any { document ->
                document.getString("db") == dbName && document.getString("role") == "readWrite"
            }

            if (!isRoleConsistent) {
                database.runCommand<Document>(
                        """
                        {
                            grantRolesToUser: "$userName",
                            roles: [
                                {
                                    role: "readWrite",
                                    db: "$dbName"
                                }
                            ]
                        }
                    """.trimIndent()
                )
            }
        }
    }
}