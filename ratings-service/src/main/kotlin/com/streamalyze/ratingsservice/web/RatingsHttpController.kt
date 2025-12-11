package com.streamalyze.ratingsservice.web

import com.streamalyze.ratingsservice.domain.RatingQueryService
import com.streamalyze.ratingsservice.domain.RatingSummary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/dev/ratings")
class RatingsHttpController(
    private val ratingQueryService: RatingQueryService,
) {
    @GetMapping("/movies/{movieId}/average")
    fun getAverageForMovie(
        @PathVariable movieId: Long,
    ): Mono<RatingSummary> = ratingQueryService.getAverageForMovie(movieId)
}
