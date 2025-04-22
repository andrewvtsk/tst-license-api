package com.irdeto.license.repository

import com.irdeto.license.domain.License
import com.irdeto.license.domain.LicenseRepository
import com.irdeto.license.mapper.toDomain
import com.irdeto.license.mapper.toEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RelationalLicenseRepository(
    private val jpa: JpaLicenseRepository
) : LicenseRepository {

    override fun existsByUserIdAndContentId(userId: UUID, contentId: String): Boolean {
        return jpa.existsByUserIdAndContentId(userId, contentId)
    }

    override fun save(license: License): License {
        val entity = jpa.save(license.toEntity())
        return entity.toDomain()
    }

    override fun findAll(): List<License> {
        return jpa.findAll().map { it.toDomain() }
    }
}
