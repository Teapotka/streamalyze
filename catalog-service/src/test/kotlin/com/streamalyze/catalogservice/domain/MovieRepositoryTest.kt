package com.streamalyze.catalogservice.domain

import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

private val logger = KotlinLogging.logger {}

@SpringBootTest
class MovieRepositoryTest(
    @Autowired private val movieRepository: MovieRepository,
) {
    @Test
    fun `read movie with id 5 from DB`() {
        val movieMono = movieRepository.findById(5)

        StepVerifier
            .create(movieMono)
            .assertNext { movie: Movie ->
                logger.info { "MOVIE: " + movie }
                assertThat(movie.id).isEqualTo(5)
                assertThat(movie.title).isEqualTo("Father of the Bride Part II")
                assertThat(movie.genres).containsExactly("Comedy")
            }.verifyComplete()
    }
}
