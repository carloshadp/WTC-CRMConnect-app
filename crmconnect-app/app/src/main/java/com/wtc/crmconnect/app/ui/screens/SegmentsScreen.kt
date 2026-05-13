package com.wtc.crmconnect.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wtc.crmconnect.app.data.remote.dto.segment.SegmentResponseDto
import com.wtc.crmconnect.app.viewmodel.SegmentsEvent
import com.wtc.crmconnect.app.viewmodel.SegmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentsScreen(
    navController: NavController,
    viewModel: SegmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val laranja = Color(0xFFF07D29)
    val backgroundColor = Color(0xFF22394E)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SegmentsEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is SegmentsEvent.ShowInfo -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Segmentos",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { BottomNavigationBar(navController, activeScreen = "segments_screen") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = laranja,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo segmento")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = laranja,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.segments.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhum segmento criado.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontFamily = Poppins,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Toque em + para criar o primeiro.",
                            color = Color.White.copy(alpha = 0.4f),
                            fontFamily = Poppins,
                            fontSize = 12.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
                    ) {
                        items(uiState.segments, key = { it.id }) { segment ->
                            SegmentCard(
                                segment = segment,
                                onEdit = { viewModel.showEditDialog(segment) },
                                onDelete = { viewModel.showDeleteConfirm(segment.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showCreateDialog || uiState.editingSegment != null) {
        SegmentFormDialog(
            title = if (uiState.editingSegment != null) "Editar segmento" else "Novo segmento",
            name = uiState.formName,
            description = uiState.formDescription,
            isSaving = uiState.isSaving,
            onNameChange = viewModel::onNameChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onConfirm = { viewModel.save() },
            onDismiss = { viewModel.dismissDialog() }
        )
    }

    uiState.deleteConfirmId?.let {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirm() },
            title = { Text("Excluir segmento", fontFamily = Poppins, fontWeight = FontWeight.SemiBold) },
            text = { Text("Tem certeza? Esta ação não pode ser desfeita.", fontFamily = Poppins) },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Excluir", color = Color.White, fontFamily = Poppins)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirm() }) {
                    Text("Cancelar", fontFamily = Poppins)
                }
            }
        )
    }
}

@Composable
private fun SegmentCard(
    segment: SegmentResponseDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D4A5E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = segment.name,
                    color = Color.White,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                if (!segment.description.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = segment.description,
                        color = Color.White.copy(alpha = 0.6f),
                        fontFamily = Poppins,
                        fontSize = 12.sp
                    )
                }
                segment.createdAt?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Criado em: ${it.take(10)}",
                        color = Color.White.copy(alpha = 0.4f),
                        fontFamily = Poppins,
                        fontSize = 11.sp
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFFF07D29),
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SegmentFormDialog(
    title: String,
    name: String,
    description: String,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFFF07D29),
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = Color(0xFFF07D29),
        unfocusedLabelColor = Color.Gray,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontFamily = Poppins, fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nome *", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isSaving,
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Descrição (opcional)", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    enabled = !isSaving,
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSaving && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07D29))
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text("Salvar", color = Color.White, fontFamily = Poppins)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancelar", fontFamily = Poppins)
            }
        }
    )
}
