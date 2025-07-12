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
fun GroupScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF1FFF4)
    
    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            AppTopBar(
                onLogoClick = { navController.navigate("home") },
                onProfileClick = { navController.navigate("profile") }
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
                text = "그룹 화면",
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

