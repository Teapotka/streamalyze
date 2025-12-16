package com.streamalyze.ratingsservice.domain

import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import reactor.test.StepVerifier

private val logger = KotlinLogging.logger {}

@SpringBootTest
@TestPropertySource(
    properties = [
        // disable Eureka during tests so it doesn’t matter if 8761 is down
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false",
    ],
)
class RatingRepositoryTest
    @Autowired
    constructor(
        private val ratingRepository: RatingRepository,
    ) {
        @Test
        fun `should count ratings reactively`() {
            logger.info { "Starting RatingRepository reactive count() test" }

            val countMono = ratingRepository.count()

            StepVerifier
                .create(countMono)
                .assertNext { count ->
                    logger.info { "Ratings in DB = " + count }
                    assertThat(count).isGreaterThan(0L)
                }.verifyComplete()
        }
    }
