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

        // Когда repository.save получит любой License, вернёт его обратно
        every { licenseRepository.save(any()) } answers { firstArg() as License }

        val result = licenseService.createLicense(userId, contentId)

        // При создании сервисом id не null
        assertNotNull(result.id)
        assertEquals(userId, result.userId)
        assertEquals(contentId, result.contentId)

        verify(exactly = 1) { licenseRepository.save(any()) }
    }

    @Test
    fun `getAllLicenses should return licenses`() {
        val l1 = License(UUID.randomUUID(), UUID.randomUUID(), "avatar")
        val l2 = License(UUID.randomUUID(), UUID.randomUUID(), "matrix")
        val licenses = listOf(l1, l2)

        every { licenseRepository.findAll() } returns licenses

        val result = licenseService.getAllLicenses()

        assertEquals(2, result.size)
        assertEquals(licenses, result)
    }
}
