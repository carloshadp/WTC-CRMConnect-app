package com.wtc.crmconnect.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wtc.crmconnect.app.R
import com.wtc.crmconnect.app.data.remote.dto.enums.Role
import com.wtc.crmconnect.app.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        delay(1200L)
        val role = viewModel.resolveStartDestination()
        val target = when (role) {
            Role.OPERATOR -> "home_screen"
            Role.CUSTOMER -> "home_client_screen"
            null -> "login_screen"
        }
        navController.navigate(target) {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF22394E)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.wtc_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(160.dp)
        )
    }
}
