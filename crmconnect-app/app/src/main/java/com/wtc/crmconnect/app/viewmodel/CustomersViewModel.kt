package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
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

data class CustomersUiState(
    val isLoading: Boolean = false,
    val customers: List<CustomerResponseDto> = emptyList(),
    val searchText: String = "",
    val segmentFilter: String? = null
) {
    val filtered: List<CustomerResponseDto>
        get() = if (searchText.isBlank()) customers
        else customers.filter {
            it.name.contains(searchText, ignoreCase = true) ||
                    it.email.contains(searchText, ignoreCase = true)
        }
}

sealed interface CustomersEvent {
    data class ShowError(val message: String) : CustomersEvent
}

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomersUiState())
    val uiState: StateFlow<CustomersUiState> = _uiState.asStateFlow()

    private val _events = Channel<CustomersEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            customerRepository.list(
                segmentId = _uiState.value.segmentFilter,
                page = 0,
                size = 100
            ).onSuccess { page ->
                _uiState.update { it.copy(isLoading = false, customers = page.content) }
            }.onFailure { ex ->
                _uiState.update { it.copy(isLoading = false) }
                _events.send(CustomersEvent.ShowError(ex.message ?: "Falha ao carregar clientes."))
            }
        }
    }

    fun onSearchTextChanged(value: String) = _uiState.update { it.copy(searchText = value) }
}
