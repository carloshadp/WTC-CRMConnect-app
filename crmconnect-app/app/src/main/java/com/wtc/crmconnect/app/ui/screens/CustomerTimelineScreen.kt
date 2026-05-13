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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
import com.wtc.crmconnect.app.data.remote.dto.customer.TimelineCampaignDto
import com.wtc.crmconnect.app.data.remote.dto.customer.TimelineMessageDto
import com.wtc.crmconnect.app.data.remote.dto.customer.TimelineTaskDto
import com.wtc.crmconnect.app.data.remote.dto.enums.CustomerStatus
import com.wtc.crmconnect.app.viewmodel.CustomerTimelineEvent
import com.wtc.crmconnect.app.viewmodel.CustomerTimelineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerTimelineScreen(
    navController: NavController,
    viewModel: CustomerTimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backgroundColor = Color(0xFF22394E)
    val laranja = Color(0xFFF07D29)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CustomerTimelineEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
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
                        "Perfil 360°",
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
                actions = {
                    IconButton(onClick = { viewModel.load() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Recarregar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(backgroundColor)
        ) {
            when {
                uiState.isLoading && uiState.timeline == null -> {
                    CircularProgressIndicator(
                        color = laranja,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.timeline == null -> {
                    Text(
                        text = "Não foi possível carregar a timeline.",
                        color = Color.White,
                        fontFamily = Poppins,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    val timeline = uiState.timeline!!
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            CustomerHeaderCard(
                                customer = timeline.customer,
                                onOpenChat = {
                                    navController.navigate("message_storage_screen/${timeline.customer.id}")
                                }
                            )
                        }
                        item {
                            SectionHeader("Mensagens recentes")
                            if (timeline.messages.isEmpty()) {
                                EmptySectionCard("Sem mensagens registradas para este cliente.")
                            }
                        }
                        items(timeline.messages, key = { "msg-${it.id}" }) { msg ->
                            TimelineMessageCard(msg)
                        }
                        item {
                            SectionHeader("Campanhas")
                            if (timeline.campaigns.isEmpty()) {
                                EmptySectionCard("Nenhuma campanha associada.")
                            }
                        }
                        items(timeline.campaigns, key = { "cmp-${it.id}" }) { campaign ->
                            TimelineCampaignCard(campaign)
                        }
                        item {
                            SectionHeader("Tarefas")
                            if (timeline.tasks.isEmpty()) {
                                EmptySectionCard("Sem tarefas para este cliente.")
                            }
                        }
                        items(timeline.tasks, key = { "tsk-${it.id}" }) { task ->
                            TimelineTaskCard(task)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomerHeaderCard(
    customer: CustomerResponseDto,
    onOpenChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = customer.name,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = customer.email,
                fontFamily = Poppins,
                color = Color.DarkGray,
                fontSize = 13.sp
            )
            if (!customer.phone.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = customer.phone,
                    fontFamily = Poppins,
                    color = Color.DarkGray,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TimelineStatusChip(status = customer.status)
                if (customer.score != null) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Score ${customer.score}",
                        fontFamily = Poppins,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            if (!customer.tags.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tags: ${customer.tags.joinToString(", ")}",
                    fontFamily = Poppins,
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenChat,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF07D29)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Chat,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Abrir conversa", fontFamily = Poppins, color = Color.White)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        color = Color.White,
        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun EmptySectionCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E4A63)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            color = Color.LightGray,
            fontFamily = Poppins,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun TimelineMessageCard(msg: TimelineMessageDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = msg.content,
                fontFamily = Poppins,
                color = Color.Black,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Status: ${msg.status}",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                msg.sentAt?.let { sent ->
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Enviado: $sent",
                        fontFamily = Poppins,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                msg.readAt?.let { read ->
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Lido: $read",
                        fontFamily = Poppins,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineCampaignCard(campaign: TimelineCampaignDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = campaign.title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${campaign.status}",
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Color.Gray
            )
            campaign.sentAt?.let {
                Text(
                    text = "Enviado: $it",
                    fontFamily = Poppins,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun TimelineTaskCard(task: TimelineTaskDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = task.title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${task.status}${task.dueDate?.let { " · vence: $it" } ?: ""}",
                fontFamily = Poppins,
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun TimelineStatusChip(status: CustomerStatus) {
    val (bg, fg) = when (status) {
        CustomerStatus.ATIVO -> Color(0xFFE6F4EA) to Color(0xFF2E7D32)
        CustomerStatus.INATIVO -> Color(0xFFF1F1F1) to Color(0xFF616161)
        CustomerStatus.BLOQUEADO -> Color(0xFFFDECEA) to Color(0xFFC62828)
    }
    Box(
        modifier = Modifier
            .background(bg, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.name,
            color = fg,
            fontFamily = Poppins,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
