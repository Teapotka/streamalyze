package com.streamalyze.catalogservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("movies")
data class Movie(
    @Id
    val id: Long,
    val title: String,
    @Column("genres")
    val genres: List<String>,
)
