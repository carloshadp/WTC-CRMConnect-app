package com.wtc.crmconnect.app.model

data class Operador(
    val id: Int,
    val imageRes: Int,
    val name: String,
    val message: String,
    val unreadCount: Int,
    val isPinned: Boolean = false,
    val isUnread: Boolean = false,
)
