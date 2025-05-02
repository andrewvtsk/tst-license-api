package com.irdeto.license.security

import com.irdeto.license.security.TokenValidationResult
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${SECURITY_JWT_SECRET}") jwtSecret: String
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    fun getUserIdFromToken(token: String): UUID {
        val claims = requireClaims(token)
        return UUID.fromString(claims.subject)
    }

    fun validateToken(token: String): TokenValidationResult {
        val claims = tryParseClaims(token)
            ?: return TokenValidationResult.Malformed("Cannot parse or invalid signature")
        val now = Date()

        return when {
            claims.expiration.before(now)           -> TokenValidationResult.Expired
            claims.notBefore?.after(now) == true    -> TokenValidationResult.NotYetValid
            else                                    -> TokenValidationResult.Valid(claims)
        }
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
