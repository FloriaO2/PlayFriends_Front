package com.example.playfriends.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.playfriends.R
import com.example.playfriends.ui.viewmodel.UserViewModel
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import com.example.playfriends.ui.screen.findActivity
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context.findActivity() as? ComponentActivity
    requireNotNull(activity) { "Activity를 찾을 수 없습니다." }
    val userViewModel: UserViewModel = viewModel(viewModelStoreOwner = activity)

    var isSignUp by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(1) }

    var userNickname by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFFF1FFF4)
    val buttonColor = Color(0xFF5C9E5C)
    val borderColor = Color(0xFF8DB38C)
    val errorColor = Color(0xFFE57373)

    // Black Han Sans 폰트 사용 (Google Fonts)
    val fontFamily = FontFamily(
        Font(googleFont = GoogleFont("Black Han Sans"), fontProvider = GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = emptyList()
        ))
    )

    // ViewModel 상태 관찰
    val loginState by userViewModel.loginState.collectAsState()
    val user by userViewModel.user.collectAsState()

    // 사용자 정보 변경 시 로그 출력
    LaunchedEffect(user) {
        user?.let {
            Log.d("LoginScreen", "사용자 정보 업데이트: ${it.userid}, ${it.username}")
        }
    }

    // 로그인 상태에 따른 처리
    LaunchedEffect(loginState) {
        when (loginState) {
            is UserViewModel.LoginState.Success -> {
                // 로그인 성공 시 콜백 호출
                onLoginSuccess()
            }
            is UserViewModel.LoginState.SuccessMessage -> {
                // 회원가입 성공 시 로그인 화면으로 돌아가기
                isSignUp = false
                userViewModel.resetLoginState()
            }
            is UserViewModel.LoginState.Error -> {
                // 에러는 UI에서 표시하므로 여기서는 아무것도 하지 않음
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!isSignUp) {
            // 설명 텍스트
            Text(
                text = "취향으로 알아보는",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 30.dp)
            )
            Text(
                text = "스케줄 자판기,",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 30.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 로고 + 앱명
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo), // drawable/logo.png
                    contentDescription = "앱 로고",
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Play", fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                    Text("Friends", fontSize = 28.sp, fontWeight = FontWeight.Bold, fontFamily = fontFamily)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 에러 메시지 표시
            if (loginState is UserViewModel.LoginState.Error) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = errorColor.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, errorColor)
                ) {
                    Text(
                        text = (loginState as UserViewModel.LoginState.Error).message,
                        color = errorColor,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 아이디
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it },
                placeholder = { Text("아이디", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = borderColor,
                    focusedBorderColor = borderColor,
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 비밀번호
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("비밀번호", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = borderColor,
                    focusedBorderColor = borderColor,
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            val context = LocalContext.current
            Button(
                onClick = {
                    when {
                        userId.isBlank() -> {
                            Toast.makeText(context, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show()
                        }
                        password.isBlank() -> {
                            Toast.makeText(context, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            userViewModel.login(userId, password)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(10.dp),
                enabled = loginState !is UserViewModel.LoginState.Loading
            ) {
                if (loginState is UserViewModel.LoginState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("로그인", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("계정이 없으신가요?")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "회원가입 하기",
                    color = Color(0xFF088A29),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        isSignUp = true
                        step = 1
                        userId = ""
                        password = ""
                        passwordConfirm = ""
                        // 에러 상태 초기화
                        userViewModel.resetLoginState()
                    }
                )
            }
        } else {
            // 회원가입 화면
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 상단바 - 상단에 붙임
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .clickable {
                                isSignUp = false
                                // 에러 상태 초기화
                                userViewModel.resetLoginState()
                            }
                            .size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("회원가입", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "앱 로고",
                        modifier = Modifier.size(40.dp)
                    )
                }

                // 입력창들 - 상단에 배치
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // 회원가입 에러 메시지 표시
                    if (loginState is UserViewModel.LoginState.Error) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = errorColor.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, errorColor)
                        ) {
                            Text(
                                text = (loginState as UserViewModel.LoginState.Error).message,
                                color = errorColor,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (step == 1) {
                        OutlinedTextField(
                            value = userNickname,
                            onValueChange = { userNickname = it },
                            placeholder = { Text("닉네임", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedBorderColor = borderColor,
                                focusedBorderColor = borderColor,
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = userId,
                            onValueChange = { userId = it },
                            placeholder = { Text("아이디", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedBorderColor = borderColor,
                                focusedBorderColor = borderColor,
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("비밀번호", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                unfocusedBorderColor = borderColor,
                                focusedBorderColor = borderColor,
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                // 입력 검증
                                when {
                                    userNickname.isBlank() -> {
                                        // 닉네임이 비어있으면 에러 메시지 표시
                                        userViewModel.setError("닉네임을 입력해주세요.")
                                    }
                                    userId.isBlank() -> {
                                        // 아이디가 비어있으면 에러 메시지 표시
                                        userViewModel.setError("아이디를 입력해주세요.")
                                    }
                                    userId.length < 3 -> {
                                        // 아이디가 3자 미만이면 에러 메시지 표시
                                        userViewModel.setError("아이디는 3자 이상으로 입력해주세요.")
                                    }
                                    password.length < 8 -> {
                                        // 비밀번호가 8자 미만이면 에러 메시지 표시
                                        userViewModel.setError("비밀번호는 8자 이상으로 입력해주세요.")
                                    }
                                    else -> {
                                        // 모든 검증 통과 시 회원가입 API 호출
                                        Log.d("LoginScreen", "회원가입 시도: $userId, $userNickname")
                                        userViewModel.createUser(userId, userNickname, password)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(10.dp),
                            enabled = loginState !is UserViewModel.LoginState.Loading
                        ) {
                            if (loginState is UserViewModel.LoginState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            } else {
                                Text("회원가입", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
