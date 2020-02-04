package kaist.iclab.abclogger

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject

fun main(vararg args: String) {
    val portNumber = args.firstOrNull()?.toIntOrNull() ?: 50051
    startKoin {
        modules(serverModule)
    }
    val app = App(portNumber)
    app.start()
}

class App(portNumber: Int) : KoinComponent {
    private val dataOperation : DataOperationService by inject()
    private val inserter: DBWriter by inject()

    val server: Server = ServerBuilder.forPort(portNumber)
            .addService(dataOperation)
            .executor(Dispatchers.IO.asExecutor())
            .build()

    fun start() {
        inserter.subscribe()
        server.start()
        server.awaitTermination()
    }

    fun stop() {
        inserter.unsubscribe()
        server.shutdown()
    }
}
