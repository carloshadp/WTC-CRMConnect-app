package com.wtc.crmconnect.app.data.remote.api

import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentRequestDto
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SegmentApi {

    @POST("segments")
    suspend fun create(@Body request: SegmentRequestDto): SegmentResponseDto

    @GET("segments")
    suspend fun list(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
        @Query("sort") sort: String? = null
    ): PageDto<SegmentResponseDto>

    @GET("segments/{id}")
    suspend fun getById(@Path("id") id: String): SegmentResponseDto

    @PUT("segments/{id}")
    suspend fun update(@Path("id") id: String, @Body request: SegmentRequestDto): SegmentResponseDto

    @DELETE("segments/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>
}
