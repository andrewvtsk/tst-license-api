package com.irdeto.license.security

import com.irdeto.license.testutils.JwtFactory
import io.jsonwebtoken.JwtException
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
        jwtTokenProvider = JwtTokenProvider(secret)
    }

    @Test
    fun `should generate and validate token`() {
        val userId = UUID.randomUUID()

        // вручную соберем валидный JWT
        val token = JwtFactory.createToken(userId, secret, expirationMs)

        val isValid = jwtTokenProvider.validateToken(token)
        val extractedUserId = jwtTokenProvider.getUserIdFromToken(token)

        assertTrue(isValid)
        assertEquals(userId, extractedUserId)
    }

    @Test
    fun `should return false for malformed token`() {
        val result = jwtTokenProvider.validateToken("invalid.token.value")
        assertFalse(result)
    }

    @Test
    fun `should throw on getUserIdFromToken for invalid token`() {
        assertThrows(JwtException::class.java) {
            jwtTokenProvider.getUserIdFromToken("this.is.not.jwt")
        }
    }
}
