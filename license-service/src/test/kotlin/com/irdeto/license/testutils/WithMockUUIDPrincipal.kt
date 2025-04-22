package com.irdeto.license.testutils

import org.springframework.security.test.context.support.WithSecurityContext
import java.lang.annotation.Inherited

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@WithSecurityContext(factory = WithMockUUIDPrincipalSecurityContextFactory::class)
annotation class WithMockUUIDPrincipal(
    val uuid: String = "123e4567-e89b-12d3-a456-426614174000",
    val roles: Array<String> = ["USER"]
)
