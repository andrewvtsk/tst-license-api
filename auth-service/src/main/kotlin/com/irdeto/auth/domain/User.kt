package com.irdeto.auth.domain

import java.util.*

data class User(
    val id: UUID,
    val email: String,
    val hashedPassword: String,
    val createdAt: Date
)
