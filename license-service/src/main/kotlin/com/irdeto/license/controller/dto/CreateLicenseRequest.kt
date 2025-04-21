package com.irdeto.license.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class CreateLicenseRequest(
    @field:NotNull
    val userId: UUID,

    @field:NotBlank
    val contentId: String
)
