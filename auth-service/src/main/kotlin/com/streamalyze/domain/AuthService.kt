package com.streamalyze.authservice.domain

import com.streamalyze.authservice.security.JwtService
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Service
class AuthService(
    private val userRepo: UserAccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {
    fun register(
        email: String,
        rawPassword: String,
    ): Mono<String> {
        logger.info { "Register new user email=$email" }

        return userRepo
            .findByEmail(email)
            .flatMap<UserAccount> {
                Mono.error(IllegalArgumentException("Email already in use"))
            }.switchIfEmpty(
                Mono.defer {
                    val user =
                        UserAccount(
                            email = email,
                            passwordHash = passwordEncoder.encode(rawPassword) as String,
                            roles = "ROLE_USER",
                        )
                    userRepo.save(user)
                },
            ).map { saved ->
                jwtService.generateToken(
                    userId = saved.id!!,
                    email = saved.email,
                    roles = saved.roles,
                )
            }
    }

    fun login(
        email: String,
        rawPassword: String,
    ): Mono<String> {
        logger.info { "Login attempt email=$email" }

        return userRepo
            .findByEmail(email)
            .switchIfEmpty(Mono.error(IllegalArgumentException("Bad credentials")))
            .flatMap { user ->
                if (passwordEncoder.matches(rawPassword, user.passwordHash)) {
                    val token =
                        jwtService.generateToken(
                            userId = user.id!!,
                            email = user.email,
                            roles = user.roles,
                        )
                    Mono.just(token)
                } else {
                    Mono.error(IllegalArgumentException("Bad credentials"))
                }
            }
    }
}
