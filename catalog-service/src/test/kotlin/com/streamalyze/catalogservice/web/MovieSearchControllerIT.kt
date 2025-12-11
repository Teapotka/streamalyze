package com.streamalyze.catalogservice.web

import com.streamalyze.catalogservice.domain.MovieRepository
import com.streamalyze.catalogservice.domain.Movie
import org.mockito.kotlin.verify
import org.mockito.kotlin.argThat
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import com.streamalyze.catalogservice.search.MovieSearchDocument
import com.streamalyze.catalogservice.search.MovieSearchService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.mockito.kotlin.argThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.junit.jupiter.api.Assertions.assertEquals

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@AutoConfigureWebTestClient
class MovieSearchControllerIT(
    @Autowired
    private val webTestClient: WebTestClient,
) {
    @MockitoBean
    private lateinit var movieRepository: MovieRepository

    @MockitoBean
    private lateinit var movieSearchService: MovieSearchService

    @Test
    fun `GET dev_search_movies returns matching documents from service`() {
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

        whenever(movieSearchService.searchByTitlePrefix(query)).thenReturn(docs)

        // when + then
        val result: EntityExchangeResult<List<MovieSearchDocument>> =
            webTestClient
            .get()
            .uri { builder ->
                builder
                    .path("/dev/movies/search")
                    .queryParam("query", query)
                    .build()
            }.exchange()
            .expectStatus()
            .isOk
            .expectBodyList(MovieSearchDocument::class.java)
            .hasSize(2)
            .returnResult()

        val body = result.responseBody!!
        assertEquals(5L, body[0].id)
        assertEquals("Father of the Bride Part II", body[0].title)
    }

    @Test
    fun `POST dev_movies_search_id_index indexes movie into ES`() {
        // given
        val movieId = 5L
        val movie =
            Movie(
                id = movieId,
                title = "Father of the Bride Part II",
                genres = listOf("Comedy"),
            ) 

        whenever(movieRepository.findById(movieId)).thenReturn(Mono.just(movie))

        // when + then
        webTestClient
            .post()
            .uri("/dev/movies/search/{id}/index", movieId)
            .exchange()
            .expectStatus().isOk

        verify(movieSearchService).indexMovie(
            argThat<MovieSearchDocument> {
                id == movieId &&
                title == "Father of the Bride Part II" &&
                genres == listOf("Comedy")
            },
        )
    }

    @Test
    fun `GET dev_movies_id returns 404 when movie not found`() {
        // given
        val missingId = 999L
        whenever(movieRepository.findById(missingId))
            .thenReturn(Mono.empty())

        // when + then
        webTestClient
            .get()
            .uri("/dev/movies/{id}", missingId) 
            .exchange()
            .expectStatus().isNotFound
    }
}
