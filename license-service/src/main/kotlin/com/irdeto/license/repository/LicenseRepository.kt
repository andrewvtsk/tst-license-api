package com.irdeto.license.repository

import com.irdeto.license.model.License
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LicenseRepository : JpaRepository<License, UUID> {
    fun existsByUserIdAndContentId(userId: UUID, contentId: String): Boolean
}
