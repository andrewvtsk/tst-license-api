package com.irdeto.license.domain

import java.util.*

interface LicenseRepository {
    fun existsByUserIdAndContentId(userId: UUID, contentId: String): Boolean
    fun save(license: License): License
    fun findAll(): List<License>
}
