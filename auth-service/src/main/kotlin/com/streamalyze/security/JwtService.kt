package com.streamalyze.authservice.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date

@Component
class JwtService(
    @Value("\${security.jwt.secret}") secret: String,
    @Value("\${security.jwt.expiration-seconds:3600}") private val expirationSeconds: Long,
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(
        userId: Long,
        email: String,
        roles: String,
    ): String {
        val now = Instant.now()
        val expiry = now.plusSeconds(expirationSeconds)

        return Jwts
            .builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .claim("email", email)
            .claim("roles", roles)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}
