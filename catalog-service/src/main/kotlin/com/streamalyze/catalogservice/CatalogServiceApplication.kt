package com.streamalyze.catalogservice

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class CatalogServiceApplication

fun main(args: Array<String>) {
    logger.info { "Starting catalog-service..." }
    runApplication<CatalogServiceApplication>(*args)
}
