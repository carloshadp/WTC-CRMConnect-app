package com.wtc.crmconnect.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpClientScreen(
    navController: NavController,
    showDivider: Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FAQ", fontFamily = Poppins,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("edit_user_client_screen") {
                                popUpTo("help_client_screen") { inclusive = true }
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val faqs = listOf(
                "Posso alterar meus dados pessoais dentro do app?" to
                        "Resposta: Sim, vá em  “Editar Perfil” e edite suas informações.",
                "Posso criar anotações dentro do app?" to
                        "Resposta: Sim, vá em  “Minhas Anotações” e clique no botão  de + para criar sua anotação.",
                "Como faço para recuperar minha senha?" to
                        "Resposta: Basta clicar em “Esqueci minha senha” na tela de Login" +
                        " e seguir as instruções enviadas para seu e-mail cadastrado.",
                "As mensagens ficam salvas no histórico de conversas?" to
                        "Resposta: Sim, todas as conversas ficam registradas no histórico do app" +
                        " e podem ser acessadas a qualquer momento pelo usuário.",
                "Como faço para apagar uma mensagem enviada?" to
                        "Resposta: É simples, basta segurar sobre a mensagem que deseja excluir e," +
                        " em seguida, tocar no ícone de lixeira para apagar."
            )

            faqs.forEachIndexed { index, (pergunta, resposta) ->
                ExpandableFaqItem(pergunta, resposta)

                if (showDivider && index < faqs.size - 1) {
                    HelpDividerLine()
                }
            }
        }
    }
}

@Composable
fun HelpDividerLine() {
    Divider(
        color = Color(0xFF0810A5),
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ExpandableFaqItem(pergunta: String, resposta: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = pergunta,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = Poppins,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = if (expanded) "Recolher" else "Expandir"
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resposta,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
