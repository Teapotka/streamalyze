package com.streamalyze.catalogservice.config

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ElasticsearchConfig {
    @Bean
    fun elasticsearchClient(): RestHighLevelClient {
        // dev setup: ES is in Docker on localhost:9200
        return RestHighLevelClient(
            RestClient.builder(
                HttpHost("localhost", 9200, "http"),
            ),
        )
    }
}
