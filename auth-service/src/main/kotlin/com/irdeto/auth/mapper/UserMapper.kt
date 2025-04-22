package com.irdeto.auth.mapper

import com.irdeto.auth.domain.User
import com.irdeto.auth.model.UserEntity

fun User.toEntity(): UserEntity =
    UserEntity(id, email, hashedPassword, createdAt)

fun UserEntity.toDomain(): User =
    User(id, email, password, createdAt)
