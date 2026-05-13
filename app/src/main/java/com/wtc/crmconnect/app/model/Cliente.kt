package com.wtc.crmconnect.app.model

data class Cliente(
    val id: Int,
    val imageRes: Int,
    val name: String,
    val message: String,
    val unreadCount: Int,
    val isPinned: Boolean = false,
    val isUnread: Boolean = false,
    var noteText: String = ""
)

data class GrupoCliente(
    val id: Int,
    val imageRes: Int,
    val name: String,
    val isPinned: Boolean = false
)
