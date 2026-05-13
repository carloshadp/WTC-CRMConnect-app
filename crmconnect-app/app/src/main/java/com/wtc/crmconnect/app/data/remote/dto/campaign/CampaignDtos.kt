package com.wtc.crmconnect.app.data.remote.dto.campaign

import com.wtc.crmconnect.app.data.remote.dto.enums.CampaignStatus

data class CampaignRequestDto(
    val title: String,
    val content: String,
    val segmentId: String,
    val deeplink: String? = null
)

data class CampaignResponseDto(
    val id: String,
    val title: String,
    val content: String,
    val segmentId: String,
    val deeplink: String?,
    val status: CampaignStatus,
    val scheduledAt: String?,
    val sentAt: String?,
    val createdByUserId: String,
    val createdAt: String?,
    val updatedAt: String?,
    val abTest: AbTestVariantDto?
)

data class ScheduleRequestDto(
    val scheduledAt: String
)

data class AbTestRequestDto(
    val variantBTitle: String? = null,
    val variantBContent: String,
    val splitPercent: Int
)

data class AbTestVariantDto(
    val variantBTitle: String?,
    val variantBContent: String,
    val splitPercent: Int
)
