package com.wtc.crmconnect.app.data.remote.dto.common

/**
 * Espelha o `org.springframework.data.domain.Page<T>` serializado por Spring.
 * Apenas campos necessários para listagem paginada do app.
 */
data class PageDto<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val first: Boolean? = null,
    val last: Boolean? = null,
    val empty: Boolean? = null
)
