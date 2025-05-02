package com.irdeto.auth.security

import io.jsonwebtoken.Claims

sealed class TokenValidationResult {
    data class Valid(val claims: Claims) : TokenValidationResult()
    object Expired : TokenValidationResult()
    object NotYetValid : TokenValidationResult()
    data class InvalidSignature(val reason: String) : TokenValidationResult()
    data class Malformed(val reason: String) : TokenValidationResult()
}
