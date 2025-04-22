package com.irdeto.auth.repository

import com.irdeto.auth.model.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JpaUserRepository : JpaRepository<UserEntity, UUID> {
    fun findByEmail(email: String): UserEntity?
    fun existsByEmail(email: String): Boolean
}
