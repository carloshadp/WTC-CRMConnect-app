package com.wtc.crmconnect.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun TextMessageScreen(navController: NavController) {

    val branco = Color.White
    val laranja = Color(0xFFF07D29)
    val azulEscuro = Color(0xFF22394E)
    val cinzaClaro = Color(0xFFF6F6F6)
    val cinzaLinha = Color(0xFFDDDDDD)
    val textoCinza = Color(0xFF757575)

    var nomeGrupo by remember { mutableStateOf("") }
    var incluirBotoes by remember { mutableStateOf(true) }

    val tags = listOf("Campanha", "Evento", "Promoção", "Comunicado")
    var tagSelecionada by remember { mutableStateOf("Tags") }

    val fontes = listOf("Arial", "Times New Roman", "Roboto")
    var fonteSelecionada by remember { mutableStateOf("Fonte") }

    val quantidades = listOf(1, 2, 3, 4)
    var qtdSelecionada by remember { mutableStateOf(2) }

    val botoesConteudo = remember { mutableStateListOf("", "", "", "") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        backgroundColor = branco,
        bottomBar = { BottomNavigationBar(navController, activeScreen = "text_message_screen") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(branco)
                .padding(inner)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(50.dp))
            Text(
                text = "Escreva seu anúncio",
                color = Color.Black,
                fontSize = 20.sp,
                fontFamily = Poppins,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(8.dp))
            Divider(color = Color(0x33000000), thickness = 6.dp)
            Spacer(Modifier.height(50.dp))

            // CAMPO EMAIL / GRUPO
            OutlinedTextField(
                value = nomeGrupo,
                onValueChange = { nomeGrupo = it },
                placeholder = {
                    Text(
                        "Nome do Grupo ou E-mail",
                        fontFamily = Poppins,
                        color = textoCinza
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Pesquisar",
                        tint = textoCinza
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(branco, RoundedCornerShape(12.dp))
                    .shadow(0.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = cinzaLinha,
                    unfocusedBorderColor = cinzaLinha,
                    cursorColor = laranja,
                    textColor = Color.Black,
                    backgroundColor = branco
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(20.dp))

            // TAG E FONTE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnderlineDropdown(
                    selectedText = tagSelecionada,
                    options = tags,
                    onSelect = { tagSelecionada = it },
                    modifier = Modifier.width(130.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                UnderlineDropdown(
                    selectedText = fonteSelecionada,
                    options = fontes,
                    onSelect = { fonteSelecionada = it },
                    modifier = Modifier.width(150.dp)
                )
            }

            Spacer(Modifier.height(50.dp))

            // CHECKBOX E QUANTIDADE DE BOTÕES
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = incluirBotoes,
                        onCheckedChange = { incluirBotoes = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = laranja,
                            checkmarkColor = Color.White,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text("Botões", fontFamily = Poppins, color = Color.Black, fontSize = 16.sp)
                }

                UnderlineDropdown(
                    selectedText = qtdSelecionada.toString(),
                    options = quantidades.map { it.toString() },
                    onSelect = { qtdSelecionada = it.toInt() },
                    modifier = Modifier.width(70.dp)
                )
            }

            Spacer(Modifier.height(10.dp))
            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp)

            Spacer(Modifier.height(20.dp))

            if (incluirBotoes) {
                repeat(qtdSelecionada) { index ->
                    OutlinedTextField(
                        value = botoesConteudo[index],
                        onValueChange = { botoesConteudo[index] = it },
                        placeholder = {
                            Text(
                                "${index + 1}. Escreva o conteúdo de botão",
                                fontFamily = Poppins,
                                color = textoCinza
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cinzaClaro, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            cursorColor = laranja,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            backgroundColor = cinzaClaro
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(50.dp))

            // BOTÃO AVANÇAR
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val camposValidos =
                            nomeGrupo.isNotBlank() &&
                                    tagSelecionada != "Tags" &&
                                    fonteSelecionada != "Fonte" &&
                                    (!incluirBotoes || botoesConteudo.take(qtdSelecionada)
                                        .all { it.isNotBlank() })

                        if (camposValidos) {
                            val botoesStr = botoesConteudo.take(qtdSelecionada)
                                .filter { it.isNotBlank() }
                                .joinToString(",") { it.replace(",", ";") }

                            val emailEnc =
                                URLEncoder.encode(nomeGrupo, StandardCharsets.UTF_8.toString())
                            val tagEnc =
                                URLEncoder.encode(tagSelecionada, StandardCharsets.UTF_8.toString())
                            val fonteEnc = URLEncoder.encode(
                                fonteSelecionada,
                                StandardCharsets.UTF_8.toString()
                            )
                            val botoesEnc =
                                URLEncoder.encode(botoesStr, StandardCharsets.UTF_8.toString())

                            val rota = "writeAdScreen/$emailEnc/$tagEnc/$fonteEnc/$botoesEnc"
                            navController.navigate(rota)
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("É necessário preencher todos os campos.")
                            }
                        }
                    },
                    modifier = Modifier
                        .width(121.dp)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF22394E)),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        "Avançar", color = branco, fontFamily = Poppins,
                        fontWeight = FontWeight.Medium, fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun UnderlineDropdown(
    selectedText: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedText,
                fontFamily = Poppins,
                fontSize = 15.sp,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Abrir opções",
                tint = Color.Black
            )
        }

        Divider(color = Color(0xFF999999), thickness = 1.dp)

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onSelect(option)
                    expanded = false
                }) {
                    Text(option, fontFamily = Poppins)
                }
            }
        }
    }
}