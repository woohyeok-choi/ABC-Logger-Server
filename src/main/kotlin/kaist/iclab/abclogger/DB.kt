package kaist.iclab.abclogger

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class DB (private val tables: Array<BaseTable>,
          serverName: String,
          portNumber: Int,
          dbName: String
) {
    private fun Transaction.createUser(properties: Properties, readOnly: Boolean) {
        val userName = properties.getProperty("dataSource.user")
                ?: throw IllegalArgumentException("There is no property named \'dataSource.user\'.")
        val password = properties.getProperty("dataSource.password")
                ?: throw IllegalArgumentException("There is no property named \'dataSource.password\'.")
        val databaseName = properties.getProperty("dataSource.databaseName")
                ?: throw IllegalArgumentException("There is no property named \'dataSource.databaseName\'.")

        val createStatement = """
            DO
            ${'$'}CREATE_USER${'$'}
                BEGIN
                    IF NOT EXISTS(SELECT * FROM $databaseName.pg_catalog.pg_roles WHERE rolname = '${userName}') THEN
                        CREATE ROLE $userName LOGIN PASSWORD '$password' ;
                    END IF;
                END;
            ${'$'}CREATE_USER${'$'}
        """.trimIndent()

        val grantStatement = if(readOnly) {
            """
            GRANT USAGE ON SCHEMA $SCHEMA_NAME TO $userName;
            GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA $SCHEMA_NAME TO $userName;
            GRANT SELECT ON ALL TABLES IN SCHEMA $SCHEMA_NAME TO $userName;
            """
        } else {
            """
            GRANT USAGE ON SCHEMA $SCHEMA_NAME TO $userName;
            GRANT ALL ON ALL SEQUENCES IN SCHEMA $SCHEMA_NAME TO $userName;
            GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA $SCHEMA_NAME TO $userName;
            """
        }.trimIndent()

        exec(createStatement)
        exec(grantStatement)
    }

    private fun Transaction.createSchema() {
        val createStatement = "CREATE SCHEMA IF NOT EXISTS \"$SCHEMA_NAME\";"
        exec(createStatement)
    }

    private fun mergeProperties(masterProperties: Properties, subProperties: Properties) =
            Properties().apply {
                putAll(masterProperties)
                putAll(subProperties)
            }

    private fun loadProperties(resourceName: String) : Properties {
        val inputStream = DB::class.java.classLoader.getResourceAsStream(resourceName)
                ?: throw IllegalArgumentException("There is not resource file named $resourceName.")
        return Properties().apply { load(inputStream) }
    }

    private val masterProperties by lazy {
        loadProperties(FILE_MASTER_PROPERTIES).apply {
            put("dataSource.databaseName", dbName)
            put("dataSource.serverName", serverName)
            put("dataSource.portNumber", portNumber)
        }
    }

    private val readOnlyDataSource by lazy {
        val masterProperties = masterProperties
        val readerProperties = loadProperties(FILE_READER_PROPERTIES)

        return@lazy HikariDataSource(HikariConfig(mergeProperties(masterProperties, readerProperties)))
    }

    private val writeOnlyDataSource by lazy {
        val masterProperties = masterProperties
        val writerProperties = loadProperties(FILE_WRITER_PROPERTIES)

        return@lazy HikariDataSource(HikariConfig(mergeProperties(masterProperties, writerProperties)))
    }

    fun bind() {
        val masterSource = HikariDataSource(HikariConfig(masterProperties))
        val masterDb = Database.connect(masterSource)

        val readerProperties = loadProperties(FILE_READER_PROPERTIES)
        val writerProperties = loadProperties(FILE_WRITER_PROPERTIES)

        transaction(masterDb) {
            createSchema()
            SchemaUtils.create(*tables)

            createUser(properties = mergeProperties(masterProperties, readerProperties), readOnly = true)
            createUser(properties = mergeProperties(masterProperties, writerProperties), readOnly = false)
        }
        masterSource.close()
    }

    val readOnlyDb by lazy { Database.connect(readOnlyDataSource) }

    val writeOnlyDb by lazy { Database.connect(writeOnlyDataSource) }

    companion object {
        const val SCHEMA_NAME = "data"

        private const val FILE_MASTER_PROPERTIES = "master.properties"
        private const val FILE_WRITER_PROPERTIES = "writer.properties"
        private const val FILE_READER_PROPERTIES = "reader.properties"
    }
}