package com.streamalyze.recommendationservice.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface TagRepository : ReactiveCrudRepository<Tag, Long> {
    // 5 latest by tagged_at
    fun findTop5ByMovieIdOrderByTaggedAtDesc(movieId: Long): Flux<Tag>
}
