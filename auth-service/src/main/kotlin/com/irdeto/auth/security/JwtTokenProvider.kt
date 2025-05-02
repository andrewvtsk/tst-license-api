package com.irdeto.auth.security

import com.irdeto.auth.domain.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG.HS256
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
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
        val claims = requireClaims(token)
        return UUID.fromString(claims.subject)
    }

    fun validateToken(token: String): TokenValidationResult {
        val claims = tryParseClaims(token)
            ?: return TokenValidationResult.Malformed("Cannot parse token")
        
        if (isExpired(claims))    return TokenValidationResult.Expired
        if (isNotYetValid(claims)) return TokenValidationResult.NotYetValid

        return TokenValidationResult.Valid(claims)
    }

    private fun tryParseClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (ex: SignatureException) {
            null
        } catch (ex: JwtException) {
            null
        }
    }

    private fun requireClaims(token: String): Claims =
        tryParseClaims(token)
            ?: throw JwtException("Invalid or malformed JWT token")

    private fun isExpired(claims: Claims): Boolean =
        claims.expiration.before(Date())
    
    private fun isNotYetValid(claims: Claims): Boolean =
        claims.notBefore?.after(Date()) ?: false
}
