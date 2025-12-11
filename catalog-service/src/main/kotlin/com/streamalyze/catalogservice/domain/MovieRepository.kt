package com.streamalyze.catalogservice.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface MovieRepository : R2dbcRepository<Movie, Long> {
    fun findByTitleContainingIgnoreCase(titlePart: String): Flux<Movie>
}
