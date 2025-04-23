package com.irdeto.license.mapper

import com.irdeto.license.domain.License
import com.irdeto.license.model.LicenseEntity

fun License.toEntity(): LicenseEntity =
    LicenseEntity(id = id, userId = userId, contentId = contentId)

fun LicenseEntity.toDomain(): License =
    License(id = id, userId = userId, contentId = contentId)
