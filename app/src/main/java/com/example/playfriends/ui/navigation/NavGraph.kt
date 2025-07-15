package com.example.playfriends.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.playfriends.ui.screen.HomeScreen
import com.example.playfriends.ui.screen.LoginScreen
import com.example.playfriends.ui.screen.GroupScreen
import com.example.playfriends.ui.screen.ProfileScreen
import com.example.playfriends.ui.screen.GroupPlanScreen
import com.example.playfriends.ui.screen.TestScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val authState = rememberAuthState(navController)
    
    NavHost(
        navController = navController,
        startDestination = "splash" // 스플래시 화면을 시작 화면으로 설정
    ) {
        composable("splash") {
            // 스플래시 화면 - AuthState에서 자동으로 적절한 화면으로 이동
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = authState.onLoginSuccess
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                onLogout = authState.onLogout
            )
        }
        composable(
            "group/{groupId}",
            arguments = listOf(navArgument("groupId") { type = NavType.StringType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")
            if (groupId != null) {
                GroupScreen(navController = navController, groupId = groupId)
            }
        }
        composable("groupPlan") {
            GroupPlanScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                onLogout = authState.onLogout
            )
        }
        composable("test") {
            TestScreen(navController = navController)
        }
    }
}
