package com.streamalyze.ratingsservice

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

private val logger = KotlinLogging.logger {}

@SpringBootApplication
class RatingsServiceApplication

fun main(args: Array<String>) {
    logger.info { "Starting ratings-service..." }
    runApplication<RatingsServiceApplication>(*args)
}
