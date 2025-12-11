package com.streamalyze.ratingsservice.grpc

import com.streamalyze.ratings.v1.GetAverageRatingRequest
import com.streamalyze.ratings.v1.GetAverageRatingResponse
import com.streamalyze.ratings.v1.GetRatingsForMovieRequest
import com.streamalyze.ratings.v1.GetRatingsForMovieResponse
import com.streamalyze.ratings.v1.Rating
import com.streamalyze.ratings.v1.RatingsServiceGrpc
import com.streamalyze.ratingsservice.domain.RatingRepository
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.format.DateTimeFormatter

@Service
class RatingsGrpcService(
    private val ratingRepository: RatingRepository,
) : RatingsServiceGrpc.RatingsServiceImplBase() {
    private val log = LoggerFactory.getLogger(RatingsGrpcService::class.java)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun getRatingsForMovie(
        request: GetRatingsForMovieRequest,
        responseObserver: StreamObserver<GetRatingsForMovieResponse>,
    ) {
        log.info("gRPC getRatingsForMovie movieId={}", request.movieId)

        ratingRepository
            .findByMovieId(request.movieId)
            .map { r ->
                Rating
                    .newBuilder()
                    .setId(r.id ?: 0L)
                    .setUserId(r.userId)
                    .setMovieId(r.movieId)
                    .setRating(r.rating)
                    .setRatedAt(r.ratedAt.format(formatter))
                    .build()
            }.collectList()
            .map { ratings ->
                GetRatingsForMovieResponse
                    .newBuilder()
                    .addAllRatings(ratings)
                    .build()
            }.subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                { resp ->
                    responseObserver.onNext(resp)
                    responseObserver.onCompleted()
                },
                { ex ->
                    log.error("Error in getRatingsForMovie", ex)
                    responseObserver.onError(ex)
                },
            )
    }

    override fun getAverageRating(
        request: GetAverageRatingRequest,
        responseObserver: StreamObserver<GetAverageRatingResponse>,
    ) {
        log.info("gRPC getAverageRating movieId={}", request.movieId)

        val avgMono: Mono<GetAverageRatingResponse> =
            ratingRepository
                .findByMovieId(request.movieId)
                .collectList()
                .map { ratings ->
                    val count = ratings.size.toLong()
                    val avg = if (count == 0L) 0.0 else ratings.map { it.rating }.average()
                    GetAverageRatingResponse
                        .newBuilder()
                        .setMovieId(request.movieId)
                        .setAverageRating(avg)
                        .setRatingsCount(count)
                        .build()
                }

        avgMono
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe(
                { resp ->
                    responseObserver.onNext(resp)
                    responseObserver.onCompleted()
                },
                { ex ->
                    log.error("Error in getAverageRating", ex)
                    responseObserver.onError(ex)
                },
            )
    }
}
