package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
import com.wtc.crmconnect.app.data.remote.dto.message.MessageResponseDto
import com.wtc.crmconnect.app.data.repository.CampaignRepository
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

data class Client360UiState(
    val isLoading: Boolean = false,
    val customer: CustomerResponseDto? = null,
    val recentMessages: List<MessageResponseDto> = emptyList(),
    val recentCampaigns: List<CampaignResponseDto> = emptyList()
)

sealed interface Client360Event {
    data class ShowError(val message: String) : Client360Event
}

@HiltViewModel
class Client360ViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val messageRepository: MessageRepository,
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(Client360UiState())
    val uiState: StateFlow<Client360UiState> = _uiState.asStateFlow()

    private val _events = Channel<Client360Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            customerRepository.getMe()
                .onSuccess { customer ->
                    _uiState.update { it.copy(isLoading = false, customer = customer) }
                    messageRepository.inbox(customer.id, page = 0, size = 5, sort = "sentAt,desc")
                        .onSuccess { page ->
                            _uiState.update { it.copy(recentMessages = page.content) }
                        }
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(Client360Event.ShowError(ex.message ?: "Falha ao carregar dados."))
                }
        }
        viewModelScope.launch {
            campaignRepository.list(page = 0, size = 5)
                .onSuccess { page ->
                    _uiState.update { it.copy(recentCampaigns = page.content) }
                }
        }
    }
}
