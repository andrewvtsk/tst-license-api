package com.irdeto.auth.security

import com.irdeto.auth.model.User
import io.jsonwebtoken.JwtException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider

    private val secret = "thisisaverysecuresecretkeyforjwtmustbe32bytes!"
    private val expiration = 3600000L

    @BeforeEach
    fun setup() {
        jwtTokenProvider = JwtTokenProvider(secret, expiration)
    }

    @Test
    fun `generateToken should create valid JWT with correct claims`() {
        val userId = UUID.randomUUID()
        val email = "test@example.com"
        val user = User(userId, email, "hashed-pass")

        val token = jwtTokenProvider.generateToken(user)

        assertTrue(jwtTokenProvider.validateToken(token))
        val extractedId = jwtTokenProvider.getUserIdFromToken(token)
        assertEquals(userId, extractedId)
    }

    @Test
    fun `validateToken should return false for malformed token`() {
        val invalidToken = "not.a.valid.token"
        assertFalse(jwtTokenProvider.validateToken(invalidToken))
    }

    @Test
    fun `getUserIdFromToken should throw for invalid token`() {
        val invalidToken = "fake.jwt.token"
        assertThrows(JwtException::class.java) {
            jwtTokenProvider.getUserIdFromToken(invalidToken)
        }
    }
}
