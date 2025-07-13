package com.example.playfriends.UI.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import com.example.playfriends.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var isSignUp by remember { mutableStateOf(false) }
    var step by remember { mutableStateOf(1) }

    var userNickname by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFFF1FFF4)
    val buttonColor = Color(0xFF5C9E5C)
    val borderColor = Color(0xFF8DB38C)
    
    // Black Han Sans 폰트 사용 (Google Fonts)
    val fontFamily = FontFamily(
        Font(googleFont = GoogleFont("Black Han Sans"), fontProvider = GoogleFont.Provider(
            providerAuthority = "com.google.android.gms.fonts",
            providerPackage = "com.google.android.gms",
            certificates = emptyList()
        ))
    )

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

            Button(
                onClick = {
                    // 로그인 처리
                    navController.navigate("home")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("로그인", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                        if (userId.isNotBlank()) step = 2
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("회원가입", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
                }
            }
        }
    }
}
