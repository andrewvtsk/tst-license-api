package com.irdeto.license.service

import com.irdeto.license.model.License
import com.irdeto.license.repository.LicenseRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class LicenseServiceTest {

    private lateinit var licenseRepository: LicenseRepository
    private lateinit var licenseService: LicenseService

    @BeforeEach
    fun setup() {
        licenseRepository = mockk()
        licenseService = LicenseService(licenseRepository)
    }

    @Test
    fun `should return true if license exists`() {
        val userId = UUID.randomUUID()
        val contentId = "avatar"

        every { licenseRepository.existsByUserIdAndContentId(userId, contentId) } returns true

        val result = licenseService.isUserLicensed(userId, contentId)

        assertTrue(result)
    }

    @Test
    fun `should return false if license does not exist`() {
        val userId = UUID.randomUUID()
        val contentId = "avatar"

        every { licenseRepository.existsByUserIdAndContentId(userId, contentId) } returns false

        val result = licenseService.isUserLicensed(userId, contentId)

        assertFalse(result)
    }

    @Test
    fun `should create license when not exists`() {
        val userId = UUID.randomUUID()
        val contentId = "matrix"

        every { licenseRepository.existsByUserIdAndContentId(userId, contentId) } returns false
        every { licenseRepository.save(any()) } answers { firstArg() }

        val result = licenseService.createLicense(userId, contentId)

        assertTrue(result)
        verify(exactly = 1) { licenseRepository.save(any()) }
    }

    @Test
    fun `should not create license when already exists`() {
        val userId = UUID.randomUUID()
        val contentId = "matrix"

        every { licenseRepository.existsByUserIdAndContentId(userId, contentId) } returns true

        val result = licenseService.createLicense(userId, contentId)

        assertFalse(result)
        verify(exactly = 0) { licenseRepository.save(any()) }
    }
}
