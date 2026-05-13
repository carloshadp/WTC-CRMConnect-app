package com.wtc.crmconnect.app.data.remote.api

import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerRequestDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerTimelineDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CustomerApi {

    @POST("customers")
    suspend fun create(@Body request: CustomerRequestDto): CustomerResponseDto

    @GET("customers")
    suspend fun list(
        @Query("segmentId") segmentId: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): PageDto<CustomerResponseDto>

    @GET("customers/me")
    suspend fun getMe(): CustomerResponseDto

    @GET("customers/{id}")
    suspend fun getById(@Path("id") id: String): CustomerResponseDto

    @PUT("customers/{id}")
    suspend fun update(@Path("id") id: String, @Body request: CustomerRequestDto): CustomerResponseDto

    @DELETE("customers/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>

    @GET("customers/{id}/timeline")
    suspend fun getTimeline(@Path("id") id: String): CustomerTimelineDto
}
