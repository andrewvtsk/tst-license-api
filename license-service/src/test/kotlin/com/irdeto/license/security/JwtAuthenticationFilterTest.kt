package com.irdeto.license.security

import io.jsonwebtoken.Claims
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JwtAuthenticationFilterTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var filter: JwtAuthenticationFilter

    private val systemToken = "super-secret-system-token"
    private val userId = UUID.randomUUID()

    @BeforeEach
    fun setup() {
        jwtTokenProvider = mockk(relaxed = true)
        filter = JwtAuthenticationFilter(jwtTokenProvider, systemToken)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should authenticate valid JWT token`() {
        val token = "valid.jwt.token"
        val mockClaims = mockk<Claims> {
            every { subject } returns userId.toString()
        }
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "Bearer $token")
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        every { jwtTokenProvider.validateToken(token) }.returns(TokenValidationResult.Valid(mockClaims))
        every { jwtTokenProvider.getUserIdFromToken(token) } returns userId

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertEquals(userId, auth.principal)
        assertTrue(auth.isAuthenticated)
        assertTrue(auth.authorities.isEmpty())
    }

    @Test
    fun `should authenticate valid system token`() {
        val request = MockHttpServletRequest()
        request.addHeader("Authorization", "System $systemToken")
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertEquals("system", auth.principal)
        assertTrue(auth.authorities.any { it.authority == "ROLE_SYSTEM" })
        assertTrue(auth.isAuthenticated)
    }

    @Test
    fun `should skip authentication if token is invalid`() {
        val token = "invalid.jwt"
        val request = MockHttpServletRequest().apply {
            addHeader("Authorization", "Bearer $token")
        }
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)
        every { jwtTokenProvider.validateToken(token) }.returns(TokenValidationResult.Malformed("cannot parse token"))

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)
    }


    @Test
    fun `should skip authentication if no Authorization header`() {
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)
    }
}
