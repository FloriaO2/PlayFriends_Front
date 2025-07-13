package com.example.playfriends.UI.screen

import kotlin.math.roundToInt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TestScreen(navController: NavController) {
    val tabTitles = listOf("음식 취향", "컨텐츠 취향")
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    // 음식 취향 상태 관리 - 각 옵션별로 개별 상태 관리
    val foodPreferences = remember { mutableStateMapOf<String, Boolean>() }
    
    // 컨텐츠 취향 상태 관리
    val contentSliderValues = remember { mutableStateListOf(0f, 0f, 0f, 0f, 0f, 0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1FFF4))
    ) {
        // 상단바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                    )
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // 좌측: 다음에 하기 버튼
            Button(
                onClick = { 
                    // 현재 스택을 확인하여 어디서 왔는지 판단
                    if (navController.previousBackStackEntry?.destination?.route == "login") {
                        // 회원가입에서 온 경우 홈으로 이동
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // ProfileScreen에서 온 경우 ProfileScreen으로 돌아가기
                        navController.navigate("profile") {
                            popUpTo("test") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(80.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "다음에",
                        color = Color(0xFF228B22),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // 중앙: 제목
            Text(
                text = "취향 테스트",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            
            // 우측: 완료 버튼
            Button(
                onClick = { 
                    // 현재 스택을 확인하여 어디서 왔는지 판단
                    if (navController.previousBackStackEntry?.destination?.route == "login") {
                        // 회원가입에서 온 경우 홈으로 이동
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        // ProfileScreen에서 온 경우 ProfileScreen으로 돌아가기
                        navController.navigate("profile") {
                            popUpTo("test") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(80.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "완료",
                        color = Color(0xFF228B22),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFF228B22),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontWeight = FontWeight.Bold) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        // 탭 내용을 스크롤 가능한 영역으로 감싸기
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> FoodPreferenceTab(navController, foodPreferences)
                1 -> ContentPreferenceTab(navController, contentSliderValues)
            }
        }
    }
}

@Composable
fun FoodPreferenceTab(
    navController: NavController, 
    foodPreferences: MutableMap<String, Boolean>
) {
    val categories = listOf(
        "재료" to listOf("고기", "채소", "생선", "우유", "계란", "밀가루"),
        "맛" to listOf("매운", "느끼한", "단", "짠", "쓴", "신"),
        "조리 방법" to listOf("국물", "구이", "찜/찌개", "볶음", "튀김", "날것", "음료"),
        "조리 방식" to listOf("한식", "중식", "일식", "양식", "동남아식")
    )
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        categories.forEachIndexed { index, (title, options) ->
            // 카테고리 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFFFA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 제목 (녹색, 굵은 글씨, 중앙 정렬)
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF228B22),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )
                    
                    // 3열로 배치하기 위해 옵션들을 3개씩 묶기
                    options.chunked(3).forEachIndexed { rowIndex, rowOptions ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 첫 번째 열
                            if (rowOptions.isNotEmpty()) {
                                PreferenceItem(
                                    option = rowOptions[0], 
                                    category = title,
                                    foodPreferences = foodPreferences,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            
                            // 두 번째 열
                            if (rowOptions.size > 1) {
                                PreferenceItem(
                                    option = rowOptions[1], 
                                    category = title,
                                    foodPreferences = foodPreferences,
                                    modifier = if (rowOptions.size == 2) Modifier.weight(2f).offset(x = (-4).dp) else Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            
                            // 세 번째 열 (마지막 행에 항목이 2개만 있으면 표시하지 않음)
                            if (rowOptions.size > 2) {
                                PreferenceItem(
                                    option = rowOptions[2], 
                                    category = title,
                                    foodPreferences = foodPreferences,
                                    modifier = Modifier.weight(1f)
                                )
                            } else if (rowOptions.size == 2) {
                                // 마지막 행에 항목이 2개만 있으면 빈 공간 추가하지 않음
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            // 카드 간격 조정
            if (index < categories.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                // 마지막 항목인 경우 아래쪽 padding 추가
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}



@Composable
fun PreferenceItem(
    option: String, 
    category: String,
    foodPreferences: MutableMap<String, Boolean>,
    modifier: Modifier = Modifier
) {
    val key = "${category}_${option}"
    val isChecked = foodPreferences[key] ?: false
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 4.dp)
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    foodPreferences[key] = !isChecked
                }
        ) {
            Text(
                text = if (isChecked) "❤️" else "🤍",
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = option,
            fontSize = 13.sp,
            color = if (isChecked) Color(0xFF228B22) else Color(0xFF666666),
            fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun ContentPreferenceTab(navController: NavController, sliderValues: MutableList<Float>) {
    val preferences = listOf(
        "붐비는 정도" to "-1:조용 / 1:붐빔",
        "활동성" to "-1:관람형 / 1:체험형",
        "유행 민감도" to "-1:비유행 / 1:유행",
        "계획성" to "-1:즉흥 / 1:계획",
        "장소" to "-1:실외 / 1:실내",
        "분위기" to "-1:안정 / 1:도파민 추구"
    )
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        preferences.forEachIndexed { index, (title, label) ->
            // 제목 (녹색, 굵은 글씨, 중앙 정렬)
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF228B22),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            
            // 좌우 라벨과 슬라이더
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽 라벨
                Text(
                    text = label.split(" / ")[0].replace("-1:", ""),
                    fontSize = 12.5.sp,
                    color = Color.Gray,
                    modifier = Modifier.width(60.dp)
                )
                
                // 슬라이더
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Slider(
                        value = sliderValues[index],
                        onValueChange = { newValue ->
                            // 0.1 간격으로 반올림
                            val roundedValue = (newValue * 10).roundToInt() / 10f
                            sliderValues[index] = roundedValue
                        },
                        valueRange = -1f..1f,
                        steps = 19, // -1.0부터 1.0까지 0.1 간격으로 19단계 (총 21개 위치)
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // 오른쪽 라벨
                Text(
                    text = label.split(" / ")[1].replace("1:", ""),
                    fontSize = 12.5.sp,
                    color = Color.Gray,
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.End
                )
            }
            
            // 마지막 항목이 아닌 경우에만 가로선 추가
            if (index < preferences.size - 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE0E0E0))
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                // 마지막 항목인 경우 아래쪽 padding 추가
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
