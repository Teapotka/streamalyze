package com.streamalyze.catalogservice.config

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("docker")
class ElasticsearchDockerConfig {
    @Bean
    fun elasticsearchClient(): RestHighLevelClient =
        RestHighLevelClient(
            RestClient.builder(
                HttpHost("elasticsearch", 9200, "http"),
            ),
        )
}
