package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerTimelineDto
import com.wtc.crmconnect.app.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerTimelineUiState(
    val isLoading: Boolean = false,
    val timeline: CustomerTimelineDto? = null
)

sealed interface CustomerTimelineEvent {
    data class ShowError(val message: String) : CustomerTimelineEvent
}

@HiltViewModel
class CustomerTimelineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    val customerId: String = checkNotNull(savedStateHandle["customerId"]) {
        "customerId é obrigatório na rota da Timeline"
    }

    private val _uiState = MutableStateFlow(CustomerTimelineUiState())
    val uiState: StateFlow<CustomerTimelineUiState> = _uiState.asStateFlow()

    private val _events = Channel<CustomerTimelineEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            customerRepository.getTimeline(customerId)
                .onSuccess { timeline ->
                    _uiState.update { it.copy(isLoading = false, timeline = timeline) }
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(CustomerTimelineEvent.ShowError(ex.message ?: "Falha ao carregar timeline."))
                }
        }
    }
}
