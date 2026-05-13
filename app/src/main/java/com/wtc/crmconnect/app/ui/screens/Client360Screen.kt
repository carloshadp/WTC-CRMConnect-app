package com.wtc.crmconnect.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.wtc.crmconnect.app.data.remote.dto.enums.SenderType
import com.wtc.crmconnect.app.viewmodel.Client360Event
import com.wtc.crmconnect.app.viewmodel.Client360ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Client360Screen(
    navController: NavController,
    viewModel: Client360ViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val backgroundColor = Color(0xFF22394E)
    val cardColor = Color(0xFF2D4A5E)
    val laranja = Color(0xFFF07D29)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is Client360Event.ShowError -> snackbarHostState.showSnackbar(event.message)
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
                        text = "Meu Perfil 360°",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { ClienteBottomNavigationBar(navController, activeScreen = "client_360_screen") }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.customer == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = laranja)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            uiState.customer?.let { customer ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(laranja, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = customer.email.firstOrNull()?.uppercaseChar()?.toString() ?: "C",
                                    color = Color.White,
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            }
                            Column {
                                Text(
                                    text = customer.name.ifBlank { customer.email },
                                    color = Color.White,
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = customer.email,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontFamily = Poppins,
                                    fontSize = 12.sp
                                )
                                customer.phone?.takeIf { it.isNotBlank() }?.let { phone ->
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = phone,
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontFamily = Poppins,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard360(
                            label = "Status",
                            value = customer.status.name,
                            cardColor = cardColor,
                            modifier = Modifier.weight(1f)
                        )
                        if (customer.score != null) {
                            StatCard360(
                                label = "Score",
                                value = "${customer.score}",
                                cardColor = cardColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        StatCard360(
                            label = "Mensagens",
                            value = "${uiState.recentMessages.size}",
                            cardColor = cardColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (uiState.recentMessages.isNotEmpty()) {
                item {
                    Text(
                        text = "Últimas mensagens",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
                items(uiState.recentMessages) { message ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (message.senderType == SenderType.OPERADOR) "Operador" else "Você",
                                    color = if (message.senderType == SenderType.OPERADOR) laranja else Color.White.copy(alpha = 0.6f),
                                    fontFamily = Poppins,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = message.content.take(80).let { if (message.content.length > 80) "$it…" else it },
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontFamily = Poppins,
                                    fontSize = 13.sp
                                )
                            }
                            message.sentAt?.let { sentAt ->
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = formatTime(sentAt) ?: "",
                                    color = Color.White.copy(alpha = 0.4f),
                                    fontFamily = Poppins,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.recentCampaigns.isNotEmpty()) {
                item {
                    Text(
                        text = "Últimas campanhas",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
                items(uiState.recentCampaigns) { campaign ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("client_campaign_detail_screen/${campaign.id}")
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = campaign.title,
                                    color = Color.White,
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                                campaign.sentAt?.let { sent ->
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "Enviada: ${formatCampaignDate(sent)}",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontFamily = Poppins,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            ClientCampaignStatusChip(campaign.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard360(
    label: String,
    value: String,
    cardColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = Poppins,
                fontSize = 11.sp
            )
        }
    }
}
