package com.streamalyze.authservice.web

import com.streamalyze.authservice.domain.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/dev/auth")
class AuthHttpController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @Valid @RequestBody body: AuthRequest,
    ): Mono<AuthResponse> =
        authService
            .register(body.email, body.password)
            .map { token -> AuthResponse(token) }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody body: AuthRequest,
    ): Mono<AuthResponse> =
        authService
            .login(body.email, body.password)
            .map { token -> AuthResponse(token) }
}
