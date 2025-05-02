package com.irdeto.auth.security

import com.irdeto.auth.domain.User
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Claims
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class JwtTokenProviderTest {
    private lateinit var jwtTokenProvider: JwtTokenProvider

    private val secret = "thisisaverysecuresecretkeyforjwtmustbe32bytes!"
    private val expirationMs = 3600000L

    @BeforeEach
    fun setup() {
        jwtTokenProvider = JwtTokenProvider(secret, expirationMs)
    }

    @Test
    fun `generateToken should create valid JWT with correct claims`() {
        val userId = UUID.randomUUID()
        val email = "test@example.com"
        val user = User(userId, email, "hashed-pass", Date())
        val token = jwtTokenProvider.generateToken(user)
        val result = jwtTokenProvider.validateToken(token)
        assertTrue(result is TokenValidationResult.Valid, "Expected Valid but got $result")

        val claims: Claims = (result as TokenValidationResult.Valid).claims
        assertEquals(userId.toString(), claims.subject)

        val extractedId = jwtTokenProvider.getUserIdFromToken(token)
        assertEquals(userId, extractedId)
    }

    @Test
    fun `validateToken should return Malformed for malformed token`() {
        val invalidToken = "not.a.valid.token"
        val result = jwtTokenProvider.validateToken(invalidToken)
        assertTrue(result is TokenValidationResult.Malformed, "Expected Malformed but got $result")
    
        val reason = (result as TokenValidationResult.Malformed).reason
        assertTrue(reason.isNotBlank(), "Expected non-empty reason but was '$reason'")
    }

    @Test
    fun `getUserIdFromToken should throw for invalid token`() {
        val invalidToken = "fake.jwt.token"
        assertThrows(JwtException::class.java) {
            jwtTokenProvider.getUserIdFromToken(invalidToken)
        }
    }
}
