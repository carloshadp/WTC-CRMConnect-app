package com.wtc.crmconnect.app.viewmodel

import androidx.lifecycle.ViewModel
import com.wtc.crmconnect.app.data.local.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tokenStorage: TokenStorage
) : ViewModel() {

    val userEmail: String
        get() = tokenStorage.getUserEmail() ?: "Operador"

    val userId: String?
        get() = tokenStorage.getUserId()
}
