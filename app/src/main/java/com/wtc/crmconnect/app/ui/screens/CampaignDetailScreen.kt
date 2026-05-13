package com.wtc.crmconnect.app.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wtc.crmconnect.app.data.remote.dto.enums.CampaignStatus
import com.wtc.crmconnect.app.viewmodel.CampaignDetailEvent
import com.wtc.crmconnect.app.viewmodel.CampaignDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignDetailScreen(
    navController: NavController,
    viewModel: CampaignDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val laranja = Color(0xFFF07D29)
    val backgroundColor = Color(0xFF22394E)

    var showScheduleDialog by remember { mutableStateOf(false) }
    var showAbTestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CampaignDetailEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is CampaignDetailEvent.ShowInfo -> snackbarHostState.showSnackbar(event.message)
                is CampaignDetailEvent.NavigateBack -> navController.navigateUp()
            }
        }
    }

    val campaign = uiState.campaign
    val isEditable = uiState.isNew || campaign?.status == CampaignStatus.RASCUNHO

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isNew) "Nova campanha" else "Detalhe da campanha",
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            if (uiState.isLoading && campaign == null && !uiState.isNew) {
                CircularProgressIndicator(color = laranja, modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    campaign?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CampaignStatusChip(status = it.status)
                            Spacer(modifier = Modifier.size(8.dp))
                            it.scheduledAt?.let { sched ->
                                Text(
                                    text = "Agendada: $sched",
                                    color = Color.LightGray,
                                    fontFamily = Poppins,
                                    fontSize = 12.sp
                                )
                            }
                            it.sentAt?.let { sent ->
                                Text(
                                    text = "Enviada: $sent",
                                    color = Color.LightGray,
                                    fontFamily = Poppins,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        it.abTest?.let { ab ->
                            Text(
                                text = "A/B: ${ab.splitPercent}% var B${ab.variantBTitle?.let { " · $it" } ?: ""}",
                                color = Color.White,
                                fontFamily = Poppins,
                                fontSize = 12.sp
                            )
                        }
                    }

                    CampaignFormField(
                        label = "Título",
                        value = uiState.form.title,
                        onChange = viewModel::onTitleChange,
                        enabled = isEditable
                    )
                    CampaignFormField(
                        label = "Conteúdo",
                        value = uiState.form.content,
                        onChange = viewModel::onContentChange,
                        enabled = isEditable,
                        minLines = 4
                    )

                    SegmentDropdown(
                        segments = uiState.segments,
                        selectedSegmentId = uiState.form.segmentId,
                        onSelect = viewModel::onSegmentChange,
                        enabled = isEditable
                    )

                    if (!uiState.isNew) {
                        CampaignFormField(
                            label = "Deeplink",
                            value = uiState.form.deeplink,
                            onChange = viewModel::onDeeplinkChange,
                            enabled = false
                        )
                    }

                    if (isEditable) {
                        Button(
                            onClick = { viewModel.save() },
                            colors = ButtonDefaults.buttonColors(containerColor = laranja),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSaving
                        ) {
                            if (uiState.isSaving) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = if (uiState.isNew) "Criar campanha" else "Salvar alterações",
                                    color = Color.White,
                                    fontFamily = Poppins
                                )
                            }
                        }
                    }

                    if (campaign != null && !uiState.isNew) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
                        Text(
                            text = "Ações",
                            color = Color.White,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        val canSchedule = campaign.status == CampaignStatus.RASCUNHO
                        val canAb = campaign.status == CampaignStatus.RASCUNHO
                        val canSend = campaign.status == CampaignStatus.RASCUNHO
                        val canCancel = campaign.status == CampaignStatus.RASCUNHO ||
                            campaign.status == CampaignStatus.AGENDADA

                        ActionButton(
                            text = "Agendar envio",
                            enabled = canSchedule && !uiState.isActioning,
                            onClick = { showScheduleDialog = true }
                        )
                        ActionButton(
                            text = "Configurar A/B test",
                            enabled = canAb && !uiState.isActioning,
                            onClick = { showAbTestDialog = true }
                        )
                        ActionButton(
                            text = "Enviar agora",
                            enabled = canSend && !uiState.isActioning,
                            onClick = { viewModel.sendNow() },
                            primaryColor = laranja
                        )
                        ActionButton(
                            text = "Cancelar campanha",
                            enabled = canCancel && !uiState.isActioning,
                            onClick = { viewModel.cancel() },
                            primaryColor = Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }

    if (showScheduleDialog) {
        ScheduleDialog(
            onConfirm = { iso ->
                viewModel.schedule(iso)
                showScheduleDialog = false
            },
            onDismiss = { showScheduleDialog = false }
        )
    }

    if (showAbTestDialog) {
        AbTestDialog(
            onConfirm = { variantBContent, splitPercent, variantBTitle ->
                viewModel.configureAbTest(variantBContent, splitPercent, variantBTitle)
                showAbTestDialog = false
            },
            onDismiss = { showAbTestDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampaignFormField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    enabled: Boolean,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontFamily = Poppins) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color(0xFFE0E0E0),
            focusedBorderColor = Color(0xFFF07D29),
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.DarkGray,
            focusedLabelColor = Color(0xFFF07D29),
            unfocusedLabelColor = Color.Gray
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SegmentDropdown(
    segments: List<com.wtc.crmconnect.app.data.remote.dto.segment.SegmentResponseDto>,
    selectedSegmentId: String,
    onSelect: (String) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = segments.firstOrNull { it.id == selectedSegmentId }?.name ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Segmento", fontFamily = Poppins) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFE0E0E0),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.DarkGray,
                focusedLabelColor = Color(0xFFF07D29),
                unfocusedLabelColor = Color.Gray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            segments.forEach { segment ->
                DropdownMenuItem(
                    text = { Text(segment.name, fontFamily = Poppins) },
                    onClick = {
                        onSelect(segment.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    primaryColor: Color? = null
) {
    if (primaryColor != null) {
        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text, color = Color.White, fontFamily = Poppins)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text, color = Color.White, fontFamily = Poppins)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(0) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val now = java.time.LocalTime.now()
    val timePickerState = rememberTimePickerState(
        initialHour = now.hour,
        initialMinute = now.minute,
        is24Hour = true
    )

    if (step == 0) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = { if (datePickerState.selectedDateMillis != null) step = 1 }
                ) { Text("Próximo", fontFamily = Poppins) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar", fontFamily = Poppins) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    } else {
        Dialog(onDismissRequest = onDismiss) {
            androidx.compose.material3.Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Selecione o horário",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    )
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { step = 0 }) {
                            Text("Voltar", fontFamily = Poppins)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        TextButton(onClick = {
                            val millis = datePickerState.selectedDateMillis ?: return@TextButton
                            onConfirm(buildScheduleIso(millis, timePickerState.hour, timePickerState.minute))
                        }) {
                            Text("Confirmar", fontFamily = Poppins)
                        }
                    }
                }
            }
        }
    }
}

private fun buildScheduleIso(dateMillis: Long, hour: Int, minute: Int): String {
    val date = java.time.Instant.ofEpochMilli(dateMillis)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
    val dateTime = java.time.LocalDateTime.of(date, java.time.LocalTime.of(hour, minute))
    val zdt = dateTime.atZone(java.time.ZoneId.systemDefault())
    return java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zdt)
}

@Composable
private fun AbTestDialog(
    onConfirm: (variantBContent: String, splitPercent: Int, variantBTitle: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var variantBTitle by remember { mutableStateOf("") }
    var variantBContent by remember { mutableStateOf("") }
    var splitText by remember { mutableStateOf("50") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar A/B test", fontFamily = Poppins) },
        text = {
            Column {
                OutlinedTextField(
                    value = variantBTitle,
                    onValueChange = { variantBTitle = it },
                    label = { Text("Título variante B (opcional)", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = variantBContent,
                    onValueChange = { variantBContent = it },
                    label = { Text("Conteúdo variante B", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = splitText,
                    onValueChange = { splitText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Split % (1-99)", fontFamily = Poppins) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val split = splitText.toIntOrNull() ?: 0
                if (variantBContent.isNotBlank() && split in 1..99) {
                    onConfirm(variantBContent.trim(), split, variantBTitle.trim().ifBlank { null })
                }
            }) {
                Text("Aplicar", fontFamily = Poppins)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", fontFamily = Poppins) }
        }
    )
}
