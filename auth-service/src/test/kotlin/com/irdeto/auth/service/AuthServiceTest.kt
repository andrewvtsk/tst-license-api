package com.irdeto.auth.service

import com.irdeto.auth.domain.User
import com.irdeto.auth.domain.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
    fun `registerUser should save and return new user`() {
        val email = "test@example.com"
        val rawPassword = "123456"
        val encodedPassword = "encoded"

        every { userRepository.existsByEmail(email) } returns false
        every { passwordEncoder.encode(rawPassword) } returns encodedPassword
        every { userRepository.save(any()) } answers { firstArg() }

        val user = authService.registerUser(email, rawPassword)

        assertEquals(email, user.email)
        assertEquals(encodedPassword, user.hashedPassword)
        assertNotNull(user.id)
        verify { userRepository.save(any()) }
    }

    @Test
    fun `registerUser should throw if email already exists`() {
        every { userRepository.existsByEmail("used@example.com") } returns true

        val ex = assertThrows(IllegalArgumentException::class.java) {
            authService.registerUser("used@example.com", "pass")
        }

        assertEquals("User with email used@example.com already exists", ex.message)
    }

    @Test
    fun `authenticate should return user when credentials are valid`() {
        val email = "auth@example.com"
        val rawPassword = "secret"
        val hashedPassword = "hashed"
        val user = User(UUID.randomUUID(), email, hashedPassword, Date())

        every { userRepository.findByEmail(email) } returns user
        every { passwordEncoder.matches(rawPassword, hashedPassword) } returns true

        val result = authService.authenticate(email, rawPassword)

        assertEquals(user, result)
    }

    @Test
    fun `authenticate should throw on invalid password`() {
        val user = User(UUID.randomUUID(), "x@x.com", "hashed", Date())
        every { userRepository.findByEmail("x@x.com") } returns user
        every { passwordEncoder.matches(any(), any()) } returns false

        val ex = assertThrows(IllegalArgumentException::class.java) {
            authService.authenticate("x@x.com", "wrongpass")
        }

        assertEquals("Invalid email or password", ex.message)
    }

    @Test
    fun `authenticate should throw if user not found`() {
        every { userRepository.findByEmail("nouser@example.com") } returns null

        val ex = assertThrows(IllegalArgumentException::class.java) {
            authService.authenticate("nouser@example.com", "pass")
        }

        assertEquals("Invalid email or password", ex.message)
    }
}
