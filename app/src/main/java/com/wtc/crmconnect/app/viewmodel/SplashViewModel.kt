package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import com.wtc.crmconnect.app.data.remote.dto.enums.Role
import com.wtc.crmconnect.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Decide a rota inicial após o splash com base no token persistido.
 * Não valida o token contra o backend — o `AuthAuthenticator` cuida disso
 * em 401, forçando re-login se o refresh também falhar.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun resolveStartDestination(): Role? =
        if (authRepository.isAuthenticated()) authRepository.currentRole() else null
}
