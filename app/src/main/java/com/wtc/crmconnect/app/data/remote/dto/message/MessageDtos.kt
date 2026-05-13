package com.wtc.crmconnect.app.data.remote.dto.message

import com.wtc.crmconnect.app.data.remote.dto.enums.MessageStatus
import com.wtc.crmconnect.app.data.remote.dto.enums.MessageType
import com.wtc.crmconnect.app.data.remote.dto.enums.SenderType

data class SendMessageRequestDto(
    val content: String,
    val recipientCustomerId: String? = null,
    val segmentId: String? = null,
    val replyToMessageId: String? = null
)

data class SendMessageResponseDto(
    val broadcastId: String?,
    val recipientsCount: Int,
    val messages: List<MessageResponseDto>
)

data class MessageResponseDto(
    val id: String,
    val customerId: String,
    val operatorUserId: String?,
    val senderType: SenderType,
    val type: MessageType,
    val broadcastId: String?,
    val segmentId: String?,
    val campaignId: String?,
    val abVariant: String?,
    val content: String,
    val status: MessageStatus,
    val replyToMessageId: String?,
    val sentAt: String?,
    val deliveredAt: String?,
    val readAt: String?
)
