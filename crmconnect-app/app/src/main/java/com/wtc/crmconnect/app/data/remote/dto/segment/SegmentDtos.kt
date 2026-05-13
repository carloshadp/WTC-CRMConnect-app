package com.wtc.crmconnect.app.data.remote.dto.segment

data class SegmentRequestDto(
    val name: String,
    val description: String?
)

data class SegmentResponseDto(
    val id: String,
    val name: String,
    val description: String?,
    val createdAt: String?,
    val updatedAt: String?
)
