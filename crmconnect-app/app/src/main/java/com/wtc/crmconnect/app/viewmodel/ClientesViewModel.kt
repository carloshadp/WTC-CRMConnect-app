package com.wtc.crmconnect.app.viewmodel



import com.wtc.crmconnect.app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.wtc.crmconnect.app.model.Cliente
import com.wtc.crmconnect.app.model.GrupoCliente

class ClientesViewModel : ViewModel() {

    // LISTA DE CLIENTES - HistoryCustomersScreen
    var clientesHistory by mutableStateOf(
        listOf(
            Cliente(1, R.drawable.ic_chat_client, "Cliente A", "Lorem ipsum dolor sit amet.", 2),
            Cliente(2, R.drawable.ic_chat_client, "Cliente B", "Consectetur adipiscing elit.", 3),
            Cliente(3, R.drawable.ic_chat_client, "Cliente C", "Sed do eiusmod tempor.", 5),
        )
    )
        private set

    // LISTA DE CLIENTES - ChatNewGroupScreen
    var clientesChat by mutableStateOf(
        listOf(
            GrupoCliente(1, R.drawable.ic_chat_client, "Cliente A"),
            GrupoCliente(2, R.drawable.ic_chat_client, "Cliente B"),
            GrupoCliente(3, R.drawable.ic_chat_client, "Cliente C"),
        )
    )
        private set

    private val ordemOriginal = listOf(
        Cliente(1, R.drawable.ic_chat_client, "Cliente A", "Lorem ipsum dolor sit amet.", 2),
        Cliente(2, R.drawable.ic_chat_client, "Cliente B", "Consectetur adipiscing elit.", 3),
        Cliente(3, R.drawable.ic_chat_client, "Cliente C", "Sed do eiusmod tempor.", 5),
    )

    // LISTA DE CLIENTES FIXADOS
    var clientesFixados by mutableStateOf(listOf<Cliente>())
        private set

    // LISTA DE CLIENTES NÃO LIDOS
    var clientesNaoLidos by mutableStateOf(listOf<Cliente>())
        private set

    // LISTA DE GRUPOS
    var grupos by mutableStateOf(listOf<Grupo>())
        private set

    var grupoSelecionado by mutableStateOf<Grupo?>(null)
        private set

    fun selecionarGrupo(grupo: Grupo?) {
        grupoSelecionado = grupo
    }

    // GRUPO DE CLIENTES
    data class Grupo(
        val id: Int,
        val nome: String,
        val clientes: List<GrupoCliente>,
        val isPinned: Boolean = false,
        val isUnread: Boolean = false,
        val unreadCount: Int = 0,
        var noteText: String = ""
    )

    var gruposFixados by mutableStateOf(listOf<Grupo>())
        private set

    var gruposNaoLidos by mutableStateOf(listOf<Grupo>())
        private set

    fun markUnreadGrupo(grupoId: Int) {

        val estaEmNaoLidos = gruposNaoLidos.any { it.id == grupoId }

        if (estaEmNaoLidos) {
            val grupo = gruposNaoLidos.find { it.id == grupoId } ?: return

            gruposNaoLidos = gruposNaoLidos.filterNot { it.id == grupoId }

            grupos = (grupos + grupo.copy(
                unreadCount = 0,
                isPinned = false
            )).sortedBy { it.nome }

        } else {
            val grupo = (grupos + gruposFixados)
                .find { it.id == grupoId } ?: return

            grupos = grupos.filterNot { it.id == grupoId }
            gruposFixados = gruposFixados.filterNot { it.id == grupoId }

            gruposNaoLidos = (gruposNaoLidos + grupo.copy(
                unreadCount = 1,
                isPinned = false
            )).sortedBy { it.nome }
        }
    }

    fun togglePinGrupo(grupoId: Int) {

        val grupo = (grupos + gruposFixados + gruposNaoLidos)
            .find { it.id == grupoId } ?: return

        val estaFixado = gruposFixados.any { it.id == grupoId }

        // REMOVE DE TODAS
        grupos = grupos.filterNot { it.id == grupoId }
        gruposFixados = gruposFixados.filterNot { it.id == grupoId }
        gruposNaoLidos = gruposNaoLidos.filterNot { it.id == grupoId }

        if (estaFixado) {
            // DESAFIXAR
            grupos = (grupos + grupo.copy(
                isPinned = false,
                unreadCount = 0
            )).sortedBy { it.nome }

        } else {
            // FIXAR (REMOVE DE NÃO LIDOS)
            gruposFixados = (gruposFixados + grupo.copy(
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

    fun markUnread(clienteId: Int) {

        val estaEmNaoLidos = clientesNaoLidos.any { it.id == clienteId }

        if (estaEmNaoLidos) {
            // VOLTA PARA A LISTA NORMAL
            val cliente = clientesNaoLidos.find { it.id == clienteId } ?: return

            clientesNaoLidos = clientesNaoLidos.filterNot { it.id == clienteId }

            clientesHistory = (clientesHistory + cliente.copy(
                isUnread = false,
                isPinned = false
            )).sortedBy { c -> ordemOriginal.indexOfFirst { it.id == c.id } }

        } else {
            // VAI PARA NÃO LIDOS
            val cliente = (clientesHistory + clientesFixados)
                .find { it.id == clienteId } ?: return

            clientesHistory = clientesHistory.filterNot { it.id == clienteId }
            clientesFixados = clientesFixados.filterNot { it.id == clienteId }

            clientesNaoLidos = (clientesNaoLidos + cliente.copy(
                isUnread = true,
                isPinned = false
            )).sortedBy { c -> ordemOriginal.indexOfFirst { it.id == c.id } }
        }
    }

    fun togglePin(clienteId: Int) {

        // BUSCA EM TODAS AS LISTAS
        val cliente = (clientesHistory + clientesFixados + clientesNaoLidos)
            .find { it.id == clienteId } ?: return

        val estaFixado = clientesFixados.any { it.id == clienteId }

        // REMOVE DE TODAS AS LISTAS
        clientesHistory = clientesHistory.filterNot { it.id == clienteId }
        clientesFixados = clientesFixados.filterNot { it.id == clienteId }
        clientesNaoLidos = clientesNaoLidos.filterNot { it.id == clienteId }

        if (estaFixado) {
            // DESAFIXAR (VOLTA PARA A LISTA NORMAL)
            clientesHistory = (clientesHistory + cliente.copy(
                isPinned = false,
                isUnread = false
            )).sortedBy { c -> ordemOriginal.indexOfFirst { it.id == c.id } }

        } else {
            // FIXAR (SEMPRE REMOVE DE NÃO LIDOS)
            clientesFixados = (clientesFixados + cliente.copy(
                isPinned = true,
                isUnread = false
            )).sortedBy { c -> ordemOriginal.indexOfFirst { it.id == c.id } }
        }
    }

    fun criarGrupo(nome: String, clientesSelecionados: List<GrupoCliente>) {
        if (nome.isBlank() || clientesSelecionados.size < 2) return

        val maxId = (grupos + gruposFixados + gruposNaoLidos).maxOfOrNull { it.id } ?: 0

        val novoGrupo = Grupo(
            id = maxId + 1,
            nome = nome,
            clientes = clientesSelecionados
        )

        grupos = (grupos + novoGrupo).sortedBy { it.nome }

        grupoSelecionado = novoGrupo
    }
}
