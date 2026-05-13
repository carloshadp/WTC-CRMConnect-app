package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
import com.wtc.crmconnect.app.data.repository.CampaignRepository
import com.wtc.crmconnect.app.data.repository.CustomerRepository
import com.wtc.crmconnect.app.data.repository.SegmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeClientUiState(
    val isLoading: Boolean = false,
    val customer: CustomerResponseDto? = null,
    val segmentName: String? = null,
    val campaigns: List<CampaignResponseDto> = emptyList()
)

sealed interface HomeClientEvent {
    data class ShowError(val message: String) : HomeClientEvent
}

@HiltViewModel
class HomeClientViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val segmentRepository: SegmentRepository,
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeClientUiState())
    val uiState: StateFlow<HomeClientUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeClientEvent>(Channel.BUFFERED)
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
                    segmentRepository.getById(customer.segmentId)
                        .onSuccess { segment ->
                            _uiState.update { it.copy(segmentName = segment.name) }
                        }
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(HomeClientEvent.ShowError(ex.message ?: "Falha ao carregar perfil."))
                }
        }
        viewModelScope.launch {
            campaignRepository.list(page = 0, size = 10)
                .onSuccess { page ->
                    _uiState.update { it.copy(campaigns = page.content) }
                }
        }
    }
}
