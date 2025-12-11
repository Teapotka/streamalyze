package com.streamalyze.catalogservice.search

data class MovieSearchDocument(
    val id: Long,
    val title: String,
    val genres: List<String>,
)
