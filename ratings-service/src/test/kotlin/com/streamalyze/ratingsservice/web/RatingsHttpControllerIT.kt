package com.streamalyze.ratingsservice.web

import com.streamalyze.ratingsservice.domain.RatingQueryService
import com.streamalyze.ratingsservice.domain.RatingSummary
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class RatingsHttpControllerIT(
    @Autowired
    private val webTestClient: WebTestClient,
) {
    @MockitoBean
    private lateinit var ratingQueryService: RatingQueryService

    @Test
    fun `GET dev_ratings_movies_movieId_average returns summary from service`() {
        // given
        val movieId = 42L
        val summary =
            RatingSummary(
                movieId = movieId,
                averageRating = 4.5,
                ratingCount = 2L,
            )

        whenever(ratingQueryService.getAverageForMovie(movieId))
            .thenReturn(Mono.just(summary))

        webTestClient
            .get()
            .uri("/dev/ratings/movies/{movieId}/average", movieId)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("$.movieId")
            .isEqualTo(movieId.toInt())
            .jsonPath("$.averageRating")
            .isEqualTo(4.5)
            .jsonPath("$.ratingCount")
            .isEqualTo(2)
    }
}
