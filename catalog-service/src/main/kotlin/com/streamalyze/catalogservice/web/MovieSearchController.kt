package com.streamalyze.catalogservice.web

import com.streamalyze.catalogservice.domain.MovieRepository
import com.streamalyze.catalogservice.search.MovieSearchDocument
import com.streamalyze.catalogservice.search.MovieSearchService
import mu.KotlinLogging
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/dev/movies/search")
class MovieSearchController(
    private val movieRepository: MovieRepository,
    private val movieSearchService: MovieSearchService,
) {
    // index one movie by id from Postgres into ES
    @PostMapping("/{id}/index")
    fun indexMovie(
        @PathVariable id: Long,
    ): Mono<Void> =
        movieRepository
            .findById(id)
            .map { movie ->
                val doc =
                    MovieSearchDocument(
                        id = movie.id!!,
                        title = movie.title,
                        genres = movie.genres,
                    )
                movieSearchService.indexMovie(doc)
                doc
            }.then()

    // search ES by title prefix
    @GetMapping
    fun searchByTitle(
        @RequestParam query: String,
    ): Flux<MovieSearchDocument> =
        Mono
            .fromCallable {
                movieSearchService.searchByTitlePrefix(query)
            }.flatMapMany { Flux.fromIterable(it) }

    @PostMapping("/reindex")
    fun reindexAll() =
        movieRepository
            .findAll()
            .map { movie ->
                logger.info { "REINDEX: " + movie }
                val doc =
                    MovieSearchDocument(
                        id = movie.id!!,
                        title = movie.title,
                        genres = movie.genres,
                    )
                movieSearchService.indexMovie(doc)
            }.then()
}
