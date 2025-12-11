package com.streamalyze.recommendationservice.config

import com.streamalyze.catalog.v1.CatalogServiceGrpc
import com.streamalyze.ratings.v1.RatingsServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcClientsConfig {
    @Bean
    fun ratingsChannel(
        @Value("\${ratings.grpc.host:localhost}") host: String,
        @Value("\${ratings.grpc.port:9090}") port: Int,
    ): ManagedChannel =
        NettyChannelBuilder
            .forAddress(host, port)
            .usePlaintext()
            .build()

    @Bean
    fun ratingsBlockingStub(ratingsChannel: ManagedChannel): RatingsServiceGrpc.RatingsServiceBlockingStub =
        RatingsServiceGrpc.newBlockingStub(ratingsChannel)

    @Bean
    fun catalogChannel(
        @Value("\${catalog.grpc.host:localhost}") host: String,
        @Value("\${catalog.grpc.port:9091}") port: Int,
    ): ManagedChannel =
        NettyChannelBuilder
            .forAddress(host, port)
            .usePlaintext()
            .build()

    @Bean
    fun catalogBlockingStub(catalogChannel: ManagedChannel): CatalogServiceGrpc.CatalogServiceBlockingStub =
        CatalogServiceGrpc.newBlockingStub(catalogChannel)
}
