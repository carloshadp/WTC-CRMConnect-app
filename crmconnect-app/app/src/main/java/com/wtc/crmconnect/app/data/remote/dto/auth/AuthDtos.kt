package com.wtc.crmconnect.app.data.remote.dto.auth

import com.wtc.crmconnect.app.data.remote.dto.enums.Role

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val role: Role,
    val name: String? = null,
    val phone: String? = null
)

data class RefreshRequestDto(
    val refreshToken: String
)

data class ForgotPasswordRequestDto(
    val email: String
)

data class ResetPasswordRequestDto(
    val token: String,
    val newPassword: String
)

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val role: Role,
    val userId: String
)
