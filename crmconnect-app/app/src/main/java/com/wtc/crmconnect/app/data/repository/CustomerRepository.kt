package com.wtc.crmconnect.app.data.repository

import com.squareup.moshi.Moshi
import com.wtc.crmconnect.app.data.remote.api.CustomerApi
import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerRequestDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerTimelineDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val customerApi: CustomerApi,
    private val moshi: Moshi
) {

    suspend fun list(
        segmentId: String? = null,
        page: Int = 0,
        size: Int = 20,
        sort: String? = "name,asc"
    ): Result<PageDto<CustomerResponseDto>> = apiCall(moshi) {
        customerApi.list(segmentId, page, size, sort)
    }

    suspend fun getMe(): Result<CustomerResponseDto> = apiCall(moshi) {
        customerApi.getMe()
    }

    suspend fun getById(id: String): Result<CustomerResponseDto> = apiCall(moshi) {
        customerApi.getById(id)
    }

    suspend fun create(request: CustomerRequestDto): Result<CustomerResponseDto> = apiCall(moshi) {
        customerApi.create(request)
    }

    suspend fun update(id: String, request: CustomerRequestDto): Result<CustomerResponseDto> = apiCall(moshi) {
        customerApi.update(id, request)
    }

    suspend fun delete(id: String): Result<Unit> = apiCall(moshi) {
        customerApi.delete(id).requireSuccess()
    }

    suspend fun getTimeline(id: String): Result<CustomerTimelineDto> = apiCall(moshi) {
        customerApi.getTimeline(id)
    }
}
