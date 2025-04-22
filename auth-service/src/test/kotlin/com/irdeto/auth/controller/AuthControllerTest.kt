package com.irdeto.auth.controller

import com.irdeto.auth.controller.dto.LoginRequest
import com.irdeto.auth.controller.dto.RegisterRequest
import com.irdeto.auth.domain.User
import com.irdeto.auth.security.JwtTokenProvider
import com.irdeto.auth.service.AuthService
import com.irdeto.auth.config.TestSecurityConfig
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.util.*

@Import(TestSecurityConfig::class)
@WebMvcTest(AuthController::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var authService: AuthService

    @MockkBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    fun `POST register should return 200 with user email`() {
        val request = RegisterRequest("user@example.com", "password")
        val user = User(UUID.randomUUID(), request.email, "hashed", Date())

        every { authService.registerUser(request.email, request.password) } returns user

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.email") { value("user@example.com") }
        }
    }

    @Test
    fun `POST login should return token`() {
        val request = LoginRequest("user@example.com", "password")
        val user = User(UUID.randomUUID(), request.email, "hashed", Date())

        every { authService.authenticate(request.email, request.password) } returns user
        every { jwtTokenProvider.generateToken(user) } returns "mocked-jwt-token"

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { value("mocked-jwt-token") }
        }
    }

    @Test
    fun `POST register should return 400 on blank email`() {
        val request = RegisterRequest("", "password")

        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
