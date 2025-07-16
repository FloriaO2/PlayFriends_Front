package com.example.playfriends.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun GroupCardHeader(
    groupName: String,
    time: String,
    location: String,
    memberCount: Int = 0,
    titleColor: Color = Color(0xFF2B8A3E),
    onMemberClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = groupName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Members",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onMemberClick() }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = time,
                fontSize = 13.5.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentWidth()
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = Color.Black,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = location,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
fun GroupCard(
    groupName: String,
    time: String,
    location: String,
    activities: List<Triple<String, String, String>>,
    moves: List<String>,
    onClick: () -> Unit,
    cardColor: Color,
    titleColor: Color,
    chipColor: Color,
    moveWalkColor: Color,
    moveSubwayColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 그룹 카드 헤더
            GroupCardHeader(
                groupName = groupName,
                time = time,
                location = location,
                titleColor = titleColor,
                onMemberClick = { /* 그룹 멤버 보기 다이얼로그 표시 */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 활동 스케줄
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ScheduleTimeline(
                    activities = activities,
                    moves = moves,
                    moveColors = List(moves.size) { if (it % 2 == 0) moveWalkColor else moveSubwayColor },
                    moveIcons = List(moves.size) { if (it % 2 == 0) "🚶" else "🚇" },
                    chipColor = chipColor
                )
            }
        }
    }
}

@Composable
fun ScheduleTimeline(
    activities: List<Triple<String, String, String>>,
    moves: List<String>,
    moveColors: List<Color>,
    moveIcons: List<String>,
    chipColor: Color,
    leftChipWidth: Dp = 0.dp // 좌측 거리 영역 제거
) {
    val timelineWidth = 2.dp
    val dotSize = 14.dp
    val rowHeight = 40.dp
    val timelineColor = Color.Gray
    val dotRows = activities.size
    val density = LocalDensity.current
    var rightMaxWidthPx by remember { mutableStateOf(0) }
    val additionalPaddingPx = with(density) { 16.dp.toPx() }.toInt()

    // 점의 중앙 y좌표 계산용
    val dotCenters = List(dotRows) { i ->
        var y = 0.dp
        for (j in 0 until i) {
            y += rowHeight
        }
        y += rowHeight / 2
        y
    }
    val timelineStart = if (dotCenters.isNotEmpty()) dotCenters.first() else 0.dp
    val timelineEnd = if (dotCenters.isNotEmpty()) dotCenters.last() else 0.dp

    val timelineWidthTotal = with(density) { (rightMaxWidthPx + 24).toDp() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start // 좌측 정렬
    ) {
        Box(
            modifier = Modifier
                .width(timelineWidthTotal)
                .height(rowHeight * activities.size)
        ) {
            // 수직선(한 번만 그림, 시작/끝을 점의 중앙에 맞춤)
            if (dotCenters.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .width(timelineWidth)
                        .height(timelineEnd - timelineStart)
                        .offset(x = 0.dp, y = timelineStart)
                        .background(timelineColor)
                )
            }
            // 각 Row
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (activityIdx in activities.indices) {
                    Box(Modifier.height(rowHeight)) {
                        // 점: 활동(일정) 줄에만 표시
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .offset(x = -dotSize / 2)
                                .align(Alignment.CenterStart)
                                .background(Color.White, CircleShape)
                                .border(3.dp, Color.Black, CircleShape)
                        )
                        // 일정(왼쪽에 붙여서 출력)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .offset(x = 24.dp)
                                .wrapContentWidth()
                                .onGloballyPositioned { coordinates ->
                                    rightMaxWidthPx = maxOf(rightMaxWidthPx, coordinates.size.width + additionalPaddingPx)
                                }
                        ) {
                            Text(activities[activityIdx].first, fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(chipColor)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("${activities[activityIdx].third} ${activities[activityIdx].second}", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
} 