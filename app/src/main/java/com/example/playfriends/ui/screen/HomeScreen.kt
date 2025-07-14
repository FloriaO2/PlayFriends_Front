@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.playfriends.ui.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.example.playfriends.ui.component.GroupCardHeader
import com.example.playfriends.ui.component.AppTopBar
import com.example.playfriends.ui.component.ScheduleTimeline
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.playfriends.R
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Slider
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playfriends.ui.viewmodel.GroupViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import android.app.DatePickerDialog
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val fabExpanded = remember { mutableStateOf(false) }
    val backgroundColor = Color(0xFFF1FFF4)
    val cardBackground = Color(0xFFFAFFFA)
    val titleColor = Color(0xFF2B8A3E)
    val chipColor = Color(0xFFE0F8CD)
    val moveWalkColor = Color(0xFFF1EFE9)
    val moveSubwayColor = Color(0xFFC9EDD8)

    // 현재 열린 그룹을 추적하는 상태
    var expandedGroupId by remember { mutableStateOf<String?>(null) }

    // 팝업 관련 상태
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var showJoinGroupDialog by remember { mutableStateOf(false) }
    var showGroupCreatedDialog by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf("") }
    var showInputErrorDialog by remember { mutableStateOf(false) }
    var popupInputError by remember { mutableStateOf<String?>(null) }

    val groupViewModel: GroupViewModel = viewModel()
    val groupOperationState by groupViewModel.groupOperationState.collectAsState()
    val selectedGroup by groupViewModel.selectedGroup.collectAsState()
    var createdGroupId by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var navigateToHome by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            AppTopBar(
                onLogoClick = { navController.navigate("home") },
                onProfileClick = { navController.navigate("profile") }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(
                    visible = fabExpanded.value,
                    enter = slideInVertically(
                        animationSpec = tween(300),
                        initialOffsetY = { it / 2 }
                    ) + scaleIn(
                        animationSpec = tween(300),
                        initialScale = 0.3f
                    ) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { it / 2 }
                    ) + scaleOut(
                        animationSpec = tween(300),
                        targetScale = 0.3f
                    ) + fadeOut(animationSpec = tween(300))
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Button(
                            onClick = {
                                showJoinGroupDialog = true
                                fabExpanded.value = false
                            },
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .height(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color(0x33000000),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9E9DC)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Join Group", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                        Button(
                            onClick = {
                                showCreateGroupDialog = true
                                fabExpanded.value = false
                            },
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .height(48.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color(0x33000000),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9E9DC)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Create Group", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { fabExpanded.value = !fabExpanded.value },
                    containerColor = Color(0xFF4C6A57)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 그룹 데이터 정의
            val groups = listOf(
                GroupData(
                    id = "group1",
                    name = "그룹 1",
                    time = "25/06/20 14:30 - 22:00",
                    location = "강남",
                    activities = listOf(
                        Triple("14:30 - 15:30", "운동", "🏀"),
                        Triple("15:40 - 16:40", "카페", "☕"),
                        Triple("16:50 - 19:30", "공연", "🎵"),
                        Triple("19:40 - 22:00", "쇼핑", "🛒")
                    ),
                    moves = listOf("7분", "3분", "10분")
                ),
                GroupData(
                    id = "group2",
                    name = "그룹 2",
                    time = "25/06/27 12:30 - 16:00",
                    location = "홍대",
                    activities = listOf(
                        Triple("12:30 - 14:00", "점심", "🍜"),
                        Triple("14:10 - 16:00", "카페", "☕")
                    ),
                    moves = listOf("5분")
                ),
                GroupData(
                    id = "group3",
                    name = "그룹 3",
                    time = "25/06/28 12:30 - 16:00",
                    location = "명동",
                    activities = listOf(
                        Triple("12:30 - 14:00", "점심", "🍜"),
                        Triple("14:10 - 16:00", "쇼핑", "🛒")
                    ),
                    moves = listOf("8분")
                ),
                GroupData(
                    id = "group4",
                    name = "그룹 4",
                    time = "25/06/30 16:30 - 23:40",
                    location = "강남",
                    activities = listOf(
                        Triple("16:30 - 18:00", "운동", "🏃"),
                        Triple("18:10 - 20:00", "저녁", "🍖"),
                        Triple("20:10 - 23:40", "노래방", "🎤")
                    ),
                    moves = listOf("10분", "15분")
                )
            )

            // 그룹 카드들 렌더링
            groups.forEach { group ->
                AccordionGroupCard(
                    group = group,
                    isExpanded = expandedGroupId == group.id,
                    onToggle = {
                        expandedGroupId = if (expandedGroupId == group.id) null else group.id
                    },
                    onGroupClick = {
                        // 상세정보가 열린 상태에서 한 번 더 클릭하면 GroupScreen으로 이동
                        if (expandedGroupId == group.id) {
                            navController.navigate("group")
                        }
                    },
                    cardBackground = cardBackground,
                    titleColor = titleColor,
                    chipColor = chipColor,
                    moveWalkColor = moveWalkColor,
                    moveSubwayColor = moveSubwayColor
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Create Group 팝업
        if (showCreateGroupDialog) {
            var tempGroupName by remember { mutableStateOf("") }
            // 날짜/시간 상태
            var startDate by remember { mutableStateOf<Long?>(null) }
            var endDate by remember { mutableStateOf<Long?>(null) }
            var startHour by remember { mutableStateOf(0) }
            var startMinute by remember { mutableStateOf(0) }
            var endHour by remember { mutableStateOf(0) }
            var endMinute by remember { mutableStateOf(0) }
            // 다이얼로그 show 상태
            var showStartDatePicker by remember { mutableStateOf(false) }
            var showEndDatePicker by remember { mutableStateOf(false) }
            var showStartTimePicker by remember { mutableStateOf(false) }
            var showEndTimePicker by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showCreateGroupDialog = false },
                title = {
                    Text(
                        "그룹 생성",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            "그룹 이름을 입력해주세요",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = tempGroupName,
                            onValueChange = { tempGroupName = it },
                            placeholder = { Text("그룹 이름") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedBorderColor = Color(0xFF8DB38C),
                                focusedBorderColor = Color(0xFF8DB38C)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // 시작 날짜 및 시간 박스
                            Card(
                                modifier = Modifier.weight(1f).padding(end = 8.dp).border(1.dp, Color(0xFF8DB38C), RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E6F1))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "시작",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF4C6A57),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ClickableField(
                                        value = startDate?.let { java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(it)) } ?: "",
                                        label = "날짜",
                                        onClick = { showStartDatePicker = true },
                                        valueTextSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // 시간 선택 칸 클릭 시 기존처럼 바로 showStartTimePicker/showEndTimePicker만 true로 변경
                                    ClickableField(
                                        value = String.format("%02d:%02d", startHour, startMinute),
                                        label = "시간",
                                        onClick = { showStartTimePicker = true }
                                    )
                                }
                            }
                            // 종료 날짜 및 시간 박스
                            Card(
                                modifier = Modifier.weight(1f).padding(start = 8.dp).border(1.dp, Color(0xFF8DB38C), RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E6F1))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        "종료",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF4C6A57),
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ClickableField(
                                        value = endDate?.let { java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(it)) } ?: "",
                                        label = "날짜",
                                        onClick = { showEndDatePicker = true },
                                        valueTextSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    // 시간 선택 칸 클릭 시 기존처럼 바로 showStartTimePicker/showEndTimePicker만 true로 변경
                                    ClickableField(
                                        value = String.format("%02d:%02d", endHour, endMinute),
                                        label = "시간",
                                        onClick = { showEndTimePicker = true }
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            when {
                                tempGroupName.isBlank() -> {
                                    popupInputError = "그룹 이름을 작성해주세요"
                                }
                                startDate == null || endDate == null -> {
                                    popupInputError = "날짜를 선택해주세요"
                                }
                                else -> {
                                    popupInputError = null
                                    showCreateGroupDialog = false // 팝업 먼저 닫기
                                    // 날짜+시간을 ISO 8601로 변환
                                    fun toIso8601(date: Long?, hour: Int, minute: Int): String {
                                        if (date == null) return ""
                                        val cal = java.util.Calendar.getInstance()
                                        cal.time = Date(date)
                                        cal.set(java.util.Calendar.HOUR_OF_DAY, hour)
                                        cal.set(java.util.Calendar.MINUTE, minute)
                                        cal.set(java.util.Calendar.SECOND, 0)
                                        cal.set(java.util.Calendar.MILLISECOND, 0)
                                        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                                        return sdf.format(cal.time)
                                    }
                                    val startIso = toIso8601(startDate, startHour, startMinute)
                                    val endIso = toIso8601(endDate, endHour, endMinute)
                                    groupViewModel.createGroup(tempGroupName, startIso, endIso)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6A57))
                    ) {
                        Text("그룹 생성하기")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showCreateGroupDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("취소")
                    }
                }
            )
            // Compose Material3 DatePickerDialog 사용
            if (showStartDatePicker) {
                val context = LocalContext.current
                LaunchedEffect(showStartDatePicker) {
                    if (showStartDatePicker) {
                        val cal = Calendar.getInstance()
                        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            val selectedCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            startDate = selectedCal.timeInMillis
                        }
                        DatePickerDialog(
                            context,
                            listener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            setOnDismissListener { showStartDatePicker = false }
                        }.show()
                    }
                }
            }
            if (showEndDatePicker) {
                val context = LocalContext.current
                LaunchedEffect(showEndDatePicker) {
                    if (showEndDatePicker) {
                        val cal = Calendar.getInstance()
                        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            val selectedCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, month)
                                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            endDate = selectedCal.timeInMillis
                        }
                        DatePickerDialog(
                            context,
                            listener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).apply {
                            setOnDismissListener { showEndDatePicker = false }
                        }.show()
                    }
                }
            }
            // 시간 커스텀 다이얼로그(시/분 슬라이더)
            if (showStartTimePicker) {
                CustomTimePickerDialog(
                    hour = startHour,
                    minute = startMinute,
                    onTimeSelected = { h, m ->
                        startHour = h; startMinute = m; showStartTimePicker = false
                    },
                    onDismiss = { showStartTimePicker = false }
                )
            }
            if (showEndTimePicker) {
                CustomTimePickerDialog(
                    hour = endHour,
                    minute = endMinute,
                    onTimeSelected = { h, m ->
                        endHour = h; endMinute = m; showEndTimePicker = false
                    },
                    onDismiss = { showEndTimePicker = false }
                )
            }
        }

        // 그룹 생성 완료 팝업
        LaunchedEffect(groupOperationState, selectedGroup) {
            val group = selectedGroup
            if (groupOperationState is GroupViewModel.GroupOperationState.Success && group != null) {
                createdGroupId = group._id
                groupName = group.groupname // 생성된 그룹 이름을 저장
                showGroupCreatedDialog = true
                // showCreateGroupDialog = false // 필요시 생성 팝업 닫기
            }
        }
        if (groupOperationState is GroupViewModel.GroupOperationState.Error) {
            val errorMsg = (groupOperationState as GroupViewModel.GroupOperationState.Error).message
            AlertDialog(
                onDismissRequest = { groupViewModel.resetOperationState() },
                title = { Text("에러", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = { Text(errorMsg, color = Color.Red) },
                confirmButton = {
                    Button(onClick = { groupViewModel.resetOperationState() }) {
                        Text("확인")
                    }
                }
            )
        }
        if (showGroupCreatedDialog) {
            val clipboardManager = LocalClipboardManager.current
            AlertDialog(
                onDismissRequest = { showGroupCreatedDialog = false },
                title = {
                    Text(
                        "그룹이 생성되었습니다!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = createdGroupId,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4C6A57),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(createdGroupId))
                                    Toast.makeText(context, "복사가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.copy),
                                    contentDescription = "복사",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            "Join Group 버튼을 누르고 이 초대코드를 입력하면 ${groupName} 그룹에 참여할 수 있습니다.\n초대코드는 그룹 창에서도 확인할 수 있습니다.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showGroupCreatedDialog = false
                            navigateToHome = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6A57))
                    ) {
                        Text("확인")
                    }
                }
            )
        }
        if (popupInputError != null) {
            AlertDialog(
                onDismissRequest = { popupInputError = null },
                title = { Text("입력 오류", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = { Text(popupInputError!!, color = Color(0xFF000000), fontWeight = FontWeight.Bold) },
                confirmButton = {
                    Button(onClick = { popupInputError = null }) {
                        Text("확인")
                    }
                }
            )
        }

        // Join Group 팝업
        if (showJoinGroupDialog) {
            var inviteCode by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showJoinGroupDialog = false },
                title = {
                    Text(
                        "그룹 참여",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            "초대코드를 입력해주세요",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = { inviteCode = it },
                            placeholder = { Text("초대코드") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedBorderColor = Color(0xFF8DB38C),
                                focusedBorderColor = Color(0xFF8DB38C)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (inviteCode.isNotBlank()) {
                                // TODO: 그룹 참여 로직 구현
                                showJoinGroupDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6A57))
                    ) {
                        Text("참여하기")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showJoinGroupDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("취소")
                    }
                }
            )
        }
        if (navigateToHome) {
            LaunchedEffect(Unit) {
                delay(150)
                navController.navigate("home")
                navigateToHome = false
            }
        }
    }
}

// 그룹 데이터 클래스
data class GroupData(
    val id: String,
    val name: String,
    val time: String,
    val location: String,
    val activities: List<Triple<String, String, String>>,
    val moves: List<String>
)

@Composable
fun AccordionGroupCard(
    group: GroupData,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onGroupClick: () -> Unit,
    cardBackground: Color,
    titleColor: Color,
    chipColor: Color,
    moveWalkColor: Color,
    moveSubwayColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                if (isExpanded) {
                    onGroupClick()
                } else {
                    onToggle()
                }
            },
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 그룹 카드 헤더 (항상 표시)
            GroupCardHeader(
                groupName = group.name,
                time = group.time,
                location = group.location,
                titleColor = titleColor,
                onMemberClick = { /* 그룹 멤버 보기 다이얼로그 표시 */ }
            )
            // 상세 정보 (확장 시에만 표시)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                ScheduleTimeline(
                    activities = group.activities,
                    moves = group.moves,
                    moveColors = List(group.moves.size) { if (it % 2 == 0) moveWalkColor else moveSubwayColor },
                    moveIcons = List(group.moves.size) { if (it % 2 == 0) "🚶" else "🚇" },
                    chipColor = chipColor
                )
            }
        }
    }
}

// 기존 DatePickerDialog를 CustomDatePickerDialog로 교체
@Composable
fun CustomTimePickerDialog(
    hour: Int,
    minute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val hours = (0..23).toList()
    val minutes = listOf(0, 10, 20, 30, 40, 50)
    val hourList = List(100) { hours }.flatten() // 2400개
    val minuteList = List(100) { minutes }.flatten() // 600개
    // 현재 값에서 한 칸 위(이전 값)가 중앙에 오도록 초기화
    val prevHour = (hour - 1 + 24) % 24
    val prevMinute = (minute - 10 + 60) % 60
    val hourInit = 50 * 24 + prevHour // 중앙 근처로 초기화
    val minuteInit = 50 * 6 + (minutes.indexOf(prevMinute).takeIf { it >= 0 } ?: 0)
    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = hourInit - 1)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = minuteInit - 1)
    val itemHeightDp = 40.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeightDp.toPx() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("시간 선택", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 시 휠 피커
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("시", fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .height(120.dp)
                                .width(60.dp)
                        ) {
                            LazyColumn(
                                state = hourState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(hourList.size) { idx ->
                                    // 중앙 인덱스 계산
                                    val centerIndex = hourState.firstVisibleItemIndex + 2
                                    val isSelected = idx == centerIndex
                                    Text(
                                        text = "%02d".format(hourList[idx]),
                                        fontSize = if (isSelected) 24.sp else 16.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.Black else Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(itemHeightDp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                                    .height(itemHeightDp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    // 분 휠 피커
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("분", fontSize = 14.sp)
                        Box(
                            modifier = Modifier
                                .height(120.dp)
                                .width(60.dp)
                        ) {
                            LazyColumn(
                                state = minuteState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(minuteList.size) { idx ->
                                    val centerIndex = minuteState.firstVisibleItemIndex + 2
                                    val isSelected = idx == centerIndex
                                    Text(
                                        text = "%02d".format(minuteList[idx]),
                                        fontSize = if (isSelected) 24.sp else 16.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.Black else Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(itemHeightDp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxWidth()
                                    .height(itemHeightDp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                        Text("취소")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        // 중앙 인덱스의 값만 사용
                        val h = hourList[hourState.firstVisibleItemIndex + 2]
                        val m = minuteList[minuteState.firstVisibleItemIndex + 2]
                        onTimeSelected(h, m)
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6A57))) {
                        Text("확인")
                    }
                }
            }
        }
    }
}

@Composable
fun ClickableField(
    value: String,
    label: String,
    onClick: () -> Unit,
    valueTextSize: TextUnit = 16.sp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF8DB38C),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(
                value.ifBlank { " " },
                fontSize = valueTextSize,
                color = Color.Black
            )
        }
    }
}

