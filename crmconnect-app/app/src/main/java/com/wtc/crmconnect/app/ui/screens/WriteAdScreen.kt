package com.wtc.crmconnect.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

@Composable
fun WriteAdScreen(
    navController: NavController,
    userEmail: String,
    tag: String,
    fonte: String,
    botoes: String
) {
    val backgroundColor = Color.White
    val azulEscuro = Color(0xFF22394E)
    val laranja = Color(0xFFF07D29)

    var titulo by remember { mutableStateOf("") }
    var conteudo by remember { mutableStateOf("") }
    var arquivoUri by remember { mutableStateOf<Uri?>(null) }
    var arquivoNome by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        arquivoUri = uri
        arquivoNome = uri?.lastPathSegment
    }

    Scaffold(
        backgroundColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = (-12).dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Escreva seu anúncio",
                    color = Color.Black,
                    style = MaterialTheme.typography.h6
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    focusedBorderColor = laranja,
                    unfocusedBorderColor = azulEscuro,
                    cursorColor = laranja
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = conteudo,
                onValueChange = { conteudo = it },
                label = { Text("Escreva o conteúdo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    focusedBorderColor = laranja,
                    unfocusedBorderColor = azulEscuro,
                    cursorColor = laranja
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "#$tag",
                color = laranja,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            if (botoes.isNotBlank()) {
                Text("Botões selecionados:", color = azulEscuro)
                Spacer(Modifier.height(4.dp))
                botoes.split(",").forEachIndexed { index, botao ->
                    Text("${index + 1}. $botao", color = Color.Gray)
                }
                Spacer(Modifier.height(12.dp))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { filePickerLauncher.launch("*/*") },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = azulEscuro),
                    border = ButtonDefaults.outlinedBorder,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Attachment,
                        contentDescription = "Arquivo",
                        tint = azulEscuro
                    )
                    Spacer(Modifier.width(8.dp))

                    Text("Arquivo")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (titulo.isBlank() || conteudo.isBlank()) {
                                snackbarHostState.showSnackbar("Preencha todos os campos!")
                                return@launch
                            }

                            // Chamada atualizada para a nova função
                            enviarCampanhaBackend(
                                emailDestino = userEmail,
                                titulo = titulo,
                                conteudo = conteudo,
                                tag = tag,
                                botoes = botoes,
                                arquivoNome = arquivoNome
                            ) { sucesso ->
                                coroutineScope.launch {
                                    if (sucesso) {
                                        snackbarHostState.showSnackbar("Anúncio enviado com sucesso!")
                                        delay(1500)
                                        navController.navigate("home_screen") {
                                            popUpTo("home_screen") { inclusive = true }
                                        }
                                    } else {
                                        snackbarHostState.showSnackbar("Erro ao conectar com o servidor.")
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = azulEscuro),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)

                    Spacer(Modifier.width(8.dp))

                    Text("Enviar", color = Color.White)
                }
            }

            arquivoNome?.let {
                Spacer(Modifier.height(8.dp))

                Text("Arquivo selecionado: $it", color = Color.Gray)
            }
        }
    }
}

// ------------------------------------------------------------------
// ENVIO DE CAMPANHA USANDO O SEU SPRING BOOT BACKEND LOCAL
// ------------------------------------------------------------------
fun enviarCampanhaBackend(
    emailDestino: String,
    titulo: String,
    conteudo: String,
    tag: String,
    botoes: String,
    arquivoNome: String?,
    onResult: (Boolean) -> Unit
) {
    val client = OkHttpClient()

    val json = JSONObject().apply {
        put("email_destino", emailDestino)
        put("titulo", titulo)
        put("conteudo", conteudo)
        put("tag", tag)
        put("botoes", botoes)
        put("arquivo", arquivoNome ?: "Nenhum anexo")
    }

    val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    // URL ATUALIZADA PARA O SEU SERVIDOR LOCAL (EMULADOR)
    val request = Request.Builder()
        .url("http://10.0.2.2:8080/campaigns")
        .addHeader("Content-Type", "application/json")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onResult(false)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            println("📩 Spring Boot Response: ${response.code} - $responseBody")
            onResult(response.isSuccessful)
        }
    })
}