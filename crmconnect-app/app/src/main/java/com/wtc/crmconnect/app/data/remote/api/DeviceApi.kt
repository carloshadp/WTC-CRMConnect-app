package com.wtc.crmconnect.app.data.remote.api

import com.wtc.crmconnect.app.data.remote.dto.device.RegisterTokenRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface aDeviceApi {

    @POST("device/token")
    suspend fun registerToken(@Body request: RegisterTokenRequestDto): Response<Unit>
}
