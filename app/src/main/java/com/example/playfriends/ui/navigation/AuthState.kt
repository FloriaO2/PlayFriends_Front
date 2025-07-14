package com.example.playfriends.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.playfriends.data.api.TokenManager
import kotlinx.coroutines.delay

@Composable
fun rememberAuthState(navController: NavController): AuthState {
    var isAuthenticated by remember { mutableStateOf<Boolean?>(null) }
    
    LaunchedEffect(Unit) {
        // 토큰 존재 여부 확인
        val hasToken = TokenManager.hasToken()
        isAuthenticated = hasToken
        
        if (hasToken) {
            // 토큰이 있으면 홈 화면으로 이동
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // 토큰이 없으면 로그인 화면으로 이동
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    
    return remember {
        AuthState(
            isAuthenticated = isAuthenticated,
            onLoginSuccess = {
                isAuthenticated = true
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            },
            onLogout = {
                isAuthenticated = false
                TokenManager.clearToken()
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        )
    }
}

data class AuthState(
    val isAuthenticated: Boolean?,
    val onLoginSuccess: () -> Unit,
    val onLogout: () -> Unit
) 