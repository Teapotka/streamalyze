package com.streamalyze.ratingsservice.grpc

import com.streamalyze.ratings.v1.GetAverageRatingRequest
import com.streamalyze.ratings.v1.GetRatingsForMovieRequest
import com.streamalyze.ratingsservice.domain.Rating
import com.streamalyze.ratingsservice.domain.RatingRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@ExtendWith(MockitoExtension::class)
class RatingsGrpcServiceUnitTest {
    private lateinit var ratingRepository: RatingRepository
    private lateinit var service: RatingsGrpcService

    @BeforeEach
    fun setUp() {
        ratingRepository = mock()
        service = RatingsGrpcService(ratingRepository)
    }

    @Test
    fun `getAverageRating returns correct avg and count`() {
        val movieId = 2L
        val dateString1 = "2005-04-02 23:53:47"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDateTime1 = LocalDateTime.parse(dateString1, formatter)

        val dateString2 = "1996-12-25 15:26:09"
        val localDateTime2 = LocalDateTime.parse(dateString2, formatter)

        val r1 =
            Rating(
                id = 1L,
                userId = 1L,
                movieId = movieId,
                rating = 3.5,
                ratedAt = localDateTime1,
            )
        val r2 =
            Rating(
                id = 452L,
                userId = 5L,
                movieId = movieId,
                rating = 3.0,
                ratedAt = localDateTime2,
            )

        whenever(ratingRepository.findByMovieId(movieId))
            .thenReturn(Flux.just(r1, r2))

        val request =
            GetAverageRatingRequest
                .newBuilder()
                .setMovieId(movieId)
                .build()

        val observer = TestObserver<com.streamalyze.ratings.v1.GetAverageRatingResponse>()

        service.getAverageRating(request, observer)

        assertTrue(observer.await(), "gRPC call did not complete in time")
        assertNull(observer.error)
        assertTrue(observer.completed)

        val resp = observer.value ?: error("Response is null")

        assertEquals(movieId, resp.movieId)
        assertEquals(3.25, resp.averageRating, 1e-9)
        assertEquals(2L, resp.ratingCount)
    }

    @Test
    fun `getAverageRating returns zero for no ratings`() {
        val movieId = 77777777777L

        whenever(ratingRepository.findByMovieId(movieId))
            .thenReturn(Flux.empty())

        val request =
            GetAverageRatingRequest
                .newBuilder()
                .setMovieId(movieId)
                .build()

        val observer = TestObserver<com.streamalyze.ratings.v1.GetAverageRatingResponse>()

        service.getAverageRating(request, observer)

        assertTrue(observer.await(), "gRPC call did not complete in time")
        assertNull(observer.error)
        assertTrue(observer.completed)

        val resp = observer.value ?: error("Response is null")

        assertEquals(movieId, resp.movieId)
        assertEquals(0.0, resp.averageRating, 1e-9)
        assertEquals(0L, resp.ratingCount)
    }

    @Test
    fun `getRatingsForMovie maps ratings correctly`() {
        val movieId = 99L

        // use LocalDateTime (values here are arbitrary, can use your DB sample if you want)
        val dt1 = LocalDateTime.of(1997, 1, 2, 18, 3, 24)
        val dt2 = LocalDateTime.of(1996, 12, 13, 16, 25, 14)

        val r1 =
            Rating(
                id = 10L,
                userId = 1000L,
                movieId = movieId,
                rating = 2.5,
                ratedAt = dt1,
            )
        val r2 =
            Rating(
                id = 11L,
                userId = 2000L,
                movieId = movieId,
                rating = 4.0,
                ratedAt = dt2,
            )

        whenever(ratingRepository.findByMovieId(movieId))
            .thenReturn(Flux.just(r1, r2))

        val request =
            GetRatingsForMovieRequest
                .newBuilder()
                .setMovieId(movieId)
                .build()

        val observer = TestObserver<com.streamalyze.ratings.v1.GetRatingsForMovieResponse>()

        service.getRatingsForMovie(request, observer)

        assertTrue(observer.await(), "gRPC call did not complete in time")
        assertNull(observer.error)
        assertTrue(observer.completed)

        val resp = observer.value ?: error("Response is null")

        assertEquals(2, resp.ratingsCount)

        val first = resp.ratingsList[0]
        assertEquals(r1.id, first.id)
        assertEquals(r1.userId, first.userId)
        assertEquals(r1.movieId, first.movieId)
        assertEquals(r1.rating, first.rating, 1e-9)

        val firstInstant = r1.ratedAt.atOffset(ZoneOffset.UTC).toInstant()
        assertEquals(firstInstant.epochSecond, first.ratedAt.seconds)
        assertEquals(firstInstant.nano, first.ratedAt.nanos)

        val second = resp.ratingsList[1]
        assertEquals(r2.id, second.id)
        assertEquals(r2.userId, second.userId)
        assertEquals(r2.movieId, second.movieId)
        assertEquals(r2.rating, second.rating, 1e-9)

        val secondInstant = r2.ratedAt.atOffset(ZoneOffset.UTC).toInstant()
        assertEquals(secondInstant.epochSecond, second.ratedAt.seconds)
        assertEquals(secondInstant.nano, second.ratedAt.nanos)
    }
}
