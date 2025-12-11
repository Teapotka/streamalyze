package com.streamalyze.recommendationservice.web

import com.streamalyze.recommendationservice.domain.MovieRecommendationDto
import com.streamalyze.recommendationservice.domain.RecommendationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/dev/recommendations")
class RecommendationController(
    private val recommendationService: RecommendationService,
) {
    @GetMapping("/movies/{movieId}")
    fun getMovieRecommendation(
        @PathVariable movieId: Long,
    ): Mono<MovieRecommendationDto> = recommendationService.getMovieWithRating(movieId)
}
