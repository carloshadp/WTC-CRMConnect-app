package com.wtc.crmconnect.app.data.remote.api

import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import com.wtc.crmconnect.app.data.remote.dto.message.MessageResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InboxApi {

    @GET("inbox/{customerId}")
    suspend fun inbox(
        @Path("customerId") customerId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 30,
        @Query("sort") sort: String? = null
    ): PageDto<MessageResponseDto>
}
