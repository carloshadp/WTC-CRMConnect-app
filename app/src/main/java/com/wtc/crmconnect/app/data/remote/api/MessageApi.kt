package com.wtc.crmconnect.app.data.remote.api

import com.wtc.crmconnect.app.data.remote.dto.message.MessageResponseDto
import com.wtc.crmconnect.app.data.remote.dto.message.SendMessageRequestDto
import com.wtc.crmconnect.app.data.remote.dto.message.SendMessageResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApi {

    @POST("messages")
    suspend fun send(@Body request: SendMessageRequestDto): SendMessageResponseDto

    @GET("messages/{id}")
    suspend fun getById(@Path("id") id: String): MessageResponseDto

    @PATCH("messages/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): MessageResponseDto
}
