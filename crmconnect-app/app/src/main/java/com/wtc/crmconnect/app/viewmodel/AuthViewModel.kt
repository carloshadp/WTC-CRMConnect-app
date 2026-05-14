package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.wtc.crmconnect.app.data.remote.dto.enums.Role
import com.wtc.crmconnect.app.data.repository.AuthRepository
import com.wtc.crmconnect.app.data.repository.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val phone: String = "",
    val userTypeLabel: String = "",
    val isLoading: Boolean = false
)

sealed interface AuthEvent {
    data class LoginSucceeded(val role: Role) : AuthEvent
    data object RegisterSucceeded : AuthEvent
    data object ForgotPasswordSent : AuthEvent
    data object PasswordReset : AuthEvent
    data class ShowError(val message: String) : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value) }
    fun onConfirmPasswordChanged(value: String) = _uiState.update { it.copy(confirmPassword = value) }
    fun onNameChanged(value: String) = _uiState.update { it.copy(name = value) }
    fun onPhoneChanged(value: String) = _uiState.update { it.copy(phone = value) }
    fun onUserTypeChanged(label: String) = _uiState.update { it.copy(userTypeLabel = label) }

    fun resetForm() = _uiState.update { AuthUiState() }

    fun login() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.email.isBlank() || state.password.isBlank()) {
            sendError("Informe email e senha.")
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.login(state.email.trim(), state.password)
                .onSuccess { auth ->
                    _uiState.update { AuthUiState() }
                    registerFcmToken()
                    _events.send(AuthEvent.LoginSucceeded(auth.role))
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    sendError(ex.message ?: "Falha ao entrar.")
                }
        }
    }

    fun register() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.email.isBlank() || state.password.isBlank()) {
            sendError("Preencha email e senha.")
            return
        }
        if (state.password != state.confirmPassword) {
            sendError("Senhas não conferem.")
            return
        }
        val role = roleFromLabel(state.userTypeLabel) ?: run {
            sendError("Selecione o tipo de usuário.")
            return
        }
        if (role == Role.CUSTOMER && state.name.isBlank()) {
            sendError("Informe seu nome completo.")
            return
        }
        val email = state.email.trim()
        val name = state.name.trim().ifBlank { null }
        val phone = state.phone.trim().ifBlank { null }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.register(email, state.password, role, name, phone)
                .onSuccess {
                    _uiState.update { AuthUiState() }
                    _events.send(AuthEvent.RegisterSucceeded)
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    sendError(ex.message ?: "Falha ao cadastrar.")
                }
        }
    }

    fun forgotPassword() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.email.isBlank()) {
            sendError("Informe o email.")
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.forgotPassword(state.email.trim())
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(AuthEvent.ForgotPasswordSent)
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    sendError(ex.message ?: "Falha ao solicitar reset.")
                }
        }
    }

    fun resetPassword(token: String) {
        val state = _uiState.value
        if (state.isLoading) return
        if (token.isBlank()) {
            sendError("Token de reset ausente.")
            return
        }
        if (state.password.isBlank()) {
            sendError("Informe a nova senha.")
            return
        }
        if (state.password != state.confirmPassword) {
            sendError("Senhas não conferem.")
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            authRepository.resetPassword(token, state.password)
                .onSuccess {
                    _uiState.update { AuthUiState() }
                    _events.send(AuthEvent.PasswordReset)
                }
                .onFailure { ex ->
                    _uiState.update { it.copy(isLoading = false) }
                    sendError(ex.message ?: "Falha ao redefinir senha.")
                }
        }
    }

    fun logout() {
        authRepository.logout()
        resetForm()
    }

    private fun registerFcmToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                viewModelScope.launch {
                    deviceRepository.registerToken(token).onFailure { /* silent */ }
                }
            }
        } catch (_: Exception) {
            // Firebase não configurado (google-services.json placeholder) — ignorar
        }
    }

    private fun sendError(message: String) {
        viewModelScope.launch { _events.send(AuthEvent.ShowError(message)) }
    }

    companion object {
        fun roleFromLabel(label: String): Role? = when (label) {
            "Operador" -> Role.OPERATOR
            "Cliente" -> Role.CUSTOMER
            else -> null
        }

        fun homeRouteFor(role: Role): String = when (role) {
            Role.OPERATOR -> "home_screen"
            Role.CUSTOMER -> "home_client_screen"
        }
    }
}
