package com.wtc.crmconnect.app.data.remote.dto.error

data class ErrorResponseDto(
    val timestamp: String?,
    val status: Int,
    val error: String?,
    val message: String?,
    val path: String?
)
