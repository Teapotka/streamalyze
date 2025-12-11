package com.streamalyze.catalogservice.web

import com.streamalyze.catalogservice.domain.MovieRepository
import com.streamalyze.catalogservice.search.MovieSearchDocument
import com.streamalyze.catalogservice.search.MovieSearchService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class MovieSearchControllerUnitTest {
    private val movieRepository: MovieRepository = mockk(relaxed = true)
    private val movieSearchService: MovieSearchService = mockk()

    private val controller =
        MovieSearchController(
            movieRepository = movieRepository,
            movieSearchService = movieSearchService,
        )

    @Test
    fun `searchByTitle delegates to MovieSearchService and returns Flux of documents`() {
        // given
        val query = "Father"
        val docs =
            listOf(
                MovieSearchDocument(
                    id = 5L,
                    title = "Father of the Bride Part II",
                    genres = listOf("Comedy"),
                ),
                MovieSearchDocument(
                    id = 10L,
                    title = "Father Something",
                    genres = listOf("Drama"),
                ),
            )

        every { movieSearchService.searchByTitlePrefix(query) } returns docs

        // when
        val resultFlux: Flux<MovieSearchDocument> = controller.searchByTitle(query)

        // then
        StepVerifier
            .create(resultFlux)
            .expectNextMatches { it.id == 5L && it.title.contains("Father") }
            .expectNextMatches { it.id == 10L && it.title.contains("Father") }
            .verifyComplete()

        verify(exactly = 1) { movieSearchService.searchByTitlePrefix(query) }
    }
}
