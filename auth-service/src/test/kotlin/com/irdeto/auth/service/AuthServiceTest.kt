package com.irdeto.auth.service

import com.irdeto.auth.model.User
import com.irdeto.auth.repository.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        passwordEncoder = mockk()
        authService = AuthService(userRepository, passwordEncoder)
    }

    @Test
    fun `should register new user successfully`() {
        val email = "test@example.com"
        val rawPassword = "secret"
        val encodedPassword = "hashed-secret"

        every { userRepository.existsByEmail(email) } returns false
        every { passwordEncoder.encode(rawPassword) } returns encodedPassword
        every { userRepository.save(any()) } answers { firstArg() }

        val result = authService.registerUser(email, rawPassword)

        assertEquals(email, result.email)
        assertEquals(encodedPassword, result.password)
    }

    @Test
    fun `should throw exception if email already exists`() {
        every { userRepository.existsByEmail("duplicate@example.com") } returns true

        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.registerUser("duplicate@example.com", "secret")
        }

        assertEquals("User with email duplicate@example.com already exists", exception.message)
    }

    @Test
    fun `should authenticate user with valid credentials`() {
        val email = "user@example.com"
        val rawPassword = "secret"
        val hashedPassword = "hashed-secret"
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            password = hashedPassword
        )

        every { userRepository.findByEmail(email) } returns Optional.of(user)
        every { passwordEncoder.matches(rawPassword, hashedPassword) } returns true

        val result = authService.authenticate(email, rawPassword)

        assertEquals(user, result)
    }

    @Test
    fun `should fail authentication with wrong password`() {
        val email = "user@example.com"
        val hashedPassword = "hashed-secret"
        val user = User(
            id = UUID.randomUUID(),
            email = email,
            password = hashedPassword
        )

        every { userRepository.findByEmail(email) } returns Optional.of(user)
        every { passwordEncoder.matches(any(), any()) } returns false

        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.authenticate(email, "wrong-password")
        }

        assertEquals("Invalid email or password", exception.message)
    }

    @Test
    fun `should fail authentication if email not found`() {
        every { userRepository.findByEmail("missing@example.com") } returns Optional.empty()

        val exception = assertThrows(IllegalArgumentException::class.java) {
            authService.authenticate("missing@example.com", "any")
        }

        assertEquals("Invalid email or password", exception.message)
    }
}
