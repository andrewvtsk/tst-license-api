package com.irdeto.license.mapper

import com.irdeto.license.domain.License
import com.irdeto.license.model.LicenseEntity

fun License.toEntity(): LicenseEntity =
    LicenseEntity(userId = userId, contentId = contentId)

fun LicenseEntity.toDomain(): License =
    License(userId = userId, contentId = contentId)
