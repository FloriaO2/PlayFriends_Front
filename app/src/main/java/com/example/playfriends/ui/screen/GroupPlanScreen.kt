package com.example.playfriends.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.playfriends.data.model.ScheduleSuggestionOutput
import com.example.playfriends.data.model.ScheduledActivity
import com.example.playfriends.ui.component.AppTopBar
import com.example.playfriends.ui.viewmodel.GroupViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun GroupPlanScreen(
    navController: NavController,
    groupId: String,
    categories: List<String>,
    groupViewModel: GroupViewModel = viewModel()
) {
    val backgroundColor = Color(0xFFF1FFF4)
    val cardColor = Color(0xFFFAFFFA)
    val titleColor = Color(0xFF228B22)
    val chipColor = Color(0xFFE0F8CD)
    val moveWalkColor = Color(0xFFF1EFE9)
    val moveSubwayColor = Color(0xFFC9EDD8)
    val scrollState = rememberScrollState()

    val selectedGroup by groupViewModel.selectedGroup.collectAsState()
    val scheduleSuggestions by groupViewModel.scheduleSuggestions.collectAsState()
    val groupOperationState by groupViewModel.groupOperationState.collectAsState()

    LaunchedEffect(Unit) {
        groupViewModel.clearScheduleSuggestions()
        groupViewModel.getGroup(groupId)
        if (categories.isNotEmpty()) {
            groupViewModel.createScheduleSuggestions(groupId, categories)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onLogoClick = { navController.navigate("home") },
                onProfileClick = { navController.navigate("profile") },
                profileInitial = "A"
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 그룹명 + 그룹코드 + 시간/위치
            selectedGroup?.let { group ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal=20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(group.groupname, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = titleColor)
                        Text(group._id, fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        val formatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm")
                        val startTime = LocalDateTime.parse(group.starttime).format(formatter)
                        val endTime = group.endtime?.let { LocalDateTime.parse(it).format(formatter) } ?: ""
                        Text("$startTime - $endTime", fontSize = 16.sp, color = Color.Black)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "location", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("대전", fontSize = 16.sp) // 위치 정보는 아직 모델에 없습니다.
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 참여자 카드
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = cardColor),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 그룹 생성자와 나가기 버튼
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            val ownerName = selectedGroup?.members?.find { it.id == selectedGroup?.owner_id }?.name ?: "Unknown"
                            Text(ownerName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { /* TODO: 그룹 나가기 로직 */ }
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "나가기",
                                tint = Color(0xFF942020),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 참여자들 (2열로 표시)
                    selectedGroup?.members?.chunked(2)?.forEach { rowMembers ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            rowMembers.forEach { member ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(member.name, fontSize = 16.sp)
                                }
                            }
                            // 홀수 개일 때 두 번째 열을 빈 공간으로 채움
                            if (rowMembers.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 가로줄
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 로딩 인디케이터 또는 계획 카드
            if (groupOperationState is GroupViewModel.GroupOperationState.Loading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // 계획 카드들
                LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(max = 1500.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(scheduleSuggestions.size) { index ->
                        val suggestion = scheduleSuggestions[index]
                        PlanCard(
                            planTitle = "계획 ${index + 1}",
                            suggestion = suggestion,
                            chipColor = chipColor,
                            moveWalkColor = moveWalkColor,
                            moveSubwayColor = moveSubwayColor,
                            navController = navController,
                            groupViewModel = groupViewModel
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PlanCard(
    planTitle: String,
    suggestion: ScheduleSuggestionOutput,
    chipColor: Color,
    moveWalkColor: Color,
    moveSubwayColor: Color,
    navController: NavController,
    groupViewModel: GroupViewModel
) {
    val cardColor = Color(0xFFFAFFFA)

    Card(
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = cardColor),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x728ACB97))
                    .padding(16.dp)
            ) {
                Text(
                    planTitle, 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 22.sp, 
                    color = Color(0xA6215421),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 세로 배치 타임라인
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                VerticalScheduleTimeline(
                    timeline = suggestion.scheduled_activities,
                    chipColor = chipColor,
                    moveWalkColor = moveWalkColor,
                    moveSubwayColor = moveSubwayColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Button(
                    onClick = {
                        groupViewModel.confirmSchedule(suggestion)
                        // GroupScreen으로 돌아가기
                        navController.navigate("group") {
                            popUpTo("groupPlan") { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA6D8A8)),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("확정하기", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun VerticalScheduleTimeline(
    timeline: List<ScheduledActivity>,
    chipColor: Color,
    moveWalkColor: Color,
    moveSubwayColor: Color
) {
    val timelineX = 20.dp // 수직선을 왼쪽으로 이동
    val timelineWidth = 2.dp
    val dotSize = 14.dp
    val rowHeight = 60.dp
    val timelineColor = Color.Gray
    
    val totalRows = timeline.size
    val dotRows = timeline.size

    // 고정된 가로 길이 계산
    val iconSize = 16.dp // 아이콘(이모지) 크기
    val iconTextSpacing = 8.dp // 아이콘과 텍스트 사이 공백
    val textWidth = 60.dp // 텍스트 예상 너비
    val rightPadding = 16.dp // 오른쪽 여백
    
    // 전체 가로 길이 = 수직선과 아이콘 사이 공백 + 아이콘 크기 + 아이콘과 텍스트 사이 공백 + 텍스트 크기 + 오른쪽 여백
    val timelineWidthTotal = 16.dp + iconSize + iconTextSpacing + textWidth + rightPadding

    // 점의 중앙 y좌표 계산
    val dotCenters = List(dotRows) { i ->
        i * rowHeight + rowHeight / 2
    }
    
    val timelineStart = if (dotCenters.isNotEmpty()) dotCenters.first() else 0.dp
    val timelineEnd = if (dotCenters.isNotEmpty()) dotCenters.last() else 0.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(timelineWidthTotal)
                .height(totalRows * rowHeight)
        ) {
            // 수직선
            if (dotCenters.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .width(timelineWidth)
                        .height(timelineEnd - timelineStart)
                        .offset(x = timelineX, y = timelineStart)
                        .background(timelineColor)
                )
            }
            
            // 각 일정
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                timeline.forEachIndexed { index, item ->
                    Box(Modifier.height(rowHeight)) {
                        // 점: 일정 줄에만 표시
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .offset(x = timelineX - dotSize / 2)
                                .align(Alignment.CenterStart)
                                .background(Color.White, CircleShape)
                                .border(3.dp, Color.Black, CircleShape)
                        )
                        
                        // 컨텐츠 (이모지 + 라벨)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = timelineX + 24.dp)
                                .width(textWidth + iconSize + iconTextSpacing)
                        ) {
                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val startTime = LocalDateTime.parse(item.start_time).format(formatter)
                            val endTime = LocalDateTime.parse(item.end_time).format(formatter)

                            Text(
                                text = when (item.category) {
                                    "운동" -> "🏀"
                                    "카페" -> "☕"
                                    "공연" -> "🎵"
                                    "쇼핑" -> "🛒"
                                    "점심" -> "🍜"
                                    "저녁" -> "🍖"
                                    "노래방" -> "🎤"
                                    else -> "📍"
                                },
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = item.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "$startTime - $endTime",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
