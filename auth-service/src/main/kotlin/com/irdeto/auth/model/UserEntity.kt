package com.irdeto.auth.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    val id: UUID,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Date
)
