package com.example.playfriends.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.playfriends.R
import com.example.playfriends.ui.screen.findActivity
import com.example.playfriends.ui.viewmodel.UserViewModel

@Composable
fun rememberAuthState(navController: NavController) {
    val context = LocalContext.current
    val activity = context.findActivity() as? ComponentActivity
    requireNotNull(activity) { "Activity를 찾을 수 없습니다." }
    val userViewModel: UserViewModel = viewModel(viewModelStoreOwner = activity)
    val user by userViewModel.user.collectAsState()
    val loginState by userViewModel.loginState.collectAsState()

    // 스플래쉬 UI: 흰색 배경 + 로고 중앙
    SplashScreen()

    LaunchedEffect(user, loginState) {
        if (loginState is UserViewModel.LoginState.Success && navController.currentDestination?.route == "login") {
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        } else if (user == null && loginState is UserViewModel.LoginState.Idle) {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.getCurrentUser()
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "앱 로고",
            modifier = Modifier.size(120.dp)
        )
    }
}
