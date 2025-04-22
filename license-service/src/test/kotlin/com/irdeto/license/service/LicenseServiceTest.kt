package com.irdeto.license.service

import com.irdeto.license.domain.License
import com.irdeto.license.domain.LicenseRepository
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

        val result = licenseService.isLicensed(userId, contentId)

        assertTrue(result)
    }

    @Test
    fun `should return false if license does not exist`() {
        val userId = UUID.randomUUID()
        val contentId = "avatar"

        every { licenseRepository.existsByUserIdAndContentId(userId, contentId) } returns false

        val result = licenseService.isLicensed(userId, contentId)

        assertFalse(result)
    }

    @Test
    fun `should create license`() {
        val userId = UUID.randomUUID()
        val contentId = "matrix"
        val license = License(userId, contentId)

        every { licenseRepository.save(any()) } returns license

        val result = licenseService.createLicense(userId, contentId)

        assertEquals(license, result)
        verify(exactly = 1) { licenseRepository.save(any()) }
    }

    @Test
    fun `getAllLicenses should return licenses`() {
        val licenses = listOf(
            License(UUID.randomUUID(), "avatar"),
            License(UUID.randomUUID(), "matrix")
        )

        every { licenseRepository.findAll() } returns licenses

        val result = licenseService.getAllLicenses()

        assertEquals(licenses, result)
    }
}
