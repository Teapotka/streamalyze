package com.streamalyze.catalogservice.grpc

import com.streamalyze.catalog.v1.CatalogServiceGrpc
import com.streamalyze.catalog.v1.GetMovieRequest
import com.streamalyze.catalog.v1.GetMovieResponse
import com.streamalyze.catalog.v1.Movie
import com.streamalyze.catalogservice.domain.MovieRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import mu.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers

private val logger = KotlinLogging.logger {}

@Service
class CatalogGrpcService(
    private val movieRepository: MovieRepository,
) : CatalogServiceGrpc.CatalogServiceImplBase() {
    override fun getMovie(
        request: GetMovieRequest,
        responseObserver: StreamObserver<GetMovieResponse>,
    ) {
        logger.info { "gRPC GetMovie id=${request.id}" }

        movieRepository
            .findById(request.id)
            .publishOn(Schedulers.boundedElastic())
            .map { movie ->
                GetMovieResponse
                    .newBuilder()
                    .setMovie(
                        Movie
                            .newBuilder()
                            .setId(movie.id ?: 0L)
                            .setTitle(movie.title)
                            .addAllGenres(movie.genres) // assuming List<String>
                            .build(),
                    ).build()
            }.switchIfEmpty(
                reactor.core.publisher.Mono.error(
                    Status.NOT_FOUND.withDescription("Movie ${request.id} not found").asRuntimeException(),
                ),
            ).subscribe(
                { resp ->
                    responseObserver.onNext(resp)
                    responseObserver.onCompleted()
                },
                { ex ->
                    logger.error(ex) { "Error in GetMovie" }
                    responseObserver.onError(
                        if (ex is io.grpc.StatusException || ex is io.grpc.StatusRuntimeException) {
                            ex
                        } else {
                            Status.INTERNAL
                                .withDescription(ex.message)
                                .withCause(ex)
                                .asRuntimeException()
                        },
                    )
                },
            )
    }
}
