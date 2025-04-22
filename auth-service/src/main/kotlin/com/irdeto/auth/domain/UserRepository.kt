package com.irdeto.auth.domain

import java.util.*

interface UserRepository {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?
    fun save(user: User): User
}
