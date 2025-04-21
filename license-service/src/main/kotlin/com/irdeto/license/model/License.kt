package com.irdeto.license.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "licenses")
data class License(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "content_id", nullable = false)
    val contentId: String,

    @Column(name = "issued_at", nullable = false)
    val issuedAt: Date = Date()
)
