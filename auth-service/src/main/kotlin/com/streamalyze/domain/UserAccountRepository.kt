package com.streamalyze.authservice.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserAccountRepository : ReactiveCrudRepository<UserAccount, Long> {
    fun findByEmail(email: String): Mono<UserAccount>
}
