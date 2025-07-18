package com.example.playfriends.ui.screen

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CardDefaults
import androidx.navigation.NavController
import com.example.playfriends.ui.component.AppTopBar
import com.example.playfriends.ui.component.HexagonGraph
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playfriends.ui.viewmodel.UserViewModel
import kotlin.random.Random

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity() as? ComponentActivity
    requireNotNull(activity) { "Activity를 찾을 수 없습니다." }
    val userViewModel: UserViewModel = viewModel(viewModelStoreOwner = activity)
    val user by userViewModel.user.collectAsState()

    val bgColor = Color(0xFFF1FFF4)
    val tagColor = Color(0xFFECECEC)

    // 화면이 처음 로드될 때 사용자 정보를 가져옵니다.
    LaunchedEffect(Unit) {
        userViewModel.getCurrentUser()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onLogoClick = { navController.navigate("home") },
                onProfileClick = { navController.navigate("profile") },
                profileInitial = user?.username?.first()?.toString() ?: "P"
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
            Spacer(modifier = Modifier.height(15.dp))

            Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(12.dp))

            // 프로필 상단 섹션
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 좌측: 프로필 원형 아이콘
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7E57C2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user?.username?.first()?.toString() ?: "P",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // 우측: 닉네임, 아이디와 회원 탈퇴 버튼
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color(0xA9000000))) {
                                append("닉네임: ")
                            }
                            withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xA9000000))) {
                                append(user?.username ?: "로딩 중...")
                            }
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color(0xA9000000))) {
                                append("아이디: ")
                            }
                            withStyle(SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xA9000000))) {
                                append(user?.userid ?: "로딩 중...")
                            }
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier.clickable {
                            onLogout()
                        }
                    ) {
                        Text(
                            "로그아웃",
                            color = Color(0xFFB21111),
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
                    .height(35.dp)
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
                        append("${user?.username ?: "고객"}")
                    }
                    append(" 님을 위한 취향 분석 레포트")
                },
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PlayPreferences를 이용한 육각형 그래프
            user?.play_preferences?.let {
                HexagonGraph(
                    playPreferences = it,
                    modifier = Modifier
                        .size(300.dp) // 그래프 크기 조정
                        .fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 성향 분석 결과 텍스트
            Text("취향 분석 결과,", fontSize = 16.sp)
            // 칭호 계산
            val play = user?.play_preferences
            val title = remember(play) {
                if (play == null) "-" else {
                    val map = mapOf(
                        "도파민" to play.vibe_level,
                        "붐빔" to play.crowd_level,
                        "활동성" to play.activeness_level,
                        "유행" to play.trend_level,
                        "계획성" to play.planning_level,
                        "실내" to play.location_preference
                    )
                    val max = map.values.maxOrNull() ?: 0f
                    val maxTypes = map.filter { it.value == max }.keys.toList()
                    val selected = maxTypes.random()
                    when (selected) {
                        "도파민" -> "스릴 헌터"
                        "붐빔" -> "인간 칵테일"
                        "활동성" -> "용감한 모험가"
                        "유행" -> "앞서가는 선구자"
                        "계획성" -> "전략의 귀재"
                        "실내" -> "고요한 사색가"
                        else -> "-"
                    }
                }
            }
            Text(
                buildAnnotatedString {
                    append("당신은 ")
                    withStyle(SpanStyle(color = Color(0xFF7EA86A), fontWeight = FontWeight.Bold)) {
                        append(title)
                    }
                    append("입니다.")
                },
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
        }


    }
}
