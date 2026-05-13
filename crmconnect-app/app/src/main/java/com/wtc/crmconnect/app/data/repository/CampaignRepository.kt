package com.wtc.crmconnect.app.data.repository

import com.squareup.moshi.Moshi
import com.wtc.crmconnect.app.data.remote.api.CampaignApi
import com.wtc.crmconnect.app.data.remote.dto.campaign.AbTestRequestDto
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignRequestDto
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.remote.dto.campaign.ScheduleRequestDto
import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampaignRepository @Inject constructor(
    private val campaignApi: CampaignApi,
    private val moshi: Moshi
) {

    suspend fun list(
        page: Int = 0,
        size: Int = 20,
        sort: String? = "createdAt,desc"
    ): Result<PageDto<CampaignResponseDto>> = apiCall(moshi) {
        campaignApi.list(page, size, sort)
    }

    suspend fun getById(id: String): Result<CampaignResponseDto> = apiCall(moshi) {
        campaignApi.getById(id)
    }

    suspend fun create(request: CampaignRequestDto): Result<CampaignResponseDto> = apiCall(moshi) {
        campaignApi.create(request)
    }

    suspend fun update(id: String, request: CampaignRequestDto): Result<CampaignResponseDto> = apiCall(moshi) {
        campaignApi.update(id, request)
    }

    /**
     * Cancela a campanha (`RASCUNHO`/`AGENDADA` → `CANCELADA`).
     * Backend usa DELETE mas retorna o recurso atualizado.
     */
    suspend fun cancel(id: String): Result<CampaignResponseDto> = apiCall(moshi) {
        campaignApi.cancel(id)
    }

    suspend fun schedule(id: String, scheduledAtIso: String): Result<CampaignResponseDto> = apiCall(moshi) {
        campaignApi.schedule(id, ScheduleRequestDto(scheduledAtIso))
    }

    suspend fun configureAbTest(
        id: String,
        variantBContent: String,
        splitPercent: Int,
        variantBTitle: String? = null
    ): Result<CampaignResponseDto> = apiCall(moshi) {
        campaignApi.configureAbTest(
            id,
            AbTestRequestDto(
                variantBTitle = variantBTitle,
                variantBContent = variantBContent,
                splitPercent = splitPercent
            )
        )
    }

    suspend fun sendNow(id: String): Result<CampaignResponseDto> = apiCall(moshi) {
        campaignApi.send(id)
    }
}
