package com.irdeto.auth.security

import com.irdeto.auth.domain.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG.HS256
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${SECURITY_JWT_SECRET}") private val jwtSecret: String,
    @Value("\${security.jwt.expiration}") private val jwtExpirationInMs: Long
) {

    private val key: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun generateToken(user: User): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationInMs)

        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key, HS256)
            .compact()
    }

    fun getUserIdFromToken(token: String): UUID {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        return UUID.fromString(claims.subject)
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
            val now = Date()

            if (claims.expiration.before(now)) {
                return false
            }

            if (claims.issuedAt.after(now)) {
                return false
            }

            true
        } catch (ex: Exception) {
            false
        }
    }
}
