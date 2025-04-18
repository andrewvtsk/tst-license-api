package com.irdeto.auth.service

import com.irdeto.auth.model.User
import com.irdeto.auth.repository.UserRepository
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
            email = email,
            password = encodedPassword
        )
        return userRepository.save(user)
    }

    fun authenticate(email: String, rawPassword: String): User {
        val user = userRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("Invalid email or password") }

        if (!passwordEncoder.matches(rawPassword, user.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }

        return user
    }
}
