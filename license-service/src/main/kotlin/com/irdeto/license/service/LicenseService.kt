package com.irdeto.license.service

import com.irdeto.license.domain.License
import com.irdeto.license.domain.LicenseRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class LicenseService(
    private val licenseRepository: LicenseRepository
) {

    fun isLicensed(userId: UUID, contentId: String): Boolean {
        return licenseRepository.existsByUserIdAndContentId(userId, contentId)
    }

    fun createLicense(userId: UUID, contentId: String): License {
        val license = License(userId = userId, contentId = contentId)
        return licenseRepository.save(license)
    }

    fun getAllLicenses(): List<License> = licenseRepository.findAll()
}
