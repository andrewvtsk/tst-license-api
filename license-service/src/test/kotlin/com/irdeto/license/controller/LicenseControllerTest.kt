package com.irdeto.license.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.irdeto.license.config.TestSecurityConfig
import com.irdeto.license.controller.dto.CreateLicenseRequest
import com.irdeto.license.domain.License
import com.irdeto.license.exception.GlobalExceptionHandler
import com.irdeto.license.security.JwtTokenProvider
import com.irdeto.license.security.TokenValidationResult
import com.irdeto.license.service.LicenseService
import com.irdeto.license.testutils.WithMockUUIDPrincipal
import com.ninjasquad.springmockk.MockkBean
import io.jsonwebtoken.Claims
import io.mockk.mockk
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.*

@WebMvcTest(LicenseController::class)
@Import(TestSecurityConfig::class, GlobalExceptionHandler::class)
class LicenseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var licenseService: LicenseService

    @MockkBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @WithMockUUIDPrincipal(uuid = "123e4567-e89b-12d3-a456-426614174000")
    @Test
    fun `GET license should return true`() {
        val contentId = "avatar"
        val userId = SecurityContextHolder.getContext().authentication?.principal as UUID

        val mockClaims = mockk<Claims> {
            every { subject } returns userId.toString()
        }
    
        every { jwtTokenProvider.validateToken(any()) }
            .returns(TokenValidationResult.Valid(mockClaims))

        every { jwtTokenProvider.getUserIdFromToken(any()) }
            .returns(userId)
    
        every { licenseService.isLicensed(userId, contentId) } returns true
    
        mockMvc.get("/license?contentId=$contentId") {
            header("Authorization", "Bearer any-token")
        }.andExpect {
            status { isOk() }
            content { string("true") }
        }
    }

    @WithMockUUIDPrincipal(uuid = "system", roles = ["SYSTEM"])
    @Test
    fun `POST license should return 201 Created for system token`() {
        val systemToken = "super-secret-token"
        val request = CreateLicenseRequest(UUID.randomUUID(), "avatar")
    
        every { licenseService.createLicense(request.userId, request.contentId) }
            .returns(License(UUID.randomUUID(), request.userId, request.contentId))
    
        mockMvc.post("/license") {
            header("Authorization", "System $systemToken")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }
    }

    @WithMockUUIDPrincipal(uuid = "123e4567-e89b-12d3-a456-426614174000", roles = ["USER"])
    @Test
    fun `POST license should return 403 for non-system user`(){
        val userId = SecurityContextHolder.getContext().authentication?.principal as UUID
        val mockClaims = mockk<Claims> { every { subject } returns userId.toString() }
    
        every { jwtTokenProvider.validateToken(any()) }
            .returns(TokenValidationResult.Valid(mockClaims))
    
        mockMvc.post("/license") {
            header("Authorization", "Bearer any-token")
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                CreateLicenseRequest(userId, "avatar")
            )
        }.andExpect {
            status { isForbidden() }
        }
    }
}
