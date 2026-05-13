package com.wtc.crmconnect.app.ui.screens

import com.wtc.crmconnect.app.R
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wtc.crmconnect.app.data.remote.dto.campaign.CampaignResponseDto
import com.wtc.crmconnect.app.data.remote.dto.customer.CustomerResponseDto
import com.wtc.crmconnect.app.data.remote.dto.enums.CampaignStatus
import com.wtc.crmconnect.app.data.remote.dto.enums.CustomerStatus
import com.wtc.crmconnect.app.ui.theme.AzulFundo
import com.wtc.crmconnect.app.ui.theme.LaranjaPrincipal
import com.wtc.crmconnect.app.ui.theme.PretoRodape
import com.wtc.crmconnect.app.viewmodel.HomeClientEvent
import com.wtc.crmconnect.app.viewmodel.HomeClientViewModel
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeClientScreen(
    navController: NavController,
    viewModel: HomeClientViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeClientEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        containerColor = AzulFundo,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            ClienteBottomNavigationBar(navController, activeScreen = "home_client_screen")
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val greetingName = uiState.customer?.name?.takeIf { it.isNotBlank() } ?: "Cliente"
                Column {
                    Text(
                        text = "Bem-vindo de volta",
                        color = Color.White.copy(alpha = 0.65f),
                        fontFamily = Poppins,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = greetingName,
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            item {
                ClientProfileCard(
                    customer = uiState.customer,
                    segmentName = uiState.segmentName,
                    isLoading = uiState.isLoading
                )
            }

            if (uiState.campaigns.isNotEmpty()) {
                item {
                    Text(
                        text = "Campanhas recebidas",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
                items(uiState.campaigns) { campaign ->
                    ClientCampaignCard(
                        campaign = campaign,
                        onClick = { navController.navigate("client_campaign_detail_screen/${campaign.id}") }
                    )
                }
            } else if (!uiState.isLoading) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D4A5E))
                    ) {
                        Text(
                            text = "Nenhuma campanha disponível no momento.",
                            color = Color.White.copy(alpha = 0.7f),
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientProfileCard(
    customer: CustomerResponseDto?,
    segmentName: String?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D4A5E)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when {
                isLoading && customer == null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = LaranjaPrincipal)
                    }
                }
                customer == null -> {
                    Text(
                        text = "Não foi possível carregar seu perfil.",
                        fontFamily = Poppins,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
                else -> {
                    Text(
                        text = customer.email,
                        fontFamily = Poppins,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    if (!customer.phone.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = customer.phone,
                            fontFamily = Poppins,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ClientStatusChip(status = customer.status)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Segmento: ${segmentName ?: "—"}",
                            fontFamily = Poppins,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                        if (customer.score != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Score ${customer.score}",
                                fontFamily = Poppins,
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientStatusChip(status: CustomerStatus) {
    val (bg, fg) = when (status) {
        CustomerStatus.ATIVO -> Color(0xFF1B5E20).copy(alpha = 0.5f) to Color(0xFF81C784)
        CustomerStatus.INATIVO -> Color(0xFF424242).copy(alpha = 0.5f) to Color(0xFFBDBDBD)
        CustomerStatus.BLOQUEADO -> Color(0xFFB71C1C).copy(alpha = 0.5f) to Color(0xFFEF9A9A)
    }
    Box(
        modifier = Modifier
            .background(bg, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
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

@Composable
internal fun ClientCampaignCard(
    campaign: CampaignResponseDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D4A5E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = campaign.title,
                    color = Color.White,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                ClientCampaignStatusChip(campaign.status)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = campaign.content.take(100).let { if (campaign.content.length > 100) "$it…" else it },
                color = Color.White.copy(alpha = 0.75f),
                fontFamily = Poppins,
                fontSize = 13.sp
            )
            campaign.sentAt?.let { sent ->
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Enviada: ${formatCampaignDate(sent)}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontFamily = Poppins,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
internal fun ClientCampaignStatusChip(status: CampaignStatus) {
    val (bg, fg, label) = when (status) {
        CampaignStatus.ENVIADA -> Triple(Color(0xFF1B5E20).copy(alpha = 0.5f), Color(0xFF81C784), "Enviada")
        CampaignStatus.AGENDADA -> Triple(Color(0xFF0D47A1).copy(alpha = 0.5f), Color(0xFF64B5F6), "Agendada")
        CampaignStatus.RASCUNHO -> Triple(Color(0xFF424242).copy(alpha = 0.5f), Color(0xFFBDBDBD), "Rascunho")
        CampaignStatus.CANCELADA -> Triple(Color(0xFFB71C1C).copy(alpha = 0.5f), Color(0xFFEF9A9A), "Cancelada")
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontFamily = Poppins,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

internal fun formatCampaignDate(isoString: String): String {
    return runCatching {
        val odt = OffsetDateTime.parse(isoString)
        val zoned = odt.atZoneSameInstant(ZoneId.of("America/Sao_Paulo"))
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(zoned)
    }.getOrDefault(isoString)
}

@Composable
fun ClienteBottomNavigationBar(
    navController: NavController,
    activeScreen: String = "home_client_screen"
) {
    val slotHeight = 48.dp
    val extraHeight = 36.dp
    val totalHeight = slotHeight + extraHeight

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(totalHeight)
            .background(PretoRodape),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            ClienteBottomIcon(
                iconId = R.drawable.ic_home,
                active = activeScreen == "home_client_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("home_client_screen") }
            )
            ClienteBottomIcon(
                iconId = R.drawable.ic_notes,
                active = activeScreen == "client_360_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("client_360_screen") }
            )
            ClienteBottomIcon(
                iconId = R.drawable.ic_chat,
                active = activeScreen == "message_storage_client_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("message_storage_client_screen") }
            )
            ClienteBottomIcon(
                iconId = R.drawable.ic_user,
                active = activeScreen == "edit_user_client_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("edit_user_client_screen") }
            )
        }
    }
}

@Composable
private fun ClienteBottomIcon(
    iconId: Int,
    active: Boolean,
    extraHeight: Dp = 36.dp,
    onClick: () -> Unit,
    iconOffsetY: Dp = 0.dp
) {
    val iconSize = 22.dp
    val slotHeight = 48.dp + extraHeight
    val cornerRadius = 16.dp
    val strokeWidth = 2.dp

    Box(
        modifier = Modifier
            .width(54.dp)
            .height(slotHeight)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (active) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(slotHeight)
                    .offset(y = -extraHeight * 0.40f)
                    .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
                    .background(LaranjaPrincipal)
                    .drawBehind {
                        val strokePx = strokeWidth.toPx()
                        val cornerRadiusPx = cornerRadius.toPx()
                        val width = size.width
                        val height = size.height

                        drawArc(
                            color = Color.White,
                            startAngle = 180f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(0f, 0f),
                            size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                            style = Stroke(strokePx)
                        )
                        drawArc(
                            color = Color.White,
                            startAngle = 270f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = Offset(width - cornerRadiusPx * 2, 0f),
                            size = Size(cornerRadiusPx * 2, cornerRadiusPx * 2),
                            style = Stroke(strokePx)
                        )
                        drawLine(
                            color = Color.White,
                            strokeWidth = strokePx,
                            start = Offset(cornerRadiusPx, strokePx / 20),
                            end = Offset(width + cornerRadiusPx, strokePx / 20)
                        )
                        drawLine(
                            color = Color.White,
                            strokeWidth = strokePx,
                            start = Offset(strokePx / 20, cornerRadiusPx),
                            end = Offset(strokePx / 20, height)
                        )
                        drawLine(
                            color = Color.White,
                            strokeWidth = strokePx,
                            start = Offset(width - strokePx / 20, cornerRadiusPx),
                            end = Offset(width - strokePx / 20, height)
                        )
                    }
            )
        }

        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .offset(y = iconOffsetY)
        )
    }
}
