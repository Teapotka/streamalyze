package com.streamalyze.recommendationservice.domain

data class MovieRecommendationDto(
    val id: Long,
    val title: String,
    val genres: List<String>,
    val averageRating: Double?,
    val ratingCount: Long,
    val tags: List<String>,
)
