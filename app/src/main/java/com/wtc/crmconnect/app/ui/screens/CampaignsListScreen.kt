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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.remote.dto.enums.CampaignStatus
import com.wtc.crmconnect.app.viewmodel.CampaignsEvent
import com.wtc.crmconnect.app.viewmodel.CampaignsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignsListScreen(
    navController: NavController,
    viewModel: CampaignsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val laranja = Color(0xFFF07D29)
    val backgroundColor = Color(0xFF22394E)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CampaignsEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Campanhas",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("campaign_detail_screen/new") },
                containerColor = laranja,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nova campanha")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            when {
                uiState.isLoading && uiState.campaigns.isEmpty() -> {
                    CircularProgressIndicator(
                        color = laranja,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.campaigns.isEmpty() -> {
                    Text(
                        text = "Nenhuma campanha ainda. Toque em + para criar.",
                        color = Color.White,
                        fontFamily = Poppins,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.campaigns, key = { it.id }) { campaign ->
                            CampaignCard(
                                campaign = campaign,
                                onClick = {
                                    navController.navigate("campaign_detail_screen/${campaign.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CampaignCard(
    campaign: CampaignResponseDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = campaign.title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(8.dp))
                CampaignStatusChip(status = campaign.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = campaign.content,
                fontFamily = Poppins,
                color = Color.DarkGray,
                fontSize = 13.sp,
                maxLines = 2
            )
            val sub = when {
                campaign.sentAt != null -> "Enviada em ${campaign.sentAt}"
                campaign.scheduledAt != null -> "Agendada para ${campaign.scheduledAt}"
                else -> "Criada em ${campaign.createdAt ?: "—"}"
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sub,
                fontFamily = Poppins,
                color = Color.Gray,
                fontSize = 11.sp
            )
            if (campaign.abTest != null) {
                Text(
                    text = "A/B test: ${campaign.abTest.splitPercent}% variante B",
                    fontFamily = Poppins,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
internal fun CampaignStatusChip(status: CampaignStatus) {
    val (bg, fg) = when (status) {
        CampaignStatus.RASCUNHO -> Color(0xFFF1F1F1) to Color(0xFF616161)
        CampaignStatus.AGENDADA -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        CampaignStatus.ENVIADA -> Color(0xFFE6F4EA) to Color(0xFF2E7D32)
        CampaignStatus.CANCELADA -> Color(0xFFFDECEA) to Color(0xFFC62828)
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
