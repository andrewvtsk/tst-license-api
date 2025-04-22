package com.irdeto.auth.service

import com.irdeto.auth.domain.User
import com.irdeto.auth.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun registerUser(email: String, rawPassword: String): User {
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("User with email $email already exists")
        }

        val encodedPassword = passwordEncoder.encode(rawPassword)
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            hashedPassword = encodedPassword,
            createdAt = Date()
        )

        return userRepository.save(user)
    }

    fun authenticate(email: String, rawPassword: String): User {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Invalid email or password")

        if (!passwordEncoder.matches(rawPassword, user.hashedPassword)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        return user
    }
}
