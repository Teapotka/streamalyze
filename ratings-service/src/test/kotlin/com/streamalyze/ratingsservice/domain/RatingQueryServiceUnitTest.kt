package com.streamalyze.ratingsservice.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class RatingQueryServiceUnitTest {
    private val ratingRepository: RatingRepository = mock()

    private lateinit var service: RatingQueryService

    @BeforeEach
    fun setUp() {
        service = RatingQueryService(ratingRepository)
    }

    @Test
    @DisplayName("getAverageForMovie returns correct summary when ratings exist")
    fun `getAverageForMovie returns summary for non-empty list`() {
        val movieId = 42L

        val r1: Rating = mock()
        val r2: Rating = mock()

        whenever(r1.rating).thenReturn(3.0)
        whenever(r2.rating).thenReturn(5.0)

        whenever(ratingRepository.findByMovieId(movieId))
            .thenReturn(Flux.just(r1, r2))

        val result = service.getAverageForMovie(movieId)

        StepVerifier
            .create(result)
            .assertNext { summary ->
                assertThat(summary.movieId).isEqualTo(movieId)
                assertThat(summary.ratingCount).isEqualTo(2L)
                assertThat(summary.averageRating).isEqualTo(4.0)
            }.verifyComplete()

        verify(ratingRepository).findByMovieId(movieId)
    }

    @Test
    @DisplayName("getAverageForMovie returns zero summary when no ratings")
    fun `getAverageForMovie returns zero summary for empty list`() {
        val movieId = 999L

        whenever(ratingRepository.findByMovieId(movieId))
            .thenReturn(Flux.empty())

        val result = service.getAverageForMovie(movieId)

        StepVerifier
            .create(result)
            .assertNext { summary ->
                assertThat(summary.movieId).isEqualTo(movieId)
                assertThat(summary.ratingCount).isEqualTo(0L)
                assertThat(summary.averageRating).isEqualTo(0.0)
            }.verifyComplete()

        verify(ratingRepository).findByMovieId(movieId)
    }
}
