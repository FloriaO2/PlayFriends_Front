package com.example.playfriends.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playfriends.data.api.TokenManager
import com.example.playfriends.data.model.*
import com.example.playfriends.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import retrofit2.http.HEAD

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    
    // 상태 관리
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()
    
    private val _userGroups = MutableStateFlow<List<GroupResponse>>(emptyList())
    val userGroups: StateFlow<List<GroupResponse>> = _userGroups.asStateFlow()
    
    // 그룹 참여 상태
    private val _joinGroupState = MutableStateFlow<JoinGroupState>(JoinGroupState.Idle)
    val joinGroupState: StateFlow<JoinGroupState> = _joinGroupState.asStateFlow()

    sealed class JoinGroupState {
        object Idle : JoinGroupState()
        object Loading : JoinGroupState()
        data class Success(val message: String) : JoinGroupState()
        data class Error(val message: String) : JoinGroupState()
    }
    
    // 로그인 상태
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val token: Token) : LoginState()
        data class SuccessMessage(val message: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }
    
    // 로그인
    fun login(userid: String, password: String, autoLogin: Boolean = false) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            val loginRequest = LoginRequest(userid, password, autoLogin)
            val result = userRepository.login(loginRequest)
            
            result.fold(
                onSuccess = { token ->
                    _loginState.value = LoginState.Success(token)
                    // 로그인 성공 후 사용자 정보 조회
                    getCurrentUser()
                },
                onFailure = { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "로그인 실패")
                }
            )
        }
    }
    
    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            val result = userRepository.logout()
            result.fold(
                onSuccess = {
                    // 로그아웃 성공 시 모든 상태 초기화
                    _user.value = null
                    _userGroups.value = emptyList()
                    _loginState.value = LoginState.Idle
                    TokenManager.clearToken() // 토큰 삭제
                    Log.d("UserViewModel", "로그아웃 성공 - 토큰 삭제됨")
                },
                onFailure = { exception ->
                    // 로그아웃 실패해도 로컬 상태는 초기화
                    _user.value = null
                    _userGroups.value = emptyList()
                    _loginState.value = LoginState.Idle
                    TokenManager.clearToken() // 토큰 삭제
                    Log.d("UserViewModel", "로그아웃 실패 - 토큰 삭제됨: ${exception.message}")
                }
            )
        }
    }
    
    // 회원가입
    fun createUser(userid: String, username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            val userCreate = UserCreate(userid, username, password = password)
            val result = userRepository.createUser(userCreate)
            
            result.fold(
                onSuccess = { user ->
                    // 회원가입 성공 시 사용자 정보 저장 및 로그 출력
                    _user.value = user
                    Log.d("UserViewModel", "회원가입 성공: ${user.userid}, ${user.username}")
                    _loginState.value = LoginState.SuccessMessage("회원가입이 완료되었습니다!")
                },
                onFailure = { exception ->
                    Log.e("UserViewModel", "회원가입 실패: ${exception.message}")
                    _loginState.value = LoginState.Error(exception.message ?: "회원가입 실패")
                }
            )
        }
    }
    
    // 현재 사용자 정보 조회
    fun getCurrentUser() {
        viewModelScope.launch {
            Log.d("UserViewModel", "getCurrentUser() 호출")
            val result = userRepository.getCurrentUser()
            result.fold(
                onSuccess = { user ->
                    Log.d("UserViewModel", "getCurrentUser() 성공: $user")
                    _user.value = user
                },
                onFailure = { exception ->
                    Log.d("UserViewModel", "getCurrentUser() 실패: ${exception.message}")
                    _user.value = null
                }
            )
        }
    }
    
    // 사용자의 그룹 목록 조회
    fun getUserGroups(userId: String) {
        viewModelScope.launch {
            val result = userRepository.getUserGroups(userId)
            result.fold(
                onSuccess = { groupList ->
                    _userGroups.value = groupList.groups
                },
                onFailure = { exception ->
                    // 그룹 목록 조회 실패 시 빈 리스트로 설정
                    _userGroups.value = emptyList()
                }
            )
        }
    }
    
    // 그룹 참여
    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            _joinGroupState.value = JoinGroupState.Loading
            val result = userRepository.joinGroup(groupId)
            result.fold(
                onSuccess = { message ->
                    _joinGroupState.value = JoinGroupState.Success(message)
                },
                onFailure = { exception ->
                    _joinGroupState.value = JoinGroupState.Error(exception.message ?: "그룹 참여 실패")
                }
            )
        }
    }

    // 그룹 참여 상태 초기화
    fun resetJoinGroupState() {
        _joinGroupState.value = JoinGroupState.Idle
    }
    
    // 로그인 상태 초기화
    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
    
    // 에러 메시지 설정
    fun setError(message: String) {
        _loginState.value = LoginState.Error(message)
    }

    fun getUserById(userId: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            result.fold(
                onSuccess = { user -> onResult(user) },
                onFailure = { onResult(null) }
            )
        }
    }
} 