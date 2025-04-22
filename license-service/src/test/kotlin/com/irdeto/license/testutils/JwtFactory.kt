package com.irdeto.license.testutils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG.HS256
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

object JwtFactory {
    fun createToken(userId: UUID, secret: String, expirationMs: Long): String {
        val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key, HS256)
            .compact()
    }
}
