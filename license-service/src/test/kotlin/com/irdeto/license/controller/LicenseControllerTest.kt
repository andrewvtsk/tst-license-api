package com.irdeto.license.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.irdeto.license.config.TestSecurityConfig
import com.irdeto.license.controller.dto.CreateLicenseRequest
import com.irdeto.license.service.LicenseService
import com.irdeto.license.testutils.WithMockUUIDPrincipal
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.*

@Import(TestSecurityConfig::class)
@WebMvcTest(LicenseController::class)
class LicenseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var licenseService: LicenseService

    @WithMockUUIDPrincipal(uuid = "123e4567-e89b-12d3-a456-426614174000")
    @Test
    fun `GET license should return true`() {
        val contentId = "avatar"
        val userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")

        every { licenseService.isUserLicensed(userId, contentId) } returns true

        mockMvc.get("/license?contentId=$contentId") {
            header("Authorization", "Bearer mocked-jwt")
        }.andExpect {
            status { isOk() }
            content { string("true") }
        }
    }

    @WithMockUUIDPrincipal(uuid = "system", roles = ["SYSTEM"])
    @Test
    fun `POST license should return 201 Created for system token`() {
        val request = CreateLicenseRequest(UUID.randomUUID(), "avatar")

        every { licenseService.createLicense(any(), any()) } returns true

        mockMvc.post("/license") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "System super-secret-token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }
    }

    @WithMockUUIDPrincipal(uuid = "system", roles = ["SYSTEM"])
    @Test
    fun `POST license should return 200 if license already exists`() {
        val request = CreateLicenseRequest(UUID.randomUUID(), "avatar")

        every { licenseService.createLicense(any(), any()) } returns false

        mockMvc.post("/license") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "System super-secret-token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @WithMockUUIDPrincipal(uuid = "123e4567-e89b-12d3-a456-426614174000", roles = ["USER"])
    @Test
    fun `POST license should return 403 for non-system user`() {
        val request = CreateLicenseRequest(UUID.randomUUID(), "avatar")

        mockMvc.post("/license") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer not-a-system-token")
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isForbidden() }
        }
    }
}
