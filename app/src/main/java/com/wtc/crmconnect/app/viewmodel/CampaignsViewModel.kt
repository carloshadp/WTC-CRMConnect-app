package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CampaignsUiState(
    val isLoading: Boolean = false,
    val campaigns: List<CampaignResponseDto> = emptyList()
)

sealed interface CampaignsEvent {
    data class ShowError(val message: String) : CampaignsEvent
}

@HiltViewModel
class CampaignsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CampaignsUiState())
    val uiState: StateFlow<CampaignsUiState> = _uiState.asStateFlow()

    private val _events = Channel<CampaignsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            campaignRepository.list(page = 0, size = 50)
                .onSuccess { page ->
                    _uiState.update { it.copy(isLoading = false, campaigns = page.content) }
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(CampaignsEvent.ShowError(ex.message ?: "Falha ao carregar campanhas."))
                }
        }
    }
}
