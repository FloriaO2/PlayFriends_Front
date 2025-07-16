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
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

import com.example.playfriends.ui.viewmodel.UserViewModel

@Composable
fun GroupPlanScreen(
    navController: NavController,
    groupId: String,
    categories: List<String>,
    groupViewModel: GroupViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
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
    val groupLeft by groupViewModel.groupLeft.collectAsState()
    val currentUser by userViewModel.user.collectAsState()

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        userViewModel.getCurrentUser()
        groupViewModel.clearScheduleSuggestions()
        groupViewModel.getGroup(groupId)
        if (categories.isNotEmpty()) {
            groupViewModel.createScheduleSuggestions(groupId, categories)
        }
    }

    LaunchedEffect(groupLeft) {
        if (groupLeft) {
            Toast.makeText(context, "그룹을 나갔습니다.", Toast.LENGTH_SHORT).show()
            navController.navigate("home") {
                popUpTo("home") { inclusive = true }
            }
            groupViewModel.onGroupLeftHandled()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onLogoClick = { navController.navigate("home") },
                onProfileClick = { navController.navigate("profile") },
                profileInitial = currentUser?.username?.first()?.toString() ?: "A"
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 그룹명/날짜 + 초대코드/위치 (2줄로 분리, 양끝 배치)
            selectedGroup?.let { group ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            group.groupname,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        // 일정 포맷팅: 25/07/20 00:00 - 25/07/20 20:00
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("yy/MM/dd HH:mm", Locale.getDefault())
                        val start = try {
                            outputFormat.format(inputFormat.parse(group.starttime) ?: "")
                        } catch (e: Exception) { "" }
                        val end = try {
                            group.endtime?.let { outputFormat.format(inputFormat.parse(it) ?: "") } ?: ""
                        } catch (e: Exception) { "" }
                        val timeStr = if (end.isNotBlank()) "$start - $end" else start
                        Text(
                            timeStr,
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        )
                    }
                    // 하단: 초대코드 - 위치
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(group._id))
                                Toast.makeText(context, "복사가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                            modifier = Modifier.height(IntrinsicSize.Min)
                        ) {
                            Text(group._id, fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "location", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("대전", fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // 참여자 카드
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = cardColor),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    (group.members.find { it.id == group.owner_id }?.name ?: "Unknown") + " (방장)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = {
                                currentUser?._id?.let { userId ->
                                    groupViewModel.handleLeaveOrDeleteGroup(userId)
                                }
                            }) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "나가기", tint = Color(0xFF942020), modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        group.members.chunked(2).forEach { rowMembers ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                rowMembers.forEach { member ->
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(member.name, fontSize = 16.sp)
                                    }
                                }
                                if (rowMembers.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE0E0E0)))
                Spacer(modifier = Modifier.height(20.dp))
            }

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
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            ) {
                VerticalScheduleTimeline(
                    timeline = suggestion.scheduled_activities,
                    chipColor = chipColor,
                    moveWalkColor = moveWalkColor,
                    moveSubwayColor = moveSubwayColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
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
    val timelineX = 0.dp // 수직선을 카드의 왼쪽에 최대한 붙임
    val timelineWidth = 2.dp
    val dotSize = 14.dp
    val rowHeight = 72.dp
    val timelineColor = Color.Gray

    val totalRows = timeline.size
    val dotRows = timeline.size

    // 고정된 가로 길이 계산
    val iconSize = 16.dp // 아이콘(이모지) 크기
    val iconTextSpacing = 8.dp // 아이콘과 텍스트 사이 공백
    val textWidth = 60.dp // 텍스트 예상 너비
    val rightPadding = 0.dp // 오른쪽 여백 최소화
    val timelineWidthTotal = 96.dp // 전체 가로 길이 더 줄임

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
                                .offset(x = timelineX + 8.dp)
                                .fillMaxWidth()
                        ) {
                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val startTime = LocalDateTime.parse(item.start_time).format(formatter)
                            val endTime = LocalDateTime.parse(item.end_time).format(formatter)

                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Box(Modifier.fillMaxWidth()) {
                                    Text(
                                        text = item.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 15.sp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.TopStart)
                                            .padding(start = 16.dp)
                                    )
                                    Text(
                                        text = when (item.category) {
                                            "식당" -> "🍚"
                                            "주점" -> "🍺"
                                            "카페" -> "☕"
                                            else -> "📍"
                                        },
                                        fontSize = 12.sp,
                                        lineHeight = 15.sp,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                    )
                                }
                                Text(
                                    text = "$startTime - $endTime",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    lineHeight = 12.sp,
                                    modifier = Modifier.padding(top = 0.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
