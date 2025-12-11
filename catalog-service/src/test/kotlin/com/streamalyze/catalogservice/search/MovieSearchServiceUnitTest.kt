package com.streamalyze.catalogservice.search

import org.elasticsearch.ElasticsearchStatusException
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.argumentCaptor

@ExtendWith(MockitoExtension::class)
class MovieSearchServiceUnitTest {

    @Mock
    private lateinit var esClient: RestHighLevelClient

    private lateinit var service: MovieSearchService

    @BeforeEach
    fun setUp() {
        service = MovieSearchService(esClient)
    }

    @Test
    fun `indexMovie sends IndexRequest with correct index id and body`() {

        val doc =
            MovieSearchDocument(
                id = 5L,
                title = "Father of the Bride Part II",
                genres = listOf("Comedy"),
            )

        service.indexMovie(doc)

        // capture the actual IndexRequest that was passed to esClient
        val requestCaptor = argumentCaptor<IndexRequest>()

        verify(esClient).index(requestCaptor.capture(), eq(RequestOptions.DEFAULT))

        val req = requestCaptor.firstValue

        // index + id
        assertEquals("movies", req.index())
        assertEquals("5", req.id())

        // body
        val json = req.source().utf8ToString()
        println("Indexed JSON: $json")
        assertTrue(json.contains("Father of the Bride Part II"))
        assertTrue(json.contains("\"genres\""))
    }

    @Test
    fun `searchByTitlePrefix maps ES hits to MovieSearchDocument`() {
        val query = "Father"

        val hit = mock<SearchHit>()
        whenever(hit.id).thenReturn("5")
        whenever(hit.sourceAsMap).thenReturn(
            mapOf(
                "title" to "Father of the Bride Part II",
                "genres" to listOf("Comedy"),
            ),
        )

        val hits = mock<SearchHits>()
        // Kotlin wants MutableIterator here → use mutableListOf
        whenever(hits.iterator()).thenReturn(mutableListOf(hit).iterator())

        val response = mock<SearchResponse>()
        whenever(response.hits).thenReturn(hits)

        whenever(esClient.search(any<SearchRequest>(), eq(RequestOptions.DEFAULT)))
            .thenReturn(response)

        val result = service.searchByTitlePrefix(query, limit = 10)

        assertEquals(1, result.size)
        val doc = result[0]
        assertEquals(5L, doc.id)
        assertEquals("Father of the Bride Part II", doc.title)
        assertEquals(listOf("Comedy"), doc.genres)
    }

    @Test
    fun `searchByTitlePrefix returns empty list when index_not_found_exception`() {
        val query = "Father"

        val ex = mock<ElasticsearchStatusException>()
        whenever(ex.message).thenReturn("index_not_found_exception: index [movies] not found")

        whenever(esClient.search(any<SearchRequest>(), eq(RequestOptions.DEFAULT)))
            .thenThrow(ex)

        val result = service.searchByTitlePrefix(query)

        assertTrue(result.isEmpty())
    }
}
