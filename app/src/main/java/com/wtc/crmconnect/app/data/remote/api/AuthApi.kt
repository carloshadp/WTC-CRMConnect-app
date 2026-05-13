package com.wtc.crmconnect.app.data.remote.api

import com.wtc.crmconnect.app.data.remote.dto.auth.AuthResponseDto
import com.wtc.crmconnect.app.data.remote.dto.auth.ForgotPasswordRequestDto
import com.wtc.crmconnect.app.data.remote.dto.auth.LoginRequestDto
import com.wtc.crmconnect.app.data.remote.dto.auth.RefreshRequestDto
import com.wtc.crmconnect.app.data.remote.dto.auth.RegisterRequestDto
import com.wtc.crmconnect.app.data.remote.dto.auth.ResetPasswordRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshRequestDto): AuthResponseDto

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<Unit>
}
