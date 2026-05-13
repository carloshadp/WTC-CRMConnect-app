package com.wtc.crmconnect.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.wtc.crmconnect.app.ui.screens.CampaignDetailScreen
import com.wtc.crmconnect.app.ui.screens.CampaignsListScreen
import com.wtc.crmconnect.app.ui.screens.ChatNewGroupScreen
import com.wtc.crmconnect.app.ui.screens.Client360Screen
import com.wtc.crmconnect.app.ui.screens.ClientCampaignDetailScreen
import com.wtc.crmconnect.app.ui.screens.ClientCampaignsScreen
import com.wtc.crmconnect.app.ui.screens.CustomerTimelineScreen
import com.wtc.crmconnect.app.ui.screens.EditUserClientScreen
import com.wtc.crmconnect.app.ui.screens.EditUserScreen
import com.wtc.crmconnect.app.ui.screens.ForgotPasswordScreen
import com.wtc.crmconnect.app.ui.screens.HelpClientScreen
import com.wtc.crmconnect.app.ui.screens.HistoryCostumersClientScreen
import com.wtc.crmconnect.app.ui.screens.HistoryCustomersScreen
import com.wtc.crmconnect.app.ui.screens.HomeClientScreen
import com.wtc.crmconnect.app.ui.screens.HomeScreen
import com.wtc.crmconnect.app.ui.screens.LoginScreen
import com.wtc.crmconnect.app.ui.screens.MessageStorageClientScreen
import com.wtc.crmconnect.app.ui.screens.MessageStorageScreen
import com.wtc.crmconnect.app.ui.screens.RegisterScreen
import com.wtc.crmconnect.app.ui.screens.ResetPasswordScreen
import com.wtc.crmconnect.app.ui.screens.SegmentsScreen
import com.wtc.crmconnect.app.ui.screens.SplashScreen
import com.wtc.crmconnect.app.ui.screens.TextMessageScreen
import com.wtc.crmconnect.app.ui.screens.WriteAdScreen
import com.wtc.crmconnect.app.viewmodel.ClientesViewModel
import com.wtc.crmconnect.app.viewmodel.OperadoresViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val operadoresViewModel: OperadoresViewModel = viewModel()
    val clientesViewModel: ClientesViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash_screen") {

        composable("splash_screen") { SplashScreen(navController) }
        composable("login_screen") { LoginScreen(navController) }
        composable("register_screen") { RegisterScreen(navController) }
        composable("forgot_password_screen") { ForgotPasswordScreen(navController) }
        composable("reset_password_screen") { ResetPasswordScreen(navController) }

        composable("home_screen") {
            HomeScreen(navController = navController)
        }

        composable("home_client_screen") {
            HomeClientScreen(navController = navController)
        }

        composable("text_message_screen") { TextMessageScreen(navController) }

        composable("history_customers_screen") {
            HistoryCustomersScreen(navController)
        }

        composable("history_customers_client_screen") {
            HistoryCostumersClientScreen(navController, operadoresViewModel)
        }

        composable("chat_new_group_screen") {
            ChatNewGroupScreen(navController, clientesViewModel)
        }

        composable(
            route = "message_storage_screen/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId").orEmpty()
            MessageStorageScreen(
                navController = navController,
                customerId = customerId
            )
        }

        composable(
            route = "customer_timeline_screen/{customerId}",
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) {
            CustomerTimelineScreen(navController = navController)
        }

        composable("campaigns_list_screen") {
            CampaignsListScreen(navController = navController)
        }

        composable(
            route = "campaign_detail_screen/{campaignId}",
            arguments = listOf(navArgument("campaignId") { type = NavType.StringType })
        ) {
            CampaignDetailScreen(navController = navController)
        }

        composable("message_storage_client_screen") {
            MessageStorageClientScreen(navController = navController)
        }

        composable("edit_user_screen") { EditUserScreen(navController) }

        composable("edit_user_client_screen") {
            EditUserClientScreen(navController)
        }

        composable("segments_screen") {
            SegmentsScreen(navController = navController)
        }

        composable("client_campaigns_screen") {
            ClientCampaignsScreen(navController = navController)
        }

        composable("client_360_screen") {
            Client360Screen(navController = navController)
        }

        composable(
            route = "client_campaign_detail_screen/{campaignId}",
            arguments = listOf(navArgument("campaignId") { type = NavType.StringType })
        ) {
            ClientCampaignDetailScreen(navController = navController)
        }

        composable("help_client_screen") {
            HelpClientScreen(navController = navController, showDivider = true)
        }

        composable(
            route = "writeAdScreen/{email}/{tag}/{fonte}/{botoes}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("tag") { type = NavType.StringType },
                navArgument("fonte") { type = NavType.StringType },
                navArgument("botoes") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = URLDecoder.decode(
                backStackEntry.arguments?.getString("email") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val tag = URLDecoder.decode(
                backStackEntry.arguments?.getString("tag") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val fonte = URLDecoder.decode(
                backStackEntry.arguments?.getString("fonte") ?: "",
                StandardCharsets.UTF_8.toString()
            )
            val botoes = URLDecoder.decode(
                backStackEntry.arguments?.getString("botoes") ?: "",
                StandardCharsets.UTF_8.toString()
            )

            WriteAdScreen(
                navController = navController,
                userEmail = email,
                tag = tag,
                fonte = fonte,
                botoes = botoes
            )
        }
    }
}
