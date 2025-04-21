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
    @Value("\${SYSTEM_TOKEN}") private val systemToken: String
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        
        if (header != null) {
            when {
                header.startsWith("Bearer ") -> {
                    val token = header.substring(7)
                    if (jwtTokenProvider.validateToken(token)) {
                        val userId = jwtTokenProvider.getUserIdFromToken(token)
                        val auth = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = auth
                    }
                }

                header.startsWith("System ") -> {
                    val token = header.substring(7)
                    if (token == systemToken) {
                        val auth = UsernamePasswordAuthenticationToken(
                            "system",
                            null,
                            listOf(SimpleGrantedAuthority("ROLE_SYSTEM"))
                        )
                        SecurityContextHolder.getContext().authentication = auth
                    }
                }
            }
        }

        filterChain.doFilter(request, response)
    }
}
