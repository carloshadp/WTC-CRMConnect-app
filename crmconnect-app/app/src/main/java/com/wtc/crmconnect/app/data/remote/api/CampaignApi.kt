package com.wtc.crmconnect.app.data.remote.api

import com.wtc.crmconnect.app.data.remote.dto.campaign.AbTestRequestDto
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignRequestDto
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.remote.dto.campaign.ScheduleRequestDto
import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CampaignApi {

    @POST("campaigns")
    suspend fun create(@Body request: CampaignRequestDto): CampaignResponseDto

    @GET("campaigns")
    suspend fun list(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String? = null
    ): PageDto<CampaignResponseDto>

    @GET("campaigns/{id}")
    suspend fun getById(@Path("id") id: String): CampaignResponseDto

    @PUT("campaigns/{id}")
    suspend fun update(@Path("id") id: String, @Body request: CampaignRequestDto): CampaignResponseDto

    @DELETE("campaigns/{id}")
    suspend fun cancel(@Path("id") id: String): CampaignResponseDto

    @POST("campaigns/{id}/schedule")
    suspend fun schedule(@Path("id") id: String, @Body request: ScheduleRequestDto): CampaignResponseDto

    @POST("campaigns/{id}/abtest")
    suspend fun configureAbTest(@Path("id") id: String, @Body request: AbTestRequestDto): CampaignResponseDto

    @POST("campaigns/{id}/send")
    suspend fun send(@Path("id") id: String): CampaignResponseDto
}
