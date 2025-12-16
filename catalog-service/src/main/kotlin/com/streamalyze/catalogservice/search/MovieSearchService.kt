package com.streamalyze.catalogservice.search

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.elasticsearch.ElasticsearchStatusException
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.stereotype.Service

@Service
class MovieSearchService(
    private val esClient: RestHighLevelClient,
    meterRegistry: MeterRegistry,
) {
    private val indexName = "movies"

    private val searchHitsCounter: Counter =
        Counter
            .builder("catalog_search_hits_total")
            .description("Number of successful search queries that returned at least one result")
            .register(meterRegistry)

    private val searchMissesCounter: Counter =
        Counter
            .builder("catalog_search_misses_total")
            .description("Number of search queries that returned no results")
            .register(meterRegistry)

    fun indexMovie(doc: MovieSearchDocument) {
        val json =
            """
            {
              "title": ${jsonString(doc.title)},
              "genres": ${jsonArray(doc.genres)}
            }
            """.trimIndent()

        val request =
            IndexRequest(indexName)
                .id(doc.id.toString())
                .source(json, XContentType.JSON)

        esClient.index(request, RequestOptions.DEFAULT)
    }

    fun searchByTitlePrefix(
        query: String,
        limit: Int = 10,
    ): List<MovieSearchDocument> {
        val searchSource =
            SearchSourceBuilder()
                .query(QueryBuilders.matchPhrasePrefixQuery("title", query))
                .size(limit)

        val request = SearchRequest(indexName).source(searchSource)

        return try {
            val response = esClient.search(request, RequestOptions.DEFAULT)

            val docs =
                response.hits.map { hit ->
                    val source = hit.sourceAsMap
                    MovieSearchDocument(
                        id = hit.id.toLong(),
                        title = source["title"] as? String ?: "",
                        genres = (source["genres"] as? List<*>)?.map { it.toString() } ?: emptyList(),
                    )
                }
            if (docs.isEmpty()) {
                searchMissesCounter.increment()
            } else {
                searchHitsCounter.increment()
            }
            docs
        } catch (e: ElasticsearchStatusException) {
            if (e.message?.contains("index_not_found_exception") == true) {
                // index not yet created → just return empty instead of 500
                searchMissesCounter.increment()
                emptyList()
            } else {
                throw e
            }
        }
    }

    private fun jsonString(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

    private fun jsonArray(values: List<String>): String = values.joinToString(prefix = "[", postfix = "]") { jsonString(it) }
}
