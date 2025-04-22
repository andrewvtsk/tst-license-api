package com.irdeto.license.domain

import java.util.*

data class License(
    val userId: UUID,
    val contentId: String
)
