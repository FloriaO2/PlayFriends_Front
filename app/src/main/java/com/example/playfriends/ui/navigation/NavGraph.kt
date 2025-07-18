package com.example.playfriends.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playfriends.ui.screen.HomeScreen
import com.example.playfriends.ui.screen.LoginScreen
import com.example.playfriends.ui.screen.GroupScreen
import com.example.playfriends.ui.screen.ProfileScreen
import com.example.playfriends.ui.screen.GroupPlanScreen
import com.example.playfriends.ui.screen.TestScreen
import com.example.playfriends.ui.viewmodel.UserViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    rememberAuthState(navController)

    NavHost(
        navController = navController,
        startDestination = "splash" // 스플래시 화면을 시작 화면으로 설정
    ) {
        composable("splash") {
            // 스플래시 화면 - AuthState에서 자동으로 적절한 화면으로 이동
        }
        composable("login") {
            LoginScreen(
                navController = navController
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController
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
        composable(
            "groupPlan/{groupId}?categories={categories}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.StringType },
                navArgument("categories") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId")
            val categoriesString = backStackEntry.arguments?.getString("categories")
            val categories = categoriesString?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

            if (groupId != null) {
                GroupPlanScreen(
                    navController = navController,
                    groupId = groupId,
                    categories = categories
                )
            }
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                onLogout = {
                    userViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable("test") {
            TestScreen(navController = navController)
        }
    }
}
