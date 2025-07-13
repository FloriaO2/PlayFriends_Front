package com.example.playfriends.UI.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playfriends.UI.screen.HomeScreen
import com.example.playfriends.UI.screen.LoginScreen
import com.example.playfriends.UI.screen.GroupScreen
import com.example.playfriends.UI.screen.ProfileScreen
import com.example.playfriends.UI.screen.GroupPlanScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login" // 로그인 화면을 시작 화면으로 설정
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("group") {
            GroupScreen(navController = navController)
        }
        composable("groupPlan") {
            GroupPlanScreen(navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}

