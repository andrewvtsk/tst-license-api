package com.irdeto.license.security

import com.irdeto.license.model.License
import io.jsonwebtoken.JwtException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider

    private val secret = "test-secret-key-that-is-long-enough-1234567890"
    private val expirationMs = 3600000L

    @BeforeEach
    fun setup() {
        jwtTokenProvider = JwtTokenProvider(secret)
    }

    @Test
    fun `should return false for invalid JWT`() {
        val invalidToken = "not.a.valid.jwt"

        assertFalse(jwtTokenProvider.validateToken(invalidToken))
    }

    @Test
    fun `should throw exception for invalid token in getUserIdFromToken`() {
        val invalidToken = "this.is.not.jwt"

        assertThrows(JwtException::class.java) {
            jwtTokenProvider.getUserIdFromToken(invalidToken)
        }
    }
}
