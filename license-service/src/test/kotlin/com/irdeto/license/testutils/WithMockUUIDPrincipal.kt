package com.irdeto.license.testutils

import org.springframework.security.test.context.support.WithSecurityContext
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@WithSecurityContext(factory = WithMockUUIDPrincipalSecurityContextFactory::class)
annotation class WithMockUUIDPrincipal(
    val uuid: String,
    val roles: Array<String> = []
)
