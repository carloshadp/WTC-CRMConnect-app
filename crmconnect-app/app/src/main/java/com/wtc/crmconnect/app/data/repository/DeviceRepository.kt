package com.wtc.crmconnect.app.data.repository

import com.squareup.moshi.Moshi
import com.wtc.crmconnect.app.data.local.TokenStorage
import com.wtc.crmconnect.app.data.remote.api.DeviceApi
import com.wtc.crmconnect.app.data.remote.dto.device.RegisterTokenRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepository @Inject constructor(
    private val deviceApi: DeviceApi,
    private val tokenStorage: TokenStorage,
    private val moshi: Moshi
) {

    /**
     * Registra o token FCM do dispositivo no backend e guarda localmente para
     * comparação futura (evitar re-registro do mesmo token).
     */
    suspend fun registerToken(fcmToken: String): Result<Unit> = apiCall(moshi) {
        deviceApi.registerToken(RegisterTokenRequestDto(fcmToken)).requireSuccess()
        tokenStorage.saveFcmToken(fcmToken)
    }

    fun cachedToken(): String? = tokenStorage.getFcmToken()
}
