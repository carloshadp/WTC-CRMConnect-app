package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wtc.crmconnect.app.data.local.TokenStorage
import com.wtc.crmconnect.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileEvent {
    data class ShowError(val message: String) : ProfileEvent
    data class ShowInfo(val message: String) : ProfileEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    val email: String get() = tokenStorage.getUserEmail() ?: ""
    val role: String get() = tokenStorage.getUserRole() ?: ""

    fun logout() = authRepository.logout()

    fun sendPasswordReset() {
        val addr = email
        if (addr.isBlank()) {
            viewModelScope.launch { _events.send(ProfileEvent.ShowError("Email não disponível.")) }
            return
        }
        viewModelScope.launch {
            authRepository.forgotPassword(addr)
                .onSuccess { _events.send(ProfileEvent.ShowInfo("Email de redefinição enviado para $addr")) }
                .onFailure { _events.send(ProfileEvent.ShowError(it.message ?: "Erro ao enviar email.")) }
        }
    }
}
