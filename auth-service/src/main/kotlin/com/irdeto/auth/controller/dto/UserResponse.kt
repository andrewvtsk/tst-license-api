package com.irdeto.auth.controller.dto

import com.irdeto.auth.domain.User
import java.util.*

data class UserResponse(
    val id: UUID,
    val email: String,
    val createdAt: Date
) {
    companion object {
        fun fromDomain(user: User): UserResponse =
            UserResponse(user.id, user.email, user.createdAt)
    }
}
