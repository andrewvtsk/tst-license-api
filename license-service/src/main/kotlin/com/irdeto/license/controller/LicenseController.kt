package com.irdeto.license.controller

import com.irdeto.license.service.LicenseService
import com.irdeto.license.controller.dto.CreateLicenseRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import java.util.*

@RestController
@RequestMapping("/license")
class LicenseController(
    private val licenseService: LicenseService
) {

    @GetMapping
    fun checkLicense(
        @RequestParam contentId: String,
        authentication: Authentication
    ): ResponseEntity<Boolean> {
        val userId = authentication.principal as UUID
        val entitled = licenseService.isLicensed(userId, contentId)
        return ResponseEntity.ok(entitled)
    }

    @PostMapping
    fun addLicense(
        @Valid @RequestBody request: CreateLicenseRequest,
        authentication: Authentication
    ): ResponseEntity<String> {
        if (authentication.name != "system") {
            throw AccessDeniedException("Only system is allowed to create licenses")
        }

        licenseService.createLicense(request.userId, request.contentId)
        return ResponseEntity.status(201).body("License created")
    }
}
