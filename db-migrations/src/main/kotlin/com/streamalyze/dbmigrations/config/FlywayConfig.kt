package com.streamalyze.dbmigrations.config

import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@Configuration
class FlywayConfig(
    @Value("\${spring.datasource.url}")
    private val url: String,
    @Value("\${spring.datasource.username}")
    private val username: String,
    @Value("\${spring.datasource.password}")
    private val password: String,
) {
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        logger.info { "Running Flyway migrations on $url $username" }

        return Flyway
            .configure()
            .dataSource(url, username, password)
            .baselineOnMigrate(true) // можно убрать, если БД всегда чистая
            .locations("classpath:db/migration") // здесь лежат V1__...sql, V2__...sql и т.д.
            .load()
    }
}
