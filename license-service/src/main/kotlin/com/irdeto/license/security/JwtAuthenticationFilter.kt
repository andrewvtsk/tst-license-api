package com.irdeto.license.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${SECURITY_SYSTEM_TOKEN}") private val systemToken: String
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.getHeader("Authorization")?.let { header ->
            when {
                header.startsWith("Bearer ")    -> handleBearer(header.removePrefix("Bearer ").trim(), request)
                header.startsWith("System ")    -> handleSystem(header.removePrefix("System ").trim())
                else                            -> logger.debug("No recognizable Authorization header")
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun handleBearer(token: String, request: HttpServletRequest) {
        when (val result = jwtTokenProvider.validateToken(token)) {
            is TokenValidationResult.Valid              -> setAuthentication(UUID.fromString(result.claims.subject), request)
            TokenValidationResult.Expired               -> logger.warn("JWT expired")
            TokenValidationResult.NotYetValid           -> logger.warn("JWT not yet valid")
            is TokenValidationResult.InvalidSignature   -> logger.warn("Invalid JWT signature: ${result.reason}")
            is TokenValidationResult.Malformed          -> logger.warn("Malformed JWT: ${result.reason}")
        }
    }

    private fun handleSystem(sysToken: String) {
        if (sysToken != systemToken) {
            logger.debug("Invalid system token")
            return
        }

        val auth = UsernamePasswordAuthenticationToken(
            "system", null, listOf(SimpleGrantedAuthority("ROLE_SYSTEM"))
        )

        SecurityContextHolder.getContext().authentication = auth
    }

    private fun setAuthentication(principal: UUID, request: HttpServletRequest) {
        val auth = UsernamePasswordAuthenticationToken(principal, null, emptyList())

        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = auth
    }

}
