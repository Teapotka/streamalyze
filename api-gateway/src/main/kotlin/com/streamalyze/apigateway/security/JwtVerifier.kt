package com.streamalyze.apigateway.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtVerifier(
    @Value("\${security.jwt.secret}") secret: String,
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun parseToken(token: String): Claims =
        Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
}
