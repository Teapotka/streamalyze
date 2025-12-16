package com.streamalyze.apigateway.security

import mu.KotlinLogging
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class JwtAuthFilter(
    private val jwtVerifier: JwtVerifier,
) : GlobalFilter,
    Ordered {
    override fun getOrder(): Int = -1 // run early, before routing

    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain,
    ): Mono<Void> {
        val request = exchange.request
        val path = request.path.toString()

        logger.info { "JwtAuthFilter invoked, path=$path" }

        // 1) Paths without auth (auth-service endpoints, health, etc.)
        if (path.startsWith("/dev/auth/") ||
            path.startsWith("/actuator") ||
            path.startsWith("/eureka")
        ) {
            return chain.filter(exchange)
        }

        // 2) Read Authorization header
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn { "Missing or invalid Authorization header for path=$path" }
            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
            return exchange.response.setComplete()
        }

        val token = authHeader.removePrefix("Bearer ").trim()

        val claims =
            try {
                jwtVerifier.parseToken(token)
            } catch (ex: Exception) {
                logger.warn(ex) { "JWT parsing/validation failed for path=$path" }
                exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                return exchange.response.setComplete()
            }

        // 3) Extract useful info from token
        val userId = claims.subject // we set subject = userId
        val email = claims["email"]?.toString()
        val roles = claims["roles"]?.toString()

        // 4) Mutate request: add headers for downstream services
        val mutatedExchange =
            exchange
                .mutate()
                .request { builder ->
                    builder.header("X-User-Id", userId)
                    if (!email.isNullOrBlank()) builder.header("X-User-Email", email)
                    if (!roles.isNullOrBlank()) builder.header("X-User-Roles", roles)
                }.build()

        return chain.filter(mutatedExchange)
    }
}
