package com.streamalyze.ratingsservice.domain

data class RatingSummary(
    val movieId: Long,
    val averageRating: Double,
    val ratingCount: Long,
)
