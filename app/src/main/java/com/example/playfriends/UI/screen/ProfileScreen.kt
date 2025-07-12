package com.example.playfriends.UI.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.playfriends.UI.component.AppTopBar

@Composable
fun ProfileScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF1FFF4)
    
    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            AppTopBar(
                onLogoClick = { navController.navigate("home") },
                onProfileClick = { /* 이미 프로필 화면이므로 아무것도 하지 않음 */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "프로필 화면",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { navController.navigateUp() }
            ) {
                Text("뒤로 가기")
            }
        }
    }
}

