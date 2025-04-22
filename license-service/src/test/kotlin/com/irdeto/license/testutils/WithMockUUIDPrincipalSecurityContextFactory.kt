package com.irdeto.license.testutils

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.util.*

class WithMockUUIDPrincipalSecurityContextFactory :
    WithSecurityContextFactory<WithMockUUIDPrincipal> {

    override fun createSecurityContext(annotation: WithMockUUIDPrincipal): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        val userId = try {
            UUID.fromString(annotation.uuid)
        } catch (_: IllegalArgumentException) {
            annotation.uuid
        }

        val authorities = annotation.roles.map { SimpleGrantedAuthority("ROLE_$it") }

        val auth = UsernamePasswordAuthenticationToken(userId, null, authorities)
        context.authentication = auth
        return context
    }
}
