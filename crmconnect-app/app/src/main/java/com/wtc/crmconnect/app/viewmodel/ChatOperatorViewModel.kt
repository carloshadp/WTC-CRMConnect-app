package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
import com.wtc.crmconnect.app.data.remote.dto.enums.MessageStatus
import com.wtc.crmconnect.app.data.remote.dto.enums.SenderType
import com.wtc.crmconnect.app.data.remote.dto.message.MessageResponseDto
import com.wtc.crmconnect.app.data.repository.CustomerRepository
import com.wtc.crmconnect.app.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatOperatorUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val customer: CustomerResponseDto? = null,
    val messages: List<MessageResponseDto> = emptyList()
)

sealed interface ChatOperatorEvent {
    data class ShowError(val message: String) : ChatOperatorEvent
}

@HiltViewModel
class ChatOperatorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle["customerId"]) {
        "customerId é obrigatório na rota do chat"
    }

    private val _uiState = MutableStateFlow(ChatOperatorUiState())
    val uiState: StateFlow<ChatOperatorUiState> = _uiState.asStateFlow()

    private val _events = Channel<ChatOperatorEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            customerRepository.getById(customerId)
                .onSuccess { customer ->
                    _uiState.update { it.copy(customer = customer) }
                }
                .onFailure { ex ->
                    _events.send(ChatOperatorEvent.ShowError(ex.message ?: "Falha ao carregar cliente."))
                }

            messageRepository.inbox(customerId)
                .onSuccess { page ->
                    val ordered = page.content.sortedBy { it.sentAt ?: "" }
                    _uiState.update { it.copy(isLoading = false, messages = ordered) }
                    markIncomingAsRead(ordered)
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(ChatOperatorEvent.ShowError(ex.message ?: "Falha ao carregar mensagens."))
                }
        }
    }

    fun send(text: String) {
        val content = text.trim()
        if (content.isBlank() || _uiState.value.isSending) return
        _uiState.update { it.copy(isSending = true) }
        viewModelScope.launch {
            messageRepository.sendIndividual(recipientCustomerId = customerId, content = content)
                .onSuccess { response ->
                    val sent = response.messages.firstOrNull()
                    if (sent != null) {
                        _uiState.update { it.copy(messages = it.messages + sent) }
                    }
                    _uiState.update { it.copy(isSending = false) }
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isSending = false) }
                    _events.send(ChatOperatorEvent.ShowError(ex.message ?: "Falha ao enviar mensagem."))
                }
        }
    }

    private fun markIncomingAsRead(messages: List<MessageResponseDto>) {
        val pending = messages.filter {
            it.senderType == SenderType.CLIENTE && it.status != MessageStatus.LIDO
        }
        if (pending.isEmpty()) return
        viewModelScope.launch {
            pending.forEach { msg ->
                messageRepository.markAsRead(msg.id).onSuccess { updated ->
                    _uiState.update { state ->
                        state.copy(messages = state.messages.map { if (it.id == updated.id) updated else it })
                    }
                }
            }
        }
    }
}
