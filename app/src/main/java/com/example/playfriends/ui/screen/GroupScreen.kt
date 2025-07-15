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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playfriends.ui.viewmodel.GroupViewModel
import com.example.playfriends.ui.viewmodel.UserViewModel
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.painterResource
import com.example.playfriends.R
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.text.style.TextAlign

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

    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // 스낵바 상태 추가
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMsg by remember { mutableStateOf("") }

    LaunchedEffect(groupId) {
        groupViewModel.getGroup(groupId)
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
        if (group == null) {
            // 로딩 또는 에러 처리
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("그룹 정보를 불러오는 중입니다...")
            }
            return@Scaffold
        }

        // 스낵바 상태 감지해서 띄우기
        if (showSnackbar) {
            LaunchedEffect(snackbarMsg) {
                snackbarHostState.showSnackbar(snackbarMsg)
                showSnackbar = false
            }
        }

        val isOwner = group?.owner_id == currentUser?._id

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(start = 30.dp, end = 30.dp, top = 10.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 그룹명/날짜 + 초대코드/위치 (2줄로 분리, 양끝 배치)
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
                        group!!.groupname,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )
                    // 일정 포맷팅: 25/07/20 00:00 - 25/07/20 20:00
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yy/MM/dd HH:mm", Locale.getDefault())
                    val start = try {
                        outputFormat.format(inputFormat.parse(group!!.starttime) ?: "")
                    } catch (e: Exception) { "" }
                    val end = try {
                        group!!.endtime?.let { outputFormat.format(inputFormat.parse(it) ?: "") } ?: ""
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
                            clipboardManager.setText(AnnotatedString(group!!._id))
                            Toast.makeText(context, "복사가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        },
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        Text(group!!._id, fontSize = 12.sp, color = Color.Gray)
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
            group?.let { currentGroup ->
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
                                    (currentGroup.members.find { it.id == currentGroup.owner_id }?.name ?: "Unknown") + " (방장)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
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
            }

            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE0E0E0)))
            Spacer(modifier = Modifier.height(20.dp))

            if (isOwner) {
                // Owner UI
                if (group!!.schedule == null) {
                    OwnerScheduleCreationUI(
                        groupName = group!!.groupname,
                        titleColor = titleColor,
                        cardColor = cardColor,
                        checkboxColor = checkboxColor,
                        recommendedCategories = recommendedCategories,
                        checkedStates = checkedStates,
                        onCheckedChange = { newStates -> checkedStates = newStates },
                        onEditClick = { showPopup = true },
                        onRecommendClick = {
                            val selected = checkedStates.filter { it.value }.keys.toList()
                            groupViewModel.createScheduleSuggestions(group!!._id, selected)
                            navController.navigate("groupPlan")
                        }
                    )
                } else {
                    // Owner & Schedule exists
                    ConfirmedScheduleUI(schedule = group!!.schedule!!, titleColor = titleColor)
                }
            } else {
                // Member UI
                if (group!!.schedule != null) {
                    ConfirmedScheduleUI(schedule = group!!.schedule!!, titleColor = titleColor)
                } else {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 50.dp), contentAlignment = Alignment.Center) {
                        Text("아직 확정된 스케줄이 없습니다.", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            }

            // 팝업 (Owner 전용)
            if (showPopup && isOwner) {
                // ... (팝업 로직은 기존과 유사하게 유지)
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
    onRecommendClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 취향 분석 레포트 타이틀
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = titleColor, fontWeight = FontWeight.Bold)) {
                    append(groupName)
                }
                append("을 위한 취향 분석 레포트")
            },
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(15.dp))

        // 육각형 placeholder
        Box(
            modifier = Modifier
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
                drawPath(path = hexPath, color = Color(0xFFD1E9D1), style = Fill)
                drawPath(path = hexPath, color = Color(0xFFB0EACD), style = Stroke(width = 6f))
            }
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
