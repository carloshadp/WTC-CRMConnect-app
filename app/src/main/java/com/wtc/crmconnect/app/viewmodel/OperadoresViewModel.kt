package com.wtc.crmconnect.app.viewmodel



import com.wtc.crmconnect.app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wtc.crmconnect.app.model.Operador

class OperadoresViewModel : ViewModel() {


    // LISTA DE OPERADORES - HistoryCustomersClientScreen
    var operadoresHistory by mutableStateOf(
        listOf(
            Operador(1, R.drawable.ic_chat_client, "Operador A", "Lorem ipsum dolor sit amet.", 2),
            Operador(2, R.drawable.ic_chat_client, "Operador B", "Consectetur adipiscing elit.", 3),
            Operador(3, R.drawable.ic_chat_client, "Operador C", "Sed do eiusmod tempor.", 5),
        )
    )
        private set

    private val ordemOperadoresOriginal = listOf(
        Operador(1, R.drawable.ic_chat_client, "Operador A", "Lorem ipsum dolor sit amet.", 2),
        Operador(2, R.drawable.ic_chat_client, "Operador B", "Consectetur adipiscing elit.", 3),
        Operador(3, R.drawable.ic_chat_client, "Operador C", "Sed do eiusmod tempor.", 5),
    )

    // LISTA DE OPERADORES FIXADOS
    var operadoresFixados by mutableStateOf(listOf<Operador>())
        private set

    // LISTA DE OPERADORES NÃO LIDOS
    var operadoresNaoLidos by mutableStateOf(listOf<Operador>())
        private set

    // LISTA DE GRUPOS
    var grupos by mutableStateOf(
        listOf(
            GrupoOperador(
                id = 100,
                nome = "Suporte Geral",
                operadores = listOf(
                    GrupoOperador(id = 101, nome = "Operador A", operadores = emptyList()),
                    GrupoOperador(id = 102, nome = "Operador B", operadores = emptyList()),
                    GrupoOperador(id = 103, nome = "Operador C", operadores = emptyList())
                )
            )
        )
    )
        private set

    var grupoOperadorSelecionado by mutableStateOf<GrupoOperador?>(null)
        private set

    fun selecionarGrupoOperador(grupo: GrupoOperador?) {
        grupoOperadorSelecionado = grupo
    }

    // GRUPO DE OPERADORES
    data class GrupoOperador(
        val id: Int,
        val nome: String,
        val operadores: List<GrupoOperador>,
        val isPinned: Boolean = false,
        val unreadCount: Int = 0,
        var noteText: String = ""
    )

    var gruposOperadoresFixados by mutableStateOf(listOf<GrupoOperador>())
        private set

    var gruposOperadoresNaoLidos by mutableStateOf(listOf<GrupoOperador>())
        private set

    fun markUnreadGrupo(grupoId: Int) {
        val estaEmNaoLidos = gruposOperadoresNaoLidos.any { it.id == grupoId }

        val grupo = (grupos + gruposOperadoresFixados + gruposOperadoresNaoLidos)
            .find { it.id == grupoId } ?: return

        grupos = grupos.filterNot { it.id == grupoId }
        gruposOperadoresFixados = gruposOperadoresFixados.filterNot { it.id == grupoId }
        gruposOperadoresNaoLidos = gruposOperadoresNaoLidos.filterNot { it.id == grupoId }

        if (estaEmNaoLidos) {
            // VOLTA PARA A LISTA NORMAL
            grupos = (grupos + grupo.copy(
                unreadCount = 0,
                isPinned = false
            )).sortedBy { it.nome }
        } else {
            // VAI PARA NÃO LIDOS
            gruposOperadoresNaoLidos = (gruposOperadoresNaoLidos + grupo.copy(
                unreadCount = 1,
                isPinned = false
            )).sortedBy { it.nome }
        }
    }

    fun togglePinGrupo(grupoId: Int) {
        val grupo = (grupos + gruposOperadoresFixados + gruposOperadoresNaoLidos)
            .find { it.id == grupoId } ?: return

        val estaFixado = gruposOperadoresFixados.any { it.id == grupoId }

        grupos = grupos.filterNot { it.id == grupoId }
        gruposOperadoresFixados = gruposOperadoresFixados.filterNot { it.id == grupoId }
        gruposOperadoresNaoLidos = gruposOperadoresNaoLidos.filterNot { it.id == grupoId }

        if (estaFixado) {
            grupos = (grupos + grupo.copy(
                isPinned = false,
                unreadCount = 0
            )).sortedBy { it.nome }
        } else {
            gruposOperadoresFixados = (gruposOperadoresFixados + grupo.copy(
                isPinned = true,
                unreadCount = 0
            )).sortedBy { it.nome }
        }
    }

    var searchText by mutableStateOf("")
        private set

    fun onSearchTextChanged(newText: String) {
        searchText = newText
    }

    fun markUnread(operadorId: Int) {
        val estaEmNaoLidos = operadoresNaoLidos.any { it.id == operadorId }

        val operador = (operadoresHistory + operadoresFixados + operadoresNaoLidos)
            .find { it.id == operadorId } ?: return
        operadoresHistory = operadoresHistory.filterNot { it.id == operadorId }
        operadoresFixados = operadoresFixados.filterNot { it.id == operadorId }
        operadoresNaoLidos = operadoresNaoLidos.filterNot { it.id == operadorId }

        if (estaEmNaoLidos) {
            // VOLTA PARA A LISTA NORMAL
            operadoresHistory = (operadoresHistory + operador.copy(
                isUnread = false,
                isPinned = false
            )).sortedBy { c -> ordemOperadoresOriginal.indexOfFirst { it.id == c.id } }
        } else {
            // VAI PARA NÃO LIDOS
            operadoresNaoLidos = (operadoresNaoLidos + operador.copy(
                isUnread = true,
                isPinned = false
            )).sortedBy { c -> ordemOperadoresOriginal.indexOfFirst { it.id == c.id } }
        }
    }

    fun togglePin(operadorId: Int) {
        val operador = (operadoresHistory + operadoresFixados + operadoresNaoLidos)
            .find { it.id == operadorId } ?: return

        val estaFixado = operadoresFixados.any { it.id == operadorId }

        operadoresHistory = operadoresHistory.filterNot { it.id == operadorId }
        operadoresFixados = operadoresFixados.filterNot { it.id == operadorId }
        operadoresNaoLidos = operadoresNaoLidos.filterNot { it.id == operadorId }

        if (estaFixado) {
            operadoresHistory = (operadoresHistory + operador.copy(
                isPinned = false,
                isUnread = false
            )).sortedBy { c -> ordemOperadoresOriginal.indexOfFirst { it.id == c.id } }
        } else {
            operadoresFixados = (operadoresFixados + operador.copy(
                isPinned = true,
                isUnread = false
            )).sortedBy { c -> ordemOperadoresOriginal.indexOfFirst { it.id == c.id } }
        }
    }
}
