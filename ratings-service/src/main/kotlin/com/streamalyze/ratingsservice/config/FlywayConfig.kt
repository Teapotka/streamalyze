package com.streamalyze.catalogservice.config

import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

private val logger = KotlinLogging.logger {}

@Configuration
class FlywayConfig {
    @Bean(initMethod = "migrate")
    fun flyway(env: Environment): Flyway {
        val url = env.getRequiredProperty("spring.datasource.url")
        val user = env.getRequiredProperty("spring.datasource.username")
        val password = env.getRequiredProperty("spring.datasource.password")

        logger.info { "HUJ" + url + " " + user + " " + password }

        return Flyway
            .configure()
            .dataSource(url, user, password)
            .locations("classpath:db/migration")
            .load()
    }
}
