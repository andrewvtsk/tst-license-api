package com.irdeto.auth.repository

import com.irdeto.auth.domain.User
import com.irdeto.auth.domain.UserRepository
import com.irdeto.auth.mapper.toDomain
import com.irdeto.auth.mapper.toEntity
import org.springframework.stereotype.Repository

@Repository
class RelationalUserRepository(
    private val jpa: JpaUserRepository
) : UserRepository {

    override fun existsByEmail(email: String): Boolean =
        jpa.existsByEmail(email)

    override fun findByEmail(email: String): User? =
        jpa.findByEmail(email)?.toDomain()

    override fun save(user: User): User =
        jpa.save(user.toEntity()).toDomain()
}
