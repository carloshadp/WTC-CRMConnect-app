package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignRequestDto
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentResponseDto
import com.wtc.crmconnect.app.data.repository.CampaignRepository
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

data class CampaignFormState(
    val title: String = "",
    val content: String = "",
    val segmentId: String = "",
    val deeplink: String = ""
)

data class CampaignDetailUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isActioning: Boolean = false,
    val isNew: Boolean = true,
    val campaign: CampaignResponseDto? = null,
    val form: CampaignFormState = CampaignFormState(),
    val segments: List<SegmentResponseDto> = emptyList()
)

sealed interface CampaignDetailEvent {
    data class ShowError(val message: String) : CampaignDetailEvent
    data class ShowInfo(val message: String) : CampaignDetailEvent
    data object NavigateBack : CampaignDetailEvent
}

@HiltViewModel
class CampaignDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val campaignRepository: CampaignRepository,
    private val segmentRepository: SegmentRepository
) : ViewModel() {

    private val campaignId: String = checkNotNull(savedStateHandle["campaignId"]) {
        "campaignId é obrigatório (use 'new' para criação)"
    }

    private val _uiState = MutableStateFlow(CampaignDetailUiState(isNew = campaignId == "new"))
    val uiState: StateFlow<CampaignDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<CampaignDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadSegments()
        if (campaignId != "new") loadCampaign()
    }

    private fun loadSegments() {
        viewModelScope.launch {
            segmentRepository.listAll()
                .onSuccess { list ->
                    _uiState.update { it.copy(segments = list) }
                }
                .onFailure { ex ->
                    _events.send(CampaignDetailEvent.ShowError(ex.message ?: "Falha ao carregar segmentos."))
                }
        }
    }

    private fun loadCampaign() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            campaignRepository.getById(campaignId)
                .onSuccess { campaign ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            campaign = campaign,
                            form = CampaignFormState(
                                title = campaign.title,
                                content = campaign.content,
                                segmentId = campaign.segmentId,
                                deeplink = campaign.deeplink.orEmpty()
                            )
                        )
                    }
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(CampaignDetailEvent.ShowError(ex.message ?: "Falha ao carregar campanha."))
                }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(form = it.form.copy(title = value)) }
    fun onContentChange(value: String) = _uiState.update { it.copy(form = it.form.copy(content = value)) }
    fun onSegmentChange(value: String) = _uiState.update { it.copy(form = it.form.copy(segmentId = value)) }
    fun onDeeplinkChange(value: String) = _uiState.update { it.copy(form = it.form.copy(deeplink = value)) }

    fun save() {
        val form = _uiState.value.form
        if (form.title.isBlank() || form.content.isBlank() || form.segmentId.isBlank()) {
            viewModelScope.launch {
                _events.send(CampaignDetailEvent.ShowError("Preencha título, conteúdo e segmento."))
            }
            return
        }
        val isNew = _uiState.value.isNew
        val request = CampaignRequestDto(
            title = form.title,
            content = form.content,
            segmentId = form.segmentId,
            deeplink = if (isNew) null else form.deeplink.ifBlank { null }
        )
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val result = if (isNew) {
                campaignRepository.create(request)
            } else {
                campaignRepository.update(campaignId, request)
            }
            result
                .onSuccess { created ->
                    val finalCampaign = if (isNew) {
                        val deeplink = "crmconnect://campaign/${created.id}"
                        campaignRepository.update(created.id, request.copy(deeplink = deeplink))
                            .getOrElse { created }
                    } else {
                        created
                    }
                    _uiState.update { it.copy(isSaving = false, campaign = finalCampaign, isNew = false) }
                    _events.send(CampaignDetailEvent.ShowInfo("Campanha salva."))
                    _events.send(CampaignDetailEvent.NavigateBack)
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(CampaignDetailEvent.ShowError(ex.message ?: "Falha ao salvar."))
                }
        }
    }

    fun schedule(scheduledAtIso: String) {
        val campaign = _uiState.value.campaign ?: return
        _uiState.update { it.copy(isActioning = true) }
        viewModelScope.launch {
            campaignRepository.schedule(campaign.id, scheduledAtIso)
                .onSuccess { updated ->
                    _uiState.update { it.copy(isActioning = false, campaign = updated) }
                    _events.send(CampaignDetailEvent.ShowInfo("Campanha agendada para $scheduledAtIso."))
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isActioning = false) }
                    _events.send(CampaignDetailEvent.ShowError(ex.message ?: "Falha ao agendar."))
                }
        }
    }

    fun configureAbTest(variantBContent: String, splitPercent: Int, variantBTitle: String?) {
        val campaign = _uiState.value.campaign ?: return
        _uiState.update { it.copy(isActioning = true) }
        viewModelScope.launch {
            campaignRepository.configureAbTest(campaign.id, variantBContent, splitPercent, variantBTitle)
                .onSuccess { updated ->
                    _uiState.update { it.copy(isActioning = false, campaign = updated) }
                    _events.send(CampaignDetailEvent.ShowInfo("A/B test configurado."))
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isActioning = false) }
                    _events.send(CampaignDetailEvent.ShowError(ex.message ?: "Falha no A/B test."))
                }
        }
    }

    fun sendNow() {
        val campaign = _uiState.value.campaign ?: return
        _uiState.update { it.copy(isActioning = true) }
        viewModelScope.launch {
            campaignRepository.sendNow(campaign.id)
                .onSuccess { updated ->
                    _uiState.update { it.copy(isActioning = false, campaign = updated) }
                    _events.send(CampaignDetailEvent.ShowInfo("Campanha enviada."))
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isActioning = false) }
                    _events.send(CampaignDetailEvent.ShowError(ex.message ?: "Falha ao enviar."))
                }
        }
    }

    fun cancel() {
        val campaign = _uiState.value.campaign ?: return
        _uiState.update { it.copy(isActioning = true) }
        viewModelScope.launch {
            campaignRepository.cancel(campaign.id)
                .onSuccess { updated ->
                    _uiState.update { it.copy(isActioning = false, campaign = updated) }
                    _events.send(CampaignDetailEvent.ShowInfo("Campanha cancelada."))
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isActioning = false) }
                    _events.send(CampaignDetailEvent.ShowError(ex.message ?: "Falha ao cancelar."))
                }
        }
    }
}
