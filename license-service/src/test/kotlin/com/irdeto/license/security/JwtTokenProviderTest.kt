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
        val token = JwtFactory.createToken(userId, secret, expirationMs)
        val result = jwtTokenProvider.validateToken(token)
        assertTrue(result is TokenValidationResult.Valid, "Expected Valid but was $result")

        val claims = (result as TokenValidationResult.Valid).claims
        val extractedFromClaims = UUID.fromString(claims.subject)
        assertEquals(userId, extractedFromClaims)

        val extractedViaHelper = jwtTokenProvider.getUserIdFromToken(token)
        assertEquals(userId, extractedViaHelper)
    }
    

    @Test
    fun `should return Malformed for malformed token`() {
        val token = "invalid.token.value"
        val result = jwtTokenProvider.validateToken(token)
        assertTrue(result is TokenValidationResult.Malformed, "Expected Malformed but got $result")

        val reason = (result as TokenValidationResult.Malformed).reason
        assertTrue(
            reason.isNotBlank(),
            "Expected non-empty reason, but was '$reason'"
        )
    }



    @Test
    fun `should throw on getUserIdFromToken for invalid token`() {
        assertThrows(JwtException::class.java) {
            jwtTokenProvider.getUserIdFromToken("this.is.not.jwt")
        }
    }
}
