package com.wtc.crmconnect.app.data.remote.dto.customer

import com.wtc.crmconnect.app.data.remote.dto.enums.CustomerStatus

data class CustomerRequestDto(
    val name: String,
    val email: String,
    val phone: String?,
    val segmentId: String,
    val tags: List<String>?,
    val score: Int?,
    val status: CustomerStatus
)

data class CustomerResponseDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val segmentId: String,
    val tags: List<String>?,
    val score: Int?,
    val status: CustomerStatus,
    val createdAt: String?,
    val updatedAt: String?
)

data class CustomerTimelineDto(
    val customer: CustomerResponseDto,
    val messages: List<TimelineMessageDto>,
    val campaigns: List<TimelineCampaignDto>,
    val tasks: List<TimelineTaskDto>
)

data class TimelineMessageDto(
    val id: String,
    val content: String,
    val status: String,
    val sentAt: String?,
    val readAt: String?
)

data class TimelineCampaignDto(
    val id: String,
    val title: String,
    val status: String,
    val sentAt: String?,
    val deeplink: String?
)

data class TimelineTaskDto(
    val id: String,
    val title: String,
    val status: String,
    val dueDate: String?
)
