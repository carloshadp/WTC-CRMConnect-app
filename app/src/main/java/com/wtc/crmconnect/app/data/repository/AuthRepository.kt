package com.wtc.crmconnect.app.data.repository

import com.squareup.moshi.Moshi
import com.wtc.crmconnect.app.data.local.TokenStorage
import com.wtc.crmconnect.app.data.remote.api.AuthApi
import com.wtc.crmconnect.app.data.remote.dto.auth.AuthResponseDto
import com.wtc.crmconnect.app.data.remote.dto.auth.ForgotPasswordRequestDto
import com.wtc.crmconnect.app.data.remote.dto.auth.LoginRequestDto
import com.wtc.crmconnect.app.data.remote.dto.auth.RegisterRequestDto
import com.wtc.crmconnect.app.data.remote.dto.auth.ResetPasswordRequestDto
import com.wtc.crmconnect.app.data.remote.dto.enums.Role
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
    private val moshi: Moshi
) {

    suspend fun login(email: String, password: String): Result<AuthResponseDto> =
        apiCall(moshi) { authApi.login(LoginRequestDto(email, password)) }
            .onSuccess { persistSession(it, email) }

    suspend fun register(email: String, password: String, role: Role): Result<AuthResponseDto> =
        apiCall(moshi) { authApi.register(RegisterRequestDto(email, password, role)) }
            .onSuccess { persistSession(it, email) }

    suspend fun forgotPassword(email: String): Result<Unit> = apiCall(moshi) {
        authApi.forgotPassword(ForgotPasswordRequestDto(email)).requireSuccess()
    }

    suspend fun resetPassword(token: String, newPassword: String): Result<Unit> = apiCall(moshi) {
        authApi.resetPassword(ResetPasswordRequestDto(token, newPassword)).requireSuccess()
    }

    fun logout() {
        tokenStorage.clear()
    }

    fun isAuthenticated(): Boolean = tokenStorage.isAuthenticated()

    fun currentUserId(): String? = tokenStorage.getUserId()

    fun currentEmail(): String? = tokenStorage.getUserEmail()

    fun currentRole(): Role? = tokenStorage.getUserRole()?.let {
        runCatching { Role.valueOf(it) }.getOrNull()
    }

    private fun persistSession(auth: AuthResponseDto, email: String? = null) {
        tokenStorage.saveTokens(auth.accessToken, auth.refreshToken)
        tokenStorage.saveUserId(auth.userId)
        tokenStorage.saveUserRole(auth.role.name)
        email?.let { tokenStorage.saveUserEmail(it) }
    }
}
