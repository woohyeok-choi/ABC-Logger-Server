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
          dbName: String,
          userName: String,
          password: String
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

    private val masterProperties by lazy {
        Properties().apply {
            put("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
            put("dataSource.databaseName", dbName)
            put("dataSource.serverName", serverName)
            put("dataSource.portNumber", portNumber)
            put("dataSource.user", userName)
            put("dataSource.password", password)
        }
    }

    private val readOnlyProperties by lazy {
        val masterProperties = masterProperties
        val readerProperties = Properties().apply {
            put("dataSource.user", "abcreader")
        }
        mergeProperties(masterProperties, readerProperties)
    }

    private val writeOnlyProperties by lazy {
        val masterProperties = masterProperties
        val writerProperties = Properties().apply {
            put("dataSource.user", "abcwriter")
        }
        mergeProperties(masterProperties, writerProperties)
    }

    private val readOnlyDataSource by lazy { HikariDataSource(HikariConfig(readOnlyProperties)) }

    private val writeOnlyDataSource by lazy { HikariDataSource(HikariConfig(writeOnlyProperties)) }

    fun bind() {
        val masterSource = HikariDataSource(HikariConfig(masterProperties))
        val masterDb = Database.connect(masterSource)

        transaction(masterDb) {
            createSchema()
            SchemaUtils.create(*tables)

            createUser(properties = readOnlyProperties, readOnly = true)
            createUser(properties = writeOnlyProperties, readOnly = false)
        }
        masterSource.close()
    }

    val readOnlyDb by lazy { Database.connect(readOnlyDataSource) }

    val writeOnlyDb by lazy { Database.connect(writeOnlyDataSource) }

    companion object {
        const val SCHEMA_NAME = "data"
    }
}