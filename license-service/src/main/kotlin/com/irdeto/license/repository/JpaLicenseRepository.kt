package com.irdeto.license.repository

import com.irdeto.license.model.LicenseEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface JpaLicenseRepository : JpaRepository<LicenseEntity, Long> {
    fun existsByUserIdAndContentId(userId: UUID, contentId: String): Boolean
}
