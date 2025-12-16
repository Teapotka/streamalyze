package com.streamalyze.ratingsservice.grpc

import io.grpc.Server
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@ConditionalOnProperty(
    prefix = "ratings.grpc",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true, // prod + dev get it by default
)
class GrpcServerConfig(
    private val ratingsGrpcService: RatingsGrpcService,
) {
    private val log = LoggerFactory.getLogger(GrpcServerConfig::class.java)

    private var server: Server? = null

    @Bean(initMethod = "start")
    fun grpcServer(): Server {
        val port = 9090
        server =
            NettyServerBuilder
                .forPort(port)
                .addService(ratingsGrpcService)
                .build()

        log.info("Starting gRPC server on port {}", port)
        return server!!
    }

    @PreDestroy
    fun shutdown() {
        server?.let {
            log.info("Shutting down gRPC server")
            it.shutdown()
            it.awaitTermination(5, TimeUnit.SECONDS)
        }
    }
}
