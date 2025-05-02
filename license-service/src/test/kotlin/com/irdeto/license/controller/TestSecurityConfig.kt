package com.irdeto.license.config

import com.irdeto.license.security.JwtAuthenticationFilter
import com.irdeto.license.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class TestSecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    @Value("\${SECURITY_SYSTEM_TOKEN}") private val systemToken: String
) {
    @Bean
    fun testFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .addFilterBefore(
            JwtAuthenticationFilter(jwtTokenProvider, systemToken),
            UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
