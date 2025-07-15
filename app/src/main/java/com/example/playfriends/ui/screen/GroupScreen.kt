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
import com.example.playfriends.ui.component.HexagonGraph
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playfriends.ui.viewmodel.GroupViewModel
import com.example.playfriends.ui.viewmodel.UserViewModel
import android.util.Log

@Composable
fun GroupScreen(
    navController: NavController,
    groupId: String,
    groupViewModel: GroupViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val backgroundColor = Color(0xFFF1FFF4)
    val cardColor = Color(0xFFFAFFFA)
    val titleColor = Color(0xFF228B22)
    val checkboxColor = Color(0xFF7E57C2)
    val scrollState = rememberScrollState()

    val group by groupViewModel.selectedGroup.collectAsState()
    val currentUser by userViewModel.user.collectAsState()
    val recommendedCategories by groupViewModel.recommendedCategories.collectAsState()

    var checkedStates by remember { mutableStateOf(mapOf<String, Boolean>()) }

    // 팝업 관련 상태
    var showPopup by remember { mutableStateOf(false) }
    val selectedContents = remember { mutableStateListOf<String>() }
    var selectedContentsCheckedStates by remember { mutableStateOf(mutableMapOf<String, Boolean>()) }

    // 추가 컨텐츠 옵션들
    val additionalContents = listOf(
        "식당", "카페", "박물관", "영화관", "노래방", "볼링장",
        "당구장", "보드게임카페", "만화카페", "PC방", "스포츠센터", "수영장"
    )

    LaunchedEffect(Unit) {
        groupViewModel.getGroup(groupId)
        userViewModel.getCurrentUser()
    }

    LaunchedEffect(group, currentUser) {
        Log.d("GroupScreen", "Group data updated: $group")
        if (group != null && currentUser != null && group!!.owner_id == currentUser!!._id && group!!.schedule == null) {
            groupViewModel.recommendCategories(group!!._id)
        }
    }

    LaunchedEffect(recommendedCategories) {
        checkedStates = recommendedCategories.associateWith { false }
    }

    LaunchedEffect(group) {
        Log.d("GroupScreen", "Group state updated in UI: $group")
    }

    LaunchedEffect(checkedStates.any { it.value } || selectedContentsCheckedStates.values.any { it }) {
                if (checkedStates.any { it.value } || selectedContentsCheckedStates.values.any { it }) {
                    scrollState.animateScrollTo(scrollState.maxValue)
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
        val currentGroup = group
        val user = currentUser

        if (currentGroup == null || user == null) {
            // 로딩 또는 에러 처리
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("그룹 정보를 불러오는 중입니다...")
            }
        } else {
            val isOwner = currentGroup.owner_id == user._id
            Log.d("GroupScreen", "isOwner: $isOwner, currentUser: ${user._id}, groupOwner: ${currentGroup.owner_id}")

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(currentGroup.groupname, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = titleColor)
                        Text(currentGroup._id, fontSize = 12.sp, color = Color.Gray) // 그룹 코드를 ID로 임시 대체
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(currentGroup.starttime, fontSize = 16.sp, color = Color.Black) // 시간 포맷팅 필요
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "location", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("대전", fontSize = 16.sp) // 위치 정보 필요
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(currentGroup.members.find { it.id == currentGroup.owner_id }?.name ?: "Unknown", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { /* TODO: 그룹 나가기 로직 */ }) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "나가기", tint = Color(0xFF942020), modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        currentGroup.members.chunked(2).forEach { rowMembers ->
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

                // PlayPreferences를 이용한 육각형 그래프
                currentGroup.play_preferences?.let {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        HexagonGraph(
                            playPreferences = it,
                            modifier = Modifier
                                .size(300.dp) // 그래프 크기 조정
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                if (isOwner) {
                    // Owner UI
                    if (currentGroup.schedule == null) {
                        OwnerScheduleCreationUI(
                            groupName = currentGroup.groupname,
                            titleColor = titleColor,
                            cardColor = cardColor,
                            checkboxColor = checkboxColor,
                            recommendedCategories = recommendedCategories,
                            checkedStates = checkedStates,
                            onCheckedChange = { newStates -> checkedStates = newStates },
                            onEditClick = { showPopup = true },
                            onRecommendClick = {
                                val selected = checkedStates.filter { it.value }.keys.toList()
                                groupViewModel.createScheduleSuggestions(currentGroup._id, selected)
                                navController.navigate("groupPlan")
                            },
                            showPopup = showPopup,
                            onDismissPopup = { showPopup = false },
                            additionalContents = additionalContents,
                            selectedContents = selectedContents,
                        onUpdateSelectedContents = { contents ->
                            selectedContents.clear()
                            selectedContents.addAll(contents)
                            val newCheckedStates = checkedStates.toMutableMap()
                            contents.forEach { content ->
                                if (!newCheckedStates.containsKey(content)) {
                                    newCheckedStates[content] = true
                                }
                            }
                            checkedStates = newCheckedStates
                        }
                        )
                    } else {
                        // Owner & Schedule exists
                        ConfirmedScheduleUI(schedule = currentGroup.schedule, titleColor = titleColor)
                    }
                } else {
                    // Member UI
                    if (currentGroup.schedule != null) {
                        ConfirmedScheduleUI(schedule = currentGroup.schedule, titleColor = titleColor)
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 50.dp), contentAlignment = Alignment.Center) {
                            Text("아직 확정된 스케줄이 없습니다.", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerScheduleCreationUI(
    groupName: String,
    titleColor: Color,
    cardColor: Color,
    checkboxColor: Color,
    recommendedCategories: List<String>,
    checkedStates: Map<String, Boolean>,
    onCheckedChange: (Map<String, Boolean>) -> Unit,
    onEditClick: () -> Unit,
    onRecommendClick: () -> Unit,
    showPopup: Boolean,
    onDismissPopup: () -> Unit,
    additionalContents: List<String>,
    selectedContents: List<String>,
    onUpdateSelectedContents: (List<String>) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 취향 분석 레포트 타이틀
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = titleColor, fontWeight = FontWeight.Bold)) {
                append(groupName)
                }
                append("을 위한 취향 분석 레포트")
            },
            fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
    }

    if (showPopup) {
        var tempSelectedContents by remember(selectedContents) { mutableStateOf(selectedContents.toMutableList()) }

        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissPopup,
            title = {
                Text(
                    "컨텐츠 선택",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                val selected = additionalContents.filter { it in tempSelectedContents }
                val unselected = additionalContents.filterNot { it in tempSelectedContents }

                Box(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                ) {
                    LazyColumn {
                        item {
                            Text("선택됨", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(selected) { content ->
                            SelectableContentRow(
                                content = content,
                                isSelected = true,
                                checkboxColor = checkboxColor,
                                onCheckedChange = {
                                    tempSelectedContents = tempSelectedContents.toMutableList().apply { remove(content) }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("추가하기", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(unselected) { content ->
                            SelectableContentRow(
                                content = content,
                                isSelected = false,
                                checkboxColor = checkboxColor,
                                onCheckedChange = {
                                    tempSelectedContents = tempSelectedContents.toMutableList().apply { add(content) }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSelectedContents(tempSelectedContents)
                        onDismissPopup()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = checkboxColor)
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismissPopup,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("취소")
                }
            }
        )
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
            Text("취향에 맞춰 추천받은 컨텐츠", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = titleColor)
            Spacer(modifier = Modifier.height(12.dp))

            val allContents = (recommendedCategories + selectedContents).distinct()
            recommendedCategories.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(
                        checked = checkedStates[option] ?: false,
                        onCheckedChange = { isChecked ->
                            onCheckedChange(checkedStates.toMutableMap().apply { this[option] = isChecked })
                        },
                        colors = CheckboxDefaults.colors(checkedColor = checkboxColor),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(7.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("내가 추가한 컨텐츠", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = titleColor)
            Spacer(modifier = Modifier.height(12.dp))

            selectedContents.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Checkbox(
                        checked = checkedStates[option] ?: false,
                        onCheckedChange = { isChecked ->
                            onCheckedChange(checkedStates.toMutableMap().apply { this[option] = isChecked })
                        },
                        colors = CheckboxDefaults.colors(checkedColor = checkboxColor),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(7.dp))
            }

            Button(
                onClick = onEditClick,
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

    if (checkedStates.any { it.value }) {
        Button(
            onClick = onRecommendClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA6D8A8)),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text("상세 계획 추천 받으러 가기", color = Color.White, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SelectableContentRow(
    content: String,
    isSelected: Boolean,
    checkboxColor: Color,
    onCheckedChange: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onCheckedChange() },
            colors = CheckboxDefaults.colors(checkedColor = checkboxColor)
        )
        Text(content, fontSize = 16.sp)
    }
}

@Composable
fun ConfirmedScheduleUI(schedule: List<com.example.playfriends.data.model.ScheduledActivity>, titleColor: Color) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "확정된 스케줄",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                schedule.forEach { activity ->
                    Text("${activity.start_time} - ${activity.end_time}: ${activity.name}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
