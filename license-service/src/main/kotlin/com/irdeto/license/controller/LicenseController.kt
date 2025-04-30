package com.irdeto.license.controller

import com.irdeto.license.service.LicenseService
import com.irdeto.license.controller.dto.CreateLicenseRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid
import java.util.*

@RestController
@RequestMapping("/license")
class LicenseController(
    private val licenseService: LicenseService
) {

    @GetMapping
    fun checkLicense(@RequestParam contentId: String): ResponseEntity<Boolean> {
        val auth = SecurityContextHolder.getContext().authentication 
            ?: throw AuthenticationCredentialsNotFoundException("Not authenticated")
        val userId = auth.principal as UUID

        return ResponseEntity.ok(licenseService.isLicensed(userId, contentId))
    }

    @PostMapping
    fun addLicense(@Valid @RequestBody request: CreateLicenseRequest): ResponseEntity<String> {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw AuthenticationCredentialsNotFoundException("Not authenticated")

        if (auth.name != "system") throw AccessDeniedException("Only system is allowed")
        licenseService.createLicense(request.userId, request.contentId)

        return ResponseEntity.status(201).body("License created")
    }
}
