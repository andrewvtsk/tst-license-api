package com.irdeto.license.service

import com.irdeto.license.model.License
import com.irdeto.license.repository.LicenseRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class LicenseService(
    private val licenseRepository: LicenseRepository
) {

    fun isUserLicensed(userId: UUID, contentId: String): Boolean {
        return licenseRepository.existsByUserIdAndContentId(userId, contentId)
    }

    fun createLicense(userId: UUID, contentId: String): Boolean {
        if (licenseRepository.existsByUserIdAndContentId(userId, contentId)) {
            return false
        }
    
        val license = License(
            userId = userId,
            contentId = contentId
        )
    
        licenseRepository.save(license)
        return true
    }
    
}
