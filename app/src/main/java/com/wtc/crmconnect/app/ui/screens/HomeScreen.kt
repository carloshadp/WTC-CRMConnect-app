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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wtc.crmconnect.app.ui.theme.LaranjaPrincipal
import com.wtc.crmconnect.app.ui.theme.PretoRodape
import com.wtc.crmconnect.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val backgroundColor = Color(0xFF22394E)
    val laranja = Color(0xFFF07D29)
    val cardBackground = Color(0xFF2D4A5E)

    Scaffold(
        containerColor = backgroundColor,
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Column {
                    Text(
                        text = "Bem-vindo de volta",
                        color = Color.White.copy(alpha = 0.65f),
                        fontFamily = Poppins,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = viewModel.userEmail,
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = laranja),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Painel CRM",
                            color = Color.White,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Gerencie campanhas, segmentos e clientes em um só lugar.",
                            color = Color.White.copy(alpha = 0.88f),
                            fontFamily = Poppins,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Acesso rápido",
                    color = Color.White,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardCard(
                            title = "Campanhas",
                            subtitle = "Criar e gerenciar",
                            iconId = R.drawable.ic_text,
                            modifier = Modifier.weight(1f),
                            cardColor = cardBackground,
                            onClick = { navController.navigate("campaigns_list_screen") }
                        )
                        DashboardCard(
                            title = "Segmentos",
                            subtitle = "Grupos de clientes",
                            iconId = R.drawable.ic_notes,
                            modifier = Modifier.weight(1f),
                            cardColor = cardBackground,
                            onClick = { navController.navigate("segments_screen") }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardCard(
                            title = "Clientes",
                            subtitle = "Histórico e timeline",
                            iconId = R.drawable.ic_client,
                            modifier = Modifier.weight(1f),
                            cardColor = cardBackground,
                            onClick = { navController.navigate("history_customers_screen") }
                        )
                        DashboardCard(
                            title = "Mensagens",
                            subtitle = "Atendimento direto",
                            iconId = R.drawable.ic_chat,
                            modifier = Modifier.weight(1f),
                            cardColor = cardBackground,
                            onClick = { navController.navigate("history_customers_screen") }
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Dicas de uso",
                            color = laranja,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        CrmTip("Crie segmentos para personalizar campanhas por perfil de cliente.")
                        CrmTip("Agende campanhas com antecedência para maior assertividade.")
                        CrmTip("Use o chat para atendimento direto e personalizado.")
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    subtitle: String,
    iconId: Int,
    modifier: Modifier = Modifier,
    cardColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                color = Color.White,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.55f),
                fontFamily = Poppins,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun CrmTip(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Text(text = "• ", color = Color(0xFFF07D29), fontFamily = Poppins, fontSize = 13.sp)
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.75f),
            fontFamily = Poppins,
            fontSize = 13.sp
        )
    }
}

@Composable
fun PageIndicatorDots(currentPage: Int, totalPages: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalPages) { index ->
            val color = if (index == currentPage) Color(0xFF22394E) else Color(0xFFD3D3D3)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            if (index != totalPages - 1) Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, activeScreen: String = "home_screen") {
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
            BottomIcon(
                iconId = R.drawable.ic_home,
                active = activeScreen == "home_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("home_screen") }
            )
            BottomIcon(
                iconId = R.drawable.ic_text,
                active = activeScreen == "campaigns_list_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("campaigns_list_screen") }
            )
            BottomIcon(
                iconId = R.drawable.ic_notes,
                active = activeScreen == "segments_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("segments_screen") }
            )
            BottomIcon(
                iconId = R.drawable.ic_chat,
                active = activeScreen == "history_customers_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("history_customers_screen") }
            )
            BottomIcon(
                iconId = R.drawable.ic_user,
                active = activeScreen == "edit_user_screen",
                extraHeight = extraHeight,
                iconOffsetY = (-8).dp,
                onClick = { navController.navigate("edit_user_screen") }
            )
        }
    }
}

@Composable
fun BottomIcon(
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
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(iconSize)
                .offset(y = iconOffsetY)
        )
    }
}
