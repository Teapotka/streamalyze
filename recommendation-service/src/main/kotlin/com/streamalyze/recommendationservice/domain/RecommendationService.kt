package com.streamalyze.recommendationservice.domain

import com.streamalyze.catalog.v1.CatalogServiceGrpc
import com.streamalyze.catalog.v1.GetMovieRequest
import com.streamalyze.ratings.v1.GetAverageRatingRequest
import com.streamalyze.ratings.v1.RatingsServiceGrpc
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

private val logger = KotlinLogging.logger {}

@Service
class RecommendationService(
    private val catalogStub: CatalogServiceGrpc.CatalogServiceBlockingStub,
    private val ratingsStub: RatingsServiceGrpc.RatingsServiceBlockingStub,
    private val tagRepository: TagRepository,
    private val meterRegistry: MeterRegistry,
) {
    private val recommendationTimer: Timer =
        Timer
            .builder("recommendation_latency_seconds")
            .description("Time to compute movie recommendations")
            .publishPercentiles(0.5, 0.9, 0.99)
            .register(meterRegistry)

    fun getMovieWithRating(movieId: Long): Mono<MovieRecommendationDto> {
        logger.info { "Building recommendation view via gRPC for movieId=$movieId" }

        val sample = Timer.start(meterRegistry)

        val movieMono =
            Mono
                .fromCallable {
                    val resp =
                        catalogStub.getMovie(
                            GetMovieRequest
                                .newBuilder()
                                .setId(movieId)
                                .build(),
                        )
                    resp.movie
                }.subscribeOn(Schedulers.boundedElastic())

        val ratingMono =
            Mono
                .fromCallable {
                    val resp =
                        ratingsStub.getAverageRating(
                            GetAverageRatingRequest
                                .newBuilder()
                                .setMovieId(movieId)
                                .build(),
                        )
                    resp
                }.subscribeOn(Schedulers.boundedElastic())

        val tagsMono =
            tagRepository
                .findTop5ByMovieIdOrderByTaggedAtDesc(movieId)
                .map { it.tag }
                .collectList()

        return Mono
            .zip(movieMono, ratingMono, tagsMono)
            .map { tuple ->
                val movie = tuple.t1
                val rating = tuple.t2
                val tags = tuple.t3

                MovieRecommendationDto(
                    id = movie.id,
                    title = movie.title,
                    genres = movie.genresList,
                    averageRating =
                        if (rating.ratingCount > 0) {
                            rating.averageRating
                        } else {
                            null
                        },
                    ratingCount = rating.ratingCount,
                    tags = tags,
                )
            }.doFinally {
                // stop timer on success, error or cancel
                sample.stop(recommendationTimer)
            }
    }
}
