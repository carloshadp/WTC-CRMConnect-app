package com.wtc.crmconnect.app.data.repository

import com.squareup.moshi.Moshi
import com.wtc.crmconnect.app.data.remote.api.InboxApi
import com.wtc.crmconnect.app.data.remote.api.MessageApi
import com.wtc.crmconnect.app.data.remote.dto.common.PageDto
import com.wtc.crmconnect.app.data.remote.dto.message.MessageResponseDto
import com.wtc.crmconnect.app.data.remote.dto.message.SendMessageRequestDto
import com.wtc.crmconnect.app.data.remote.dto.message.SendMessageResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messageApi: MessageApi,
    private val inboxApi: InboxApi,
    private val moshi: Moshi
) {

    /**
     * Envia mensagem individual (OPERATOR → 1 customer).
     */
    suspend fun sendIndividual(
        recipientCustomerId: String,
        content: String,
        replyToMessageId: String? = null
    ): Result<SendMessageResponseDto> = apiCall(moshi) {
        messageApi.send(
            SendMessageRequestDto(
                content = content,
                recipientCustomerId = recipientCustomerId,
                segmentId = null,
                replyToMessageId = replyToMessageId
            )
        )
    }

    /**
     * Envia broadcast (OPERATOR → todos os customers de um segmento).
     */
    suspend fun sendBroadcast(
        segmentId: String,
        content: String
    ): Result<SendMessageResponseDto> = apiCall(moshi) {
        messageApi.send(
            SendMessageRequestDto(
                content = content,
                recipientCustomerId = null,
                segmentId = segmentId,
                replyToMessageId = null
            )
        )
    }

    /**
     * CUSTOMER respondendo ao operador da conversa.
     * `replyToMessageId` opcional — se omitido, backend pega o último OPERATOR.
     */
    suspend fun replyAsCustomer(
        content: String,
        replyToMessageId: String? = null
    ): Result<SendMessageResponseDto> = apiCall(moshi) {
        messageApi.send(
            SendMessageRequestDto(
                content = content,
                recipientCustomerId = null,
                segmentId = null,
                replyToMessageId = replyToMessageId
            )
        )
    }

    suspend fun getById(id: String): Result<MessageResponseDto> = apiCall(moshi) {
        messageApi.getById(id)
    }

    suspend fun markAsRead(id: String): Result<MessageResponseDto> = apiCall(moshi) {
        messageApi.markAsRead(id)
    }

    suspend fun inbox(
        customerId: String,
        page: Int = 0,
        size: Int = 30,
        sort: String? = "sentAt,desc"
    ): Result<PageDto<MessageResponseDto>> = apiCall(moshi) {
        inboxApi.inbox(customerId, page, size, sort)
    }
}
