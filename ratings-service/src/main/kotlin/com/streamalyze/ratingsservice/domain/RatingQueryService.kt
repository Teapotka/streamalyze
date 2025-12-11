package com.streamalyze.ratingsservice.domain

import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Service
class RatingQueryService(
    private val ratingRepository: RatingRepository,
) {
    fun getAverageForMovie(movieId: Long): Mono<RatingSummary> {
        logger.info { "Computing average rating for movieId=$movieId" }

        return ratingRepository
            .findByMovieId(movieId)
            .collectList()
            .map { list ->
                if (list.isEmpty()) {
                    RatingSummary(
                        movieId = movieId,
                        averageRating = 0.0,
                        ratingCount = 0,
                    )
                } else {
                    val count = list.size.toLong()
                    val avg = list.map { it.rating }.average()
                    RatingSummary(
                        movieId = movieId,
                        averageRating = avg,
                        ratingCount = count,
                    )
                }
            }
    }
}
