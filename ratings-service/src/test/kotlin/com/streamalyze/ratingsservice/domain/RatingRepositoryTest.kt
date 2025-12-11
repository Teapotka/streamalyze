package com.streamalyze.ratingsservice.domain

import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

private val logger = KotlinLogging.logger {}

@SpringBootTest
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
