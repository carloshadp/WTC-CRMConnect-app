package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentRequestDto
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentResponseDto
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

data class SegmentsUiState(
    val segments: List<SegmentResponseDto> = emptyList(),
    val isLoading: Boolean = false,
    val showCreateDialog: Boolean = false,
    val editingSegment: SegmentResponseDto? = null,
    val formName: String = "",
    val formDescription: String = "",
    val isSaving: Boolean = false,
    val deleteConfirmId: String? = null
)

sealed interface SegmentsEvent {
    data class ShowError(val message: String) : SegmentsEvent
    data class ShowInfo(val message: String) : SegmentsEvent
}

@HiltViewModel
class SegmentsViewModel @Inject constructor(
    private val segmentRepository: SegmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SegmentsUiState())
    val uiState: StateFlow<SegmentsUiState> = _uiState.asStateFlow()

    private val _events = Channel<SegmentsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            segmentRepository.listAll()
                .onSuccess { list -> _uiState.update { it.copy(segments = list, isLoading = false) } }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(SegmentsEvent.ShowError(ex.message ?: "Erro ao carregar segmentos."))
                }
        }
    }

    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true, editingSegment = null, formName = "", formDescription = "") }
    }

    fun showEditDialog(segment: SegmentResponseDto) {
        _uiState.update {
            it.copy(
                showCreateDialog = false,
                editingSegment = segment,
                formName = segment.name,
                formDescription = segment.description ?: ""
            )
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showCreateDialog = false, editingSegment = null, formName = "", formDescription = "") }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(formName = value) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(formDescription = value) }

    fun showDeleteConfirm(id: String) = _uiState.update { it.copy(deleteConfirmId = id) }
    fun dismissDeleteConfirm() = _uiState.update { it.copy(deleteConfirmId = null) }

    fun save() {
        val state = _uiState.value
        if (state.formName.isBlank()) {
            viewModelScope.launch { _events.send(SegmentsEvent.ShowError("Nome do segmento é obrigatório.")) }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        val request = SegmentRequestDto(
            name = state.formName.trim(),
            description = state.formDescription.trim().ifBlank { null }
        )
        viewModelScope.launch {
            val result = if (state.editingSegment != null) {
                segmentRepository.update(state.editingSegment.id, request)
            } else {
                segmentRepository.create(request)
            }
            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    dismissDialog()
                    load()
                    _events.send(SegmentsEvent.ShowInfo(if (state.editingSegment != null) "Segmento atualizado." else "Segmento criado."))
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(SegmentsEvent.ShowError(ex.message ?: "Erro ao salvar."))
                }
        }
    }

    fun confirmDelete() {
        val id = _uiState.value.deleteConfirmId ?: return
        _uiState.update { it.copy(deleteConfirmId = null) }
        viewModelScope.launch {
            segmentRepository.delete(id)
                .onSuccess {
                    load()
                    _events.send(SegmentsEvent.ShowInfo("Segmento excluído."))
                }
                .onFailure { ex ->
                    _events.send(SegmentsEvent.ShowError(ex.message ?: "Erro ao excluir."))
                }
        }
    }
}
