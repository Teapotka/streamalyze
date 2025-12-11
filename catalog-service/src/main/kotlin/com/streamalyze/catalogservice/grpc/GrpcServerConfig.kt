package com.streamalyze.catalogservice.grpc

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.SmartLifecycle
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@Configuration
class GrpcServerConfig(
    private val catalogGrpcService: CatalogGrpcService,
    @Value("\${grpc.server.port:9091}") private val port: Int,
) : SmartLifecycle {
    private var server: Server? = null
    private var running = false

    override fun isRunning(): Boolean = running

    override fun start() {
        if (running) return

        server =
            NettyServerBuilder
                .forPort(port)
                .addService(catalogGrpcService)
                .build()
                .start()

        running = true
        logger.info { "Starting catalog gRPC server on port $port" }
    }

    override fun stop(callback: Runnable) {
        try {
            server?.shutdown()
            callback.run()
        } finally {
            running = false
        }
    }

    override fun stop() {
        stop(Runnable { })
    }
}
