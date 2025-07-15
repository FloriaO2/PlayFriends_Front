package com.example.playfriends.ui.screen

import android.util.Log
import kotlin.math.roundToInt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.draw.clip
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playfriends.data.model.*
import com.example.playfriends.data.model.enums.*
import com.example.playfriends.ui.viewmodel.UserViewModel

@Composable
fun TestScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val tabTitles = listOf("음식 취향", "컨텐츠 취향")
    var selectedTabIndex by remember { mutableStateOf(0) }

    val user by userViewModel.user.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    // LaunchedEffect를 사용하여 화면이 처음 로드될 때 사용자 정보를 가져옵니다.
    LaunchedEffect(Unit) {
        userViewModel.getCurrentUser()
    }

    // user가 변경될 때마다 이 상태들을 다시 계산합니다.
    val foodPreferences = remember { mutableStateMapOf<String, Int>() }
    val contentSliderValues = remember { mutableStateMapOf<String, Float>() }

    // 사용자 데이터가 로드되면 상태 맵을 업데이트합니다.
    LaunchedEffect(user) {
        Log.d("", "user: ${user}")
        user?.let { currentUser ->
            // Food Preferences 업데이트
            foodPreferences.clear()
            currentUser.food_preferences.ingredients.forEach { pref -> foodPreferences["재료_${pref.name.value}"] = pref.score.roundToInt() }
            currentUser.food_preferences.tastes.forEach { pref -> foodPreferences["맛_${pref.name.value}"] = pref.score.roundToInt() }
            currentUser.food_preferences.cooking_methods.forEach { pref -> foodPreferences["조리 방법_${pref.name.value}"] = pref.score.roundToInt() }
            currentUser.food_preferences.cuisine_types.forEach { pref -> foodPreferences["요리 유형_${pref.name.value}"] = pref.score.roundToInt() }

            // Play Preferences 업데이트
            contentSliderValues.clear()
            contentSliderValues["붐비는 정도"] = currentUser.play_preferences.crowd_level
            contentSliderValues["활동성"] = currentUser.play_preferences.activeness_level
            contentSliderValues["유행 민감도"] = currentUser.play_preferences.trend_level
            contentSliderValues["계획성"] = currentUser.play_preferences.planning_level
            contentSliderValues["장소"] = currentUser.play_preferences.location_preference
            contentSliderValues["분위기"] = currentUser.play_preferences.vibe_level
        }
    }

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
                    // 무조건 ProfileScreen으로 이동 (popUpTo 제거)
                    navController.navigate("profile")
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
                    val foodPrefs = FoodPreferences(
                        ingredients = foodPreferences.filterKeys { it.startsWith("재료_") }.map { (key, value) ->
                            val enumValue = key.substringAfter("재료_")
                            IngredientPreference(FoodIngredient.values().first { it.value == enumValue }, value.toFloat())
                        },
                        tastes = foodPreferences.filterKeys { it.startsWith("맛_") }.map { (key, value) ->
                            val enumValue = key.substringAfter("맛_")
                            TastePreference(FoodTaste.values().first { it.value == enumValue }, value.toFloat())
                        },
                        cooking_methods = foodPreferences.filterKeys { it.startsWith("조리 방법_") }.map { (key, value) ->
                            val enumValue = key.substringAfter("조리 방법_")
                            CookingMethodPreference(FoodCookingMethod.values().first { it.value == enumValue }, value.toFloat())
                        },
                        cuisine_types = foodPreferences.filterKeys { it.startsWith("요리 유형_") }.map { (key, value) ->
                            val enumValue = key.substringAfter("요리 유형_")
                            CuisineTypePreference(FoodCuisineType.values().first { it.value == enumValue }, value.toFloat())
                        }
                    )

                    val playPrefs = PlayPreferences(
                        crowd_level = contentSliderValues["붐비는 정도"] ?: 0f,
                        activeness_level = contentSliderValues["활동성"] ?: 0f,
                        trend_level = contentSliderValues["유행 민감도"] ?: 0f,
                        planning_level = contentSliderValues["계획성"] ?: 0f,
                        location_preference = contentSliderValues["장소"] ?: 0f,
                        vibe_level = contentSliderValues["분위기"] ?: 0f
                    )

                    // updatePreferences에 콜백 전달: 성공 시에만 이동 (popUpTo 제거)
                    userViewModel.updatePreferences(foodPrefs, playPrefs) {
                        navController.navigate("profile")
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
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                when (selectedTabIndex) {
                    0 -> FoodPreferenceTab(navController, foodPreferences)
                    1 -> ContentPreferenceTab(navController, contentSliderValues)
                }
            }
        }
    }
}

@Composable
fun FoodPreferenceTab(
    navController: NavController,
    foodPreferences: MutableMap<String, Int>
) {
    val categories = listOf(
        "재료" to FoodIngredient.values().map { it.value },
        "맛" to FoodTaste.values().map { it.value },
        "조리 방법" to FoodCookingMethod.values().map { it.value },
        "요리 유형" to FoodCuisineType.values().map { it.value }
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
                    .padding(vertical = 4.dp)
                    .padding(horizontal = 12.dp), // 좌우 패딩 추가
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
                    // 기존: options.chunked(3).forEachIndexed { ... Row { ... PreferenceItem ... } }
                    // 변경: 한 줄에 하나씩만 보이도록 Column으로 배치
                    options.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PreferenceItem(
                                option = option,
                                category = title,
                                foodPreferences = foodPreferences
                            )
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
    foodPreferences: MutableMap<String, Int>
) {
    val key = "${category}_${option}"
    val preferenceLevel = foodPreferences[key] as? Int ?: 0 // -1: 불호, 0: 무난, 1: 호

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(horizontal = 8.dp)
    ) {
        // 텍스트를 좌측에 정렬
        Text(
            text = option,
            fontSize = 15.sp, // 크기 키움
            color = when (preferenceLevel) {
                1 -> Color(0xFF228B22)
                -1 -> Color(0xFFE57373)
                else -> Color(0xFF666666)
            },
            fontWeight = if (preferenceLevel != 0) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1f), // 좌측 정렬, 남는 공간 차지
            textAlign = TextAlign.Start
        )
        // 버튼을 우측 끝에 정렬
        ThreeWaySwitch(
            value = preferenceLevel,
            onValueChange = { foodPreferences[key] = it },
            modifier = Modifier // 우측 끝 정렬
        )
    }
}

@Composable
fun ContentPreferenceTab(navController: NavController, sliderValues: MutableMap<String, Float>) {
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
                        value = sliderValues[title] ?: 0f,
                        onValueChange = { newValue ->
                            // 0.1 간격으로 반올림
                            val roundedValue = (newValue * 10).roundToInt() / 10f
                            sliderValues[title] = roundedValue
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

@Composable
fun ThreeWaySwitch(
    value: Int, // -1, 0, 1
    onValueChange: (Int) -> Unit,
    labels: List<String> = listOf("불호", "무난", "호"),
    modifier: Modifier = Modifier
) {
    val selectedColors = listOf(Color(0xFFE57373), Color(0xFF4B4B4B), Color(0xFF228B22))
    val unselectedColor = Color(0xFFE0E0E0)
    Row(
        modifier = modifier
            .height(24.dp)
            .width(180.dp) // 가로 길이 살짝 줄임
            .clip(RoundedCornerShape(16.dp))
            .background(unselectedColor)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
    ) {
        listOf(-1, 0, 1).forEachIndexed { idx, level ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (value == level) selectedColors[idx] else Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onValueChange(level) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labels[idx],
                    color = if (value == level) {
                        if (level == 0) Color.White else Color.White
                    } else Color.Black,
                    fontWeight = if (value == level) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp // 텍스트 크기 키움
                )
            }
            // 세로 구분선 제거: 아무것도 하지 않음
        }
    }
}
