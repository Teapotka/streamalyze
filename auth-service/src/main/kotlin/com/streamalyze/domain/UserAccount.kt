package com.streamalyze.authservice.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class UserAccount(
    @Id
    val id: Long? = null,
    val email: String,
    val passwordHash: String,
    val roles: String, // e.g. "ROLE_USER,ROLE_ADMIN"
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
