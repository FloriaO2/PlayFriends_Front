package com.example.playfriends.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.playfriends.R
import com.example.playfriends.ui.component.AppTopBar
import com.example.playfriends.ui.component.HexagonGraph
import com.example.playfriends.ui.component.ScheduleTimeline
import com.example.playfriends.ui.viewmodel.GroupViewModel
import com.example.playfriends.ui.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

// 커스텀 스낵바 컴포저블 추가
@Composable
fun CustomSnackbar(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "앱 로고",
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = message,
            color = Color(0xFF228B22),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

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

    val group by groupViewModel.selectedGroup.collectAsState()
    val currentUser by userViewModel.user.collectAsState()
    val recommendedCategories by groupViewModel.recommendedCategories.collectAsState()
    val groupLeft by groupViewModel.groupLeft.collectAsState()

    var checkedStates by remember { mutableStateOf(mapOf<String, Boolean>()) }
    var showPopup by remember { mutableStateOf(false) }
    val selectedContents = remember { mutableStateListOf<String>() }

    val additionalContents = listOf(
        "식당", "카페", "박물관", "영화관", "노래방", "볼링장",
        "당구장", "보드게임카페", "만화카페", "PC방", "스포츠센터", "수영장"
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(groupId) {
        groupViewModel.getGroup(groupId)
        userViewModel.getCurrentUser()
    }

    LaunchedEffect(group, currentUser) {
        if (group != null && currentUser != null && group!!.owner_id == currentUser!!._id && group!!.schedule == null) {
            groupViewModel.recommendCategories(group!!._id)
        }
    }

    LaunchedEffect(recommendedCategories) {
        checkedStates = recommendedCategories.associateWith { false }
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
        containerColor = backgroundColor,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                CustomSnackbar(message = data.visuals.message)
            }
        }
    ) { padding ->
        val currentGroup = group
        val user = currentUser

        if (currentGroup == null || user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("그룹 정보를 불러오는 중입니다...")
            }
        } else {
            val isOwner = currentGroup.owner_id == user._id

            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(start = 30.dp, end = 30.dp, top = 10.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    GroupHeader(currentGroup, titleColor, clipboardManager, context)
                }

                item {
                    Spacer(modifier = Modifier.height(15.dp))
                    ParticipantsCard(currentGroup, cardColor) {
                        currentUser?._id?.let { userId ->
                            groupViewModel.handleLeaveOrDeleteGroup(userId)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(1.dp)
                            .background(Color(0xFFE0E0E0))
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    currentGroup.play_preferences?.let {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            HexagonGraph(
                                playPreferences = it,
                                modifier = Modifier.size(300.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    if (isOwner) {
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
                                    val allSelectedCategories = checkedStates.filter { it.value }.keys.toList()
                                    val categoriesString = allSelectedCategories.joinToString(",")
                                    navController.navigate("groupPlan/${currentGroup._id}?categories=$categoriesString")
                                },
                                showPopup = showPopup,
                                onDismissPopup = { showPopup = false },
                                additionalContents = additionalContents,
                                selectedContents = selectedContents.toList(),
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
                            ConfirmedScheduleUI(
                                schedule = currentGroup.schedule,
                                titleColor = titleColor,
                                distancesKm = currentGroup.distances_km
                            )
                        }
                    } else {
                        if (currentGroup.schedule != null) {
                            ConfirmedScheduleUI(
                                schedule = currentGroup.schedule,
                                titleColor = titleColor,
                                distancesKm = currentGroup.distances_km
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(vertical = 50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "아직 확정된 스케줄이 없습니다.",
                                    fontSize = 16.sp,
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

@Composable
fun GroupHeader(
    group: com.example.playfriends.data.model.GroupDetailResponse,
    titleColor: Color,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    context: android.content.Context
) {
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
}

@Composable
fun ParticipantsCard(
    group: com.example.playfriends.data.model.GroupDetailResponse,
    cardColor: Color,
    onLeaveClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                IconButton(onClick = onLeaveClick) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = "나가기",
                        tint = Color(0xFF942020),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            group.members.filter { it.id != group.owner_id }.chunked(2).forEach { rowMembers ->
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
                    if (rowMembers.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
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

        AlertDialog(
            onDismissRequest = onDismissPopup,
            title = { Text("컨텐츠 선택", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                val selected = additionalContents.filter { it in tempSelectedContents }
                val unselected = additionalContents.filterNot { it in tempSelectedContents }

                Box(modifier = Modifier.heightIn(max = 300.dp)) {
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
                ) { Text("확인") }
            },
            dismissButton = {
                Button(
                    onClick = onDismissPopup,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) { Text("취소") }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("취향에 맞춰 추천받은 컨텐츠", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = titleColor)
            Spacer(modifier = Modifier.height(12.dp))

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
fun ConfirmedScheduleUI(
    schedule: List<com.example.playfriends.data.model.ScheduledActivity>,
    titleColor: Color,
    distancesKm: List<Double>? = null
) {
    val activities = schedule.map { activity ->
        val timeInputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val timeOutputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val activityStart = try {
            timeOutputFormat.format(timeInputFormat.parse(activity.start_time) ?: "")
        } catch (e: Exception) { "" }
        val activityEnd = try {
            timeOutputFormat.format(timeInputFormat.parse(activity.end_time) ?: "")
        } catch (e: Exception) { "" }
        val timeStr = "$activityStart - $activityEnd"
        val emoji = when (activity.category) {
            "운동" -> "🏀"
            "카페" -> "☕"
            "공연" -> "🎵"
            "쇼핑" -> "🛒"
            "점심" -> "🍜"
            "저녁" -> "🍖"
            "노래방" -> "🎤"
            "식당" -> "🍽️"
            "영화관" -> "🎬"
            "박물관" -> "🏛️"
            else -> "📍"
        }
        Triple(timeStr, activity.name, emoji)
    }
    val moves = distancesKm?.map { distance -> "${(distance * 1000).toInt()}m" } ?: emptyList()
    val moveWalkColor = Color(0xFFF1EFE9)
    val moveSubwayColor = Color(0xFFC9EDD8)
    val chipColor = Color(0xFFE0F8CD)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "확정된 스케줄",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
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
