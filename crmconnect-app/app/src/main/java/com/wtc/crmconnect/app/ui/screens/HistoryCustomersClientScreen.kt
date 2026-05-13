package com.wtc.crmconnect.app.ui.screens



import com.wtc.crmconnect.app.viewmodel.OperadoresViewModel
import com.wtc.crmconnect.app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.material.TextFieldDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wtc.crmconnect.app.model.Operador
import kotlin.math.roundToInt

@Composable
fun HistoryCostumersClientScreen(
    navController: NavController,
    operadoresViewModel: OperadoresViewModel
) {
    val swipeOffsetMapFixados = remember { mutableStateMapOf<Int, Float>() }
    val swipeOffsetMapNormais = remember { mutableStateMapOf<Int, Float>() }
    val swipeOffsetMapUnreads = remember { mutableStateMapOf<Int, Float>() }
    val swipeOffsetMapGrupos = remember { mutableStateMapOf<Int, Float>() }

    val listState = rememberLazyListState()

    // FILTRAGEM OPERADORES
    val operadoresFixadosFiltrados = if (operadoresViewModel.searchText.isBlank()) {
        operadoresViewModel.operadoresFixados
    } else {
        operadoresViewModel.operadoresFixados.filter {
            it.name.contains(operadoresViewModel.searchText, ignoreCase = true)
        }
    }

    val operadoresNaoLidosFiltrados = if (operadoresViewModel.searchText.isBlank()) {
        operadoresViewModel.operadoresNaoLidos
    } else {
        operadoresViewModel.operadoresNaoLidos.filter {
            it.name.contains(operadoresViewModel.searchText, ignoreCase = true)
        }
    }

    val operadoresNormaisFiltrados = if (operadoresViewModel.searchText.isBlank()) {
        operadoresViewModel.operadoresHistory.filter { c ->
            !operadoresViewModel.operadoresFixados.contains(c) &&
                    !operadoresViewModel.operadoresNaoLidos.contains(c)
        }
    } else {
        operadoresViewModel.operadoresHistory.filter {
            it.name.contains(operadoresViewModel.searchText, ignoreCase = true)
        }
    }

    // FILTRAGEM GRUPOS
    val gruposOperadoresFixadosFiltrados = if (operadoresViewModel.searchText.isBlank()) {
        operadoresViewModel.gruposOperadoresFixados
    } else {
        operadoresViewModel.gruposOperadoresFixados.filter {
            it.nome.contains(operadoresViewModel.searchText, ignoreCase = true) ||
                    it.operadores.any { c ->
                        c.nome.contains(operadoresViewModel.searchText, ignoreCase = true)
                    }
        }
    }

    val gruposOperadoresNaoLidosFiltrados = if (operadoresViewModel.searchText.isBlank()) {
        operadoresViewModel.gruposOperadoresNaoLidos
    } else {
        operadoresViewModel.gruposOperadoresNaoLidos.filter {
            it.nome.contains(operadoresViewModel.searchText, ignoreCase = true) ||
                    it.operadores.any { c ->
                        c.nome.contains(operadoresViewModel.searchText, ignoreCase = true)
                    }
        }
    }

    val gruposOperadoresNormaisFiltrados = if (operadoresViewModel.searchText.isBlank()) {
        operadoresViewModel.grupos
    } else {
        operadoresViewModel.grupos.filter {
            it.nome.contains(operadoresViewModel.searchText, ignoreCase = true) ||
                    it.operadores.any { c ->
                        c.nome.contains(operadoresViewModel.searchText, ignoreCase = true)
                    }
        }
    }

    Scaffold(
        backgroundColor = Color.White,
        bottomBar = {
            ClienteBottomNavigationBar(
                navController = navController,
                activeScreen = "message_storage_client_screen"
            )
        }
    ) { innerPadding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            // BARRA DE BUSCA
            stickyHeader {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(top = 42.dp, bottom = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        elevation = 8.dp
                    ) {
                        OutlinedTextField(
                            value = operadoresViewModel.searchText,
                            onValueChange = { operadoresViewModel.onSearchTextChanged(it) },
                            placeholder = {
                                Text(
                                    "Pesquisar",
                                    color = Color.Gray,
                                    fontFamily = Poppins
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_search),
                                    contentDescription = "Ícone de busca",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Gray
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor = Color.White,
                                focusedBorderColor = Color.Gray,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                Text(
                    text = "RECEBIDOS",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 8.dp)
                )
            }

            // GRUPOS FIXADOS
            if (gruposOperadoresFixadosFiltrados.isNotEmpty()) {
                stickyHeader { SectionClientHeader("GRUPOS FIXADOS") }
                itemsIndexed(
                    gruposOperadoresFixadosFiltrados,
                    key = { _, grupo -> grupo.id }) { index, grupo ->
                    GrupoOperadorListItem(
                        grupo = grupo,
                        isNaoLido = false,
                        onPin = { operadoresViewModel.togglePinGrupo(grupo.id) },
                        onMarkUnread = { operadoresViewModel.markUnreadGrupo(grupo.id) },
                        swipeOffsetMap = swipeOffsetMapGrupos,
                        showDivider = index != gruposOperadoresFixadosFiltrados.lastIndex,
                        onClick = {
                            operadoresViewModel.selecionarGrupoOperador(grupo)
                            navController.navigate("message_storage_client_screen")
                        }
                    )
                }
            }

            // OPERADORES FIXADOS
            if (operadoresFixadosFiltrados.isNotEmpty()) {
                stickyHeader { SectionClientHeader("OPERADORES FIXADOS") }
                itemsIndexed(operadoresFixadosFiltrados) { index, operador ->
                    OperadorListItem(
                        operador = operador,
                        onPin = { operadoresViewModel.togglePin(operador.id) },
                        onMarkUnread = { operadoresViewModel.markUnread(operador.id) },
                        swipeOffsetMap = swipeOffsetMapFixados,
                        showDivider = index != operadoresFixadosFiltrados.lastIndex,
                        onClick = {
                            operadoresViewModel.selecionarGrupoOperador(null)
                            navController.navigate("message_storage_client_screen")
                        }
                    )
                }
            }

            // GRUPOS NÃO LIDOS
            if (gruposOperadoresNaoLidosFiltrados.isNotEmpty()) {
                stickyHeader { SectionClientHeader("GRUPOS NÃO LIDOS") }
                itemsIndexed(
                    gruposOperadoresNaoLidosFiltrados,
                    key = { _, grupo -> grupo.id }) { index, grupo ->
                    GrupoOperadorListItem(
                        grupo = grupo,
                        isNaoLido = true,
                        onPin = { operadoresViewModel.togglePinGrupo(grupo.id) },
                        onMarkUnread = { operadoresViewModel.markUnreadGrupo(grupo.id) },
                        swipeOffsetMap = swipeOffsetMapGrupos,
                        showDivider = index != gruposOperadoresNaoLidosFiltrados.lastIndex,
                        onClick = {
                            operadoresViewModel.selecionarGrupoOperador(grupo)
                            navController.navigate("message_storage_client_screen")
                        }
                    )
                }
            }

            // OPERADORES NÃO LIDOS
            if (operadoresNaoLidosFiltrados.isNotEmpty()) {
                stickyHeader { SectionClientHeader("OPERADORES NÃO LIDOS") }
                itemsIndexed(operadoresNaoLidosFiltrados) { index, operador ->
                    OperadorListItem(
                        operador = operador,
                        onPin = { operadoresViewModel.togglePin(operador.id) },
                        onMarkUnread = { operadoresViewModel.markUnread(operador.id) },
                        swipeOffsetMap = swipeOffsetMapUnreads,
                        showDivider = index != operadoresNaoLidosFiltrados.lastIndex,
                        onClick = {
                            operadoresViewModel.selecionarGrupoOperador(null)
                            navController.navigate("message_storage_client_screen")
                        }
                    )
                }
            }

            // GRUPOS NORMAIS
            if (gruposOperadoresNormaisFiltrados.isNotEmpty()) {
                stickyHeader { SectionClientHeader("GRUPOS") }
                itemsIndexed(
                    gruposOperadoresNormaisFiltrados,
                    key = { _, grupo -> grupo.id }) { index, grupo ->
                    GrupoOperadorListItem(
                        grupo = grupo,
                        isNaoLido = false,
                        onPin = { operadoresViewModel.togglePinGrupo(grupo.id) },
                        onMarkUnread = { operadoresViewModel.markUnreadGrupo(grupo.id) },
                        swipeOffsetMap = swipeOffsetMapGrupos,
                        showDivider = index != gruposOperadoresNormaisFiltrados.lastIndex,
                        onClick = {
                            operadoresViewModel.selecionarGrupoOperador(grupo)
                            navController.navigate("message_storage_client_screen")
                        }
                    )
                }
            }

            // OPERADORES NORMAIS
            if (operadoresNormaisFiltrados.isNotEmpty()) {
                stickyHeader { SectionClientHeader("OPERADORES") }
                itemsIndexed(operadoresNormaisFiltrados) { index, operador ->
                    OperadorListItem(
                        operador = operador,
                        onPin = { operadoresViewModel.togglePin(operador.id) },
                        onMarkUnread = { operadoresViewModel.markUnread(operador.id) },
                        swipeOffsetMap = swipeOffsetMapNormais,
                        showDivider = index != operadoresNormaisFiltrados.lastIndex,
                        onClick = {
                            operadoresViewModel.selecionarGrupoOperador(null)
                            navController.navigate("message_storage_client_screen")
                        }
                    )
                }
            }
        }
    }
}

// HEADER
@Composable
fun SectionClientHeader(title: String) {
    Text(
        text = title,
        fontFamily = Poppins,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// OPERADOR
@Composable
fun OperadorImage(
    imageRes: Int,
    circleSize: Dp = 48.dp,
    imageSize: Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(circleSize)
            .clip(CircleShape)
            .border(2.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Foto do Operador",
            modifier = Modifier.size(imageSize)
        )
    }
}

@Composable
fun OperadorDividerLine() {
    Divider(
        color = Color(0xFF0810A5),
        thickness = 1.dp,
        modifier = Modifier.padding(start = 0.dp)
    )
}

@Composable
fun OperadorListItem(
    operador: Operador,
    onPin: () -> Unit,
    onMarkUnread: () -> Unit,
    swipeOffsetMap: MutableMap<Int, Float>,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current

    val buttonWidthDp = 80.dp
    val maxOffsetPx = with(density) { buttonWidthDp.toPx() * 2 }
    val offsetX = swipeOffsetMap.getOrPut(operador.id) { 0f }

    Column(modifier = Modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {

            // BOTÕES
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {

                // NÃO LIDO / LIDO
                Box(
                    modifier = Modifier
                        .width(buttonWidthDp)
                        .fillMaxHeight()
                        .background(Color(0xFF22394E))
                        .clickable {
                            onMarkUnread()
                            swipeOffsetMap[operador.id] = 0f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))

                        Image(
                            painter = painterResource(id = R.drawable.ic_unread),
                            contentDescription = "Não lido",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = if (operador.isUnread) "Lido" else "Não Lido",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // FIXAR / DESAFIXAR
                Box(
                    modifier = Modifier
                        .width(buttonWidthDp)
                        .fillMaxHeight()
                        .background(Color(0xFFF07D29))
                        .clickable {
                            onPin()
                            swipeOffsetMap[operador.id] = 0f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = "Fixar",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (operador.isPinned) "Desafixar" else "Fixar",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // CONTEÚDO PRINCIPAL
            Row(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable { onClick() }
                    .pointerInput(operador.id) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newOffset = (swipeOffsetMap[operador.id] ?: 0f) + dragAmount
                                swipeOffsetMap[operador.id] = newOffset.coerceIn(0f, maxOffsetPx)
                            },
                            onDragEnd = {
                                val finalOffset = swipeOffsetMap[operador.id] ?: 0f
                                swipeOffsetMap[operador.id] =
                                    if (finalOffset > maxOffsetPx / 2) maxOffsetPx else 0f
                            }
                        )
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OperadorImage(imageRes = operador.imageRes)
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = operador.name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = operador.message,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
                if (operador.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0810A5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = operador.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (showDivider) {
            OperadorDividerLine()
        }
    }
}

// GRUPO
@Composable
fun GrupoOperadorListItem(
    grupo: OperadoresViewModel.GrupoOperador,
    isNaoLido: Boolean,
    onPin: () -> Unit,
    onMarkUnread: () -> Unit,
    swipeOffsetMap: MutableMap<Int, Float>,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    val density = LocalDensity.current

    val buttonWidthDp = 80.dp
    val maxOffsetPx = with(density) { buttonWidthDp.toPx() * 2 }
    val offsetX = swipeOffsetMap.getOrPut(grupo.id) { 0f }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {

                // NÃO LIDO / LIDO
                Box(
                    modifier = Modifier
                        .width(buttonWidthDp)
                        .fillMaxHeight()
                        .background(Color(0xFF22394E))
                        .clickable {
                            onMarkUnread()
                            swipeOffsetMap[grupo.id] = 0f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))

                        Image(
                            painter = painterResource(id = R.drawable.ic_unread),
                            contentDescription = "Não lido",
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = if (isNaoLido) "Lido" else "Não Lido",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // FIXAR / DESAFIXAR
                Box(
                    modifier = Modifier
                        .width(buttonWidthDp)
                        .fillMaxHeight()
                        .background(Color(0xFFF07D29))
                        .clickable {
                            onPin()
                            swipeOffsetMap[grupo.id] = 0f
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pin),
                            contentDescription = "Fixar",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (grupo.isPinned) "Desafixar" else "Fixar",
                            fontSize = 14.sp,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // CONTEÚDO PRINCIPAL
            Row(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable { onClick() }
                    .pointerInput(grupo.id) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                val newOffset = (swipeOffsetMap[grupo.id] ?: 0f) + dragAmount
                                swipeOffsetMap[grupo.id] = newOffset.coerceIn(0f, maxOffsetPx)
                            },
                            onDragEnd = {
                                val finalOffset = swipeOffsetMap[grupo.id] ?: 0f
                                swipeOffsetMap[grupo.id] =
                                    if (finalOffset > maxOffsetPx / 2) maxOffsetPx else 0f
                            }
                        )
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chat_grupo),
                        contentDescription = "Grupo",
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = grupo.nome,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "+${grupo.operadores.size} operadores",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        if (showDivider) {
            OperadorDividerLine()
        }
    }
}
