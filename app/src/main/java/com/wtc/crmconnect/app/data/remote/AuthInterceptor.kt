package com.wtc.crmconnect.app.data.remote

import com.wtc.crmconnect.app.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Anexa `Authorization: Bearer <access>` em todas as requisições autenticadas.
 * Rotas em [PUBLIC_PATHS] passam sem header (login/refresh/forgot/reset).
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (PUBLIC_PATHS.any { path.endsWith(it) }) {
            return chain.proceed(request)
        }

        val token = tokenStorage.getAccessToken()
        val updatedRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(updatedRequest)
    }

    private companion object {
        val PUBLIC_PATHS = listOf(
            "/auth/login",
            "/auth/register",
            "/auth/refresh",
            "/auth/forgot-password",
            "/auth/reset-password"
        )
    }
}
