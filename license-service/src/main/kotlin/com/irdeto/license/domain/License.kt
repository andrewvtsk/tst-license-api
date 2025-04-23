package com.irdeto.license.domain

import java.util.*

data class License(
    val id: UUID,
    val userId: UUID,
    val contentId: String
)
