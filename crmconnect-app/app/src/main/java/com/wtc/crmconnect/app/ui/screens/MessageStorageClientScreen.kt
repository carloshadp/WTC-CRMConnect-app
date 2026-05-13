package com.wtc.crmconnect.app.ui.screens

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wtc.crmconnect.app.R
import com.wtc.crmconnect.app.data.remote.dto.enums.SenderType
import com.wtc.crmconnect.app.viewmodel.ChatClientEvent
import com.wtc.crmconnect.app.viewmodel.ChatClientViewModel

@Composable
fun MessageStorageClientScreen(
    navController: NavController,
    viewModel: ChatClientViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var textoDigitado by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFF22394E)
    val preto = Color.Black
    val laranja = Color(0xFFF07D29)
    val branco = Color.White

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ChatClientEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // TOPO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { navController.navigateUp() },
                            modifier = Modifier.offset(x = (-14).dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Atendimento",
                            color = Color.White,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notification),
                        contentDescription = "Notificações",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White, thickness = 2.dp, modifier = Modifier.fillMaxWidth())

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (uiState.isLoading && uiState.messages.isEmpty()) {
                        CircularProgressIndicator(
                            color = laranja,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (uiState.messages.isEmpty()) {
                        Text(
                            text = "Você ainda não tem mensagens. Envie a primeira para iniciar uma conversa.",
                            color = Color.White,
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 32.dp)
                        )
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .padding(bottom = 72.dp),
                            verticalArrangement = Arrangement.spacedBy(28.dp)
                        ) {
                            items(uiState.messages, key = { it.id }) { message ->
                                MessageBubble(
                                    message = message,
                                    isMine = message.senderType == SenderType.CLIENTE,
                                    bubbleColorMine = laranja,
                                    bubbleColorOther = preto,
                                    textColor = branco
                                )
                            }
                        }
                    }
                }
            }

            // BOTTOM INPUT
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xFF1B2E3C))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(
                    LocalTextSelectionColors provides TextSelectionColors(
                        handleColor = laranja,
                        backgroundColor = Color(0x80F07D29)
                    )
                ) {
                    OutlinedTextField(
                        value = textoDigitado,
                        onValueChange = { textoDigitado = it },
                        modifier = Modifier
                            .weight(1f)
                            .onPreviewKeyEvent { keyEvent ->
                                if (keyEvent.nativeKeyEvent.keyCode == AndroidKeyEvent.KEYCODE_ENTER &&
                                    keyEvent.nativeKeyEvent.action == AndroidKeyEvent.ACTION_UP
                                ) {
                                    val final = textoDigitado.trim()
                                    if (final.isNotBlank()) {
                                        viewModel.send(final)
                                        textoDigitado = ""
                                    }
                                    true
                                } else false
                            },
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text("Digite sua mensagem...", fontFamily = Poppins) },
                        singleLine = false,
                        maxLines = 5,
                        enabled = !uiState.isSending,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedBorderColor = laranja,
                            unfocusedBorderColor = Color(0x80F07D29),
                            cursorColor = laranja,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val final = textoDigitado.trim()
                        if (final.isNotBlank()) {
                            viewModel.send(final)
                            textoDigitado = ""
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = laranja),
                    contentPadding = PaddingValues(8.dp),
                    enabled = !uiState.isSending && textoDigitado.isNotBlank()
                ) {
                    if (uiState.isSending) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
