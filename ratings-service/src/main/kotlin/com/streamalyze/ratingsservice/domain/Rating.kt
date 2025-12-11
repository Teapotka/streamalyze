package com.streamalyze.ratingsservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("ratings")
data class Rating(
    @Id
    val id: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("movie_id")
    val movieId: Long,
    val rating: Double,
    @Column("rated_at")
    val ratedAt: LocalDateTime,
)
