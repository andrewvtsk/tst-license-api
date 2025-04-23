package com.irdeto.license.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "licenses", uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "content_id"])])
data class LicenseEntity(
    @Id
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "content_id", nullable = false)
    val contentId: String
)
