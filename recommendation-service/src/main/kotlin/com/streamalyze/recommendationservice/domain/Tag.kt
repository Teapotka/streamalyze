package com.streamalyze.recommendationservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("tags")
data class Tag(
    @Id
    val id: Long? = null,
    @Column("user_id")
    val userId: Long,
    @Column("movie_id")
    val movieId: Long,
    val tag: String,
    @Column("tagged_at")
    val taggedAt: LocalDateTime,
)
