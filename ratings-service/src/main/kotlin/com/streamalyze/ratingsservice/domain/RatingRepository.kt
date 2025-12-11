package com.streamalyze.ratingsservice.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface RatingRepository : ReactiveCrudRepository<Rating, Long> {
    fun findByMovieId(movieId: Long): Flux<Rating>
}
