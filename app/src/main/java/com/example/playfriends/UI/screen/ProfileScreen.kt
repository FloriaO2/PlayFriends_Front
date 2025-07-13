package com.example.playfriends.UI.screen

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.navigation.NavController
import com.example.playfriends.UI.component.AppTopBar

@Composable
fun ProfileScreen(navController: NavController) {
    val bgColor = Color(0xFFF1FFF4)
    val tagColor = Color(0xFFECECEC)

    Scaffold(
        topBar = {
            AppTopBar(
                onLogoClick = { navController.navigate("home") },
                onProfileClick = { navController.navigate("profile") },
                profileInitial = "A"
            )
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Profile", fontSize = 32.sp, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(12.dp))

            // 프로필 상단 섹션
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 좌측: 프로필 원형 아이콘
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7E57C2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("A", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(24.dp))

                // 우측: 닉네임, 아이디와 회원 탈퇴 버튼
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "닉네임 : ABC",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xA9000000)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        "아이디 : ABCDE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xA9000000)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier.clickable { /* TODO: 회원 탈퇴 로직 */ }
                    ) {
                        Text(
                            "회원 탈퇴",
                            color = Color(0xFF8B0000),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate("test") },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA1D0A3)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("취향 테스트 다시 보러 가기", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 취향 분석 레포트 타이틀
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF228B22), fontWeight = FontWeight.Bold)) {
                        append("[아이디]")
                    }
                    append("님을 위한 취향 분석 레포트")
                },
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 육각형 placeholder
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(240.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val hexPath = Path()
                    val radius = size.minDimension / 2f * 0.9f
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    for (i in 0..5) {
                        val angle = Math.toRadians((60.0 * i - 30.0))
                        val x = centerX + radius * Math.cos(angle).toFloat()
                        val y = centerY + radius * Math.sin(angle).toFloat()
                        if (i == 0) {
                            hexPath.moveTo(x, y)
                        } else {
                            hexPath.lineTo(x, y)
                        }
                    }
                    hexPath.close()
                    drawPath(
                        path = hexPath,
                        color = Color(0xFFD1E9D1),
                        style = Fill
                    )
                    drawPath(
                        path = hexPath,
                        color = Color(0xFFB0EACD),
                        style = Stroke(width = 6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 성향 분석 결과 텍스트
            Text("취향 분석 결과,", fontSize = 16.sp)
            Text(
                buildAnnotatedString {
                    append("당신은 ")
                    withStyle(SpanStyle(color = Color(0xFF7EA86A), fontWeight = FontWeight.Bold)) {
                        append("용감한 모험가")
                    }
                    append("입니다.")
                },
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            // 추천 콘텐츠 카드
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "⭐ 놀이 콘텐츠 추천 ⭐",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val tags = listOf("놀이공원", "방탈출 카페", "공방", "팝업")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tags) { tag ->
                            Box(
                                modifier = Modifier
                                    .background(tagColor, RoundedCornerShape(50))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(text = tag, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
