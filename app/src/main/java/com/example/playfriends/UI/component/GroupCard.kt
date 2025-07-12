package com.example.playfriends.UI.component

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border

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
                fontSize = 14.sp,
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
            activities.forEachIndexed { index, activity ->
                ScheduleItem(
                    time = activity.first,
                    label = activity.second,
                    icon = activity.third,
                    moveTime = if (index < moves.size) moves[index] else null,
                    moveColor = if (index % 2 == 0) moveWalkColor else moveSubwayColor,
                    moveIcon = if (index % 2 == 0) "🚶" else "🚇",
                    chipColor = chipColor
                )
            }
        }
    }
}

@Composable
fun ScheduleItem(
    time: String,
    label: String,
    icon: String,
    moveTime: String? = null,
    moveColor: Color? = null,
    moveIcon: String? = null,
    chipColor: Color = Color(0xFFE0F8CD)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        // 좌측: 이동수단 + 시간
        if (moveTime != null) {
            Surface(
                shape = RoundedCornerShape(50),
                color = moveColor ?: Color.LightGray,
                modifier = Modifier.wrapContentWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(moveIcon ?: "", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(moveTime, fontSize = 12.sp)
                }
            }
        } else {
            Spacer(modifier = Modifier.width(60.dp))
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 중앙: 수직선과 점
        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            // 수직선
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray)
            )
            
            // 점 (수직선 중앙에)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // 우측: 시간과 일정
        Column {
            Text(time, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(chipColor)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("$icon $label", fontSize = 14.sp)
            }
        }
    }
} 