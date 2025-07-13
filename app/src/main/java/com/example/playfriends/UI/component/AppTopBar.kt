package com.example.playfriends.UI.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.playfriends.R

@Composable
fun AppTopBar(
    onLogoClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    profileInitial: String = "A",
    appName: String = "PlayFriends"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFAFEDAF), Color(0xFF8FD68F))
                )
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // 로고 이미지 - 상단바 정중앙에 배치
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.topbar_logo),
            contentDescription = "TopBar Logo",
            modifier = Modifier
                .size(40.dp)
                .clickable { onLogoClick() }
                .align(Alignment.CenterStart)
                .offset(y = 0.2.dp),
            contentScale = ContentScale.Fit
        )
        
        // 앱 이름 - 상단바 정중앙에 배치
        Text(
            text = appName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
        
        // 프로필 아이콘 - 상단바 우측에 배치
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF9575CD))
                .clickable { onProfileClick() }
                .align(Alignment.CenterEnd),
            contentAlignment = Alignment.Center
        ) {
            Text(
                profileInitial,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

