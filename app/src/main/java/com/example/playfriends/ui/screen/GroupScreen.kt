package com.example.playfriends.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import com.example.playfriends.ui.component.AppTopBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun GroupScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF1FFF4)
    val cardColor = Color(0xFFFAFFFA)
    val titleColor = Color(0xFF228B22)
    val checkboxColor = Color(0xFF7E57C2)
    
    val options = listOf("놀이공원", "공방", "팝업", "방탈출 카페")
    var checkedStates by remember { mutableStateOf(List(options.size) { false }) }
    val scrollState = rememberScrollState()
    
    // 팝업 관련 상태
    var showPopup by remember { mutableStateOf(false) }
    val selectedContents = remember { mutableStateListOf<String>() }
    var selectedContentsCheckedStates by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }
    
    // 추가 컨텐츠 옵션들
    val additionalContents = listOf(
        "식당", "카페", "박물관", "영화관", "노래방", "볼링장", 
        "당구장", "보드게임카페", "만화카페", "PC방", "스포츠센터", "수영장"
    )

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
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal=20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text("그룹 1", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = titleColor)
                    Text("GROUP_1234", fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("25/06/20 14:30 - 22:00", fontSize = 16.sp, color = Color.Black)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "location", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("강남", fontSize = 16.sp)
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
                            Text("그룹 생성자 이름", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    val participants = listOf(
                        "참여자 1", "참여자 2", "참여자 3", "참여자 4", "참여자 5",
                        "참여자 6", "참여자 7", "참여자 8", "참여자 9", "참여자 10"
                    )
                    
                    participants.chunked(2).forEach { rowMembers ->
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
                                    Text(member, fontSize = 16.sp)
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

            Spacer(modifier = Modifier.height(30.dp))

            // 가로줄
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.height(20.dp))


            // 취향 분석 레포트 타이틀
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = titleColor, fontWeight = FontWeight.Bold)) {
                        append("그룹 1")
                    }
                    append("을 위한 취향 분석 레포트")
                },
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(15.dp))

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

            Spacer(modifier = Modifier.height(30.dp))

            // 놀이 콘텐츠 추천 카드
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = cardColor),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "취향에 맞춰 추천받은 컨텐츠",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = titleColor
                    )
                    Spacer(modifier = Modifier.height(12.dp))


                    
                    options.forEachIndexed { index, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = checkedStates[index],
                                onCheckedChange = { isChecked ->
                                    checkedStates = checkedStates.toMutableList().apply {
                                        this[index] = isChecked
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = checkboxColor),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = option,
                                fontSize = 16.sp
                            )
                        }
                        if (index < options.size - 1) {
                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // 선택한 컨텐츠가 있으면 표시
                    if (selectedContents.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "내가 선택한 컨텐츠",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = titleColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        selectedContents.forEach { content ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedContentsCheckedStates[content] ?: false,
                                    onCheckedChange = { isChecked ->
                                        selectedContentsCheckedStates = selectedContentsCheckedStates.toMutableMap().apply {
                                            put(content, isChecked)
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = checkboxColor),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(content, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { showPopup = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9CCC65)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 하단 버튼 (추천 컨텐츠 또는 선택한 컨텐츠가 하나라도 체크되어 있을 때만 표시)
            if (checkedStates.any { it } || selectedContentsCheckedStates.values.any { it }) {
                Button(
                    onClick = { navController.navigate("groupPlan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA6D8A8)),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text("상세 계획 추천 받으러 가기", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // 체크박스가 체크되면 자동으로 스크롤
            LaunchedEffect(checkedStates.any { it } || selectedContentsCheckedStates.values.any { it }) {
                if (checkedStates.any { it } || selectedContentsCheckedStates.values.any { it }) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }
        }
        
        // 컨텐츠 선택 팝업
        if (showPopup) {
            var tempSelectedContents by remember { mutableStateOf(selectedContents.toMutableList()) }
            
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showPopup = false },
                title = {
                    Text(
                        "컨텐츠 선택",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Box(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                    ) {
                        LazyColumn {
                            items(additionalContents) { content ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = tempSelectedContents.contains(content),
                                        onCheckedChange = { isChecked ->
                                            tempSelectedContents = if (isChecked) {
                                                tempSelectedContents.toMutableList().apply { add(content) }
                                            } else {
                                                tempSelectedContents.toMutableList().apply { remove(content) }
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = checkboxColor)
                                    )
                                    Text(content, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedContents.clear()
                            selectedContents.addAll(tempSelectedContents)
                            // 새로 추가된 컨텐츠들의 체크 상태를 true로 초기화 (체크된 상태로 표시)
                            tempSelectedContents.forEach { content ->
                                if (!selectedContentsCheckedStates.containsKey(content)) {
                                    selectedContentsCheckedStates = selectedContentsCheckedStates.toMutableMap().apply {
                                        put(content, true)
                                    }
                                }
                            }
                            showPopup = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = checkboxColor)
                    ) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showPopup = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("취소")
                    }
                }
            )
        }
    }
}
