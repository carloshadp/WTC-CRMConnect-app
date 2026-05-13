package com.wtc.crmconnect.app.data.repository

import com.squareup.moshi.Moshi
import com.wtc.crmconnect.app.data.remote.api.SegmentApi
import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentRequestDto
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SegmentRepository @Inject constructor(
    private val segmentApi: SegmentApi,
    private val moshi: Moshi
) {

    suspend fun list(
        page: Int = 0,
        size: Int = 50,
        sort: String? = "name,asc"
    ): Result<PageDto<SegmentResponseDto>> = apiCall(moshi) {
        segmentApi.list(page, size, sort)
    }

    /**
     * Carrega todos os segmentos navegando pelas páginas — pensado para dropdown selector.
     * Backend retorna page=50 por padrão, o que costuma cobrir o caso real.
     */
    suspend fun listAll(): Result<List<SegmentResponseDto>> = apiCall(moshi) {
        val accumulator = mutableListOf<SegmentResponseDto>()
        var page = 0
        do {
            val response = segmentApi.list(page = page, size = 50, sort = "name,asc")
            accumulator += response.content
            page++
        } while (response.last != true && response.content.isNotEmpty())
        accumulator
    }

    suspend fun getById(id: String): Result<SegmentResponseDto> = apiCall(moshi) {
        segmentApi.getById(id)
    }

    suspend fun create(request: SegmentRequestDto): Result<SegmentResponseDto> = apiCall(moshi) {
        segmentApi.create(request)
    }

    suspend fun update(id: String, request: SegmentRequestDto): Result<SegmentResponseDto> = apiCall(moshi) {
        segmentApi.update(id, request)
    }

    suspend fun delete(id: String): Result<Unit> = apiCall(moshi) {
        segmentApi.delete(id).requireSuccess()
    }
}
