package com.irdeto.auth.controller

import com.irdeto.auth.controller.dto.RegisterRequest
import com.irdeto.auth.controller.dto.LoginRequest
import com.irdeto.auth.controller.dto.UserResponse
import com.irdeto.auth.controller.dto.TokenResponse
import com.irdeto.auth.security.JwtTokenProvider
import com.irdeto.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val user = authService.registerUser(request.email, request.password)
        return ResponseEntity.ok(UserResponse.fromDomain(user))
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val user = authService.authenticate(request.email, request.password)
        val token = jwtTokenProvider.generateToken(user)
        return ResponseEntity.ok(TokenResponse(token))
    }
}
