package com.example.playfriends.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playfriends.data.model.*
import com.example.playfriends.data.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {
    private val groupRepository = GroupRepository()
    
    // 상태 관리
    private val _groups = MutableStateFlow<List<GroupResponse>>(emptyList())
    val groups: StateFlow<List<GroupResponse>> = _groups.asStateFlow()
    
    private val _selectedGroup = MutableStateFlow<GroupResponse?>(null)
    val selectedGroup: StateFlow<GroupResponse?> = _selectedGroup.asStateFlow()
    
    private val _groupOperationState = MutableStateFlow<GroupOperationState>(GroupOperationState.Idle)
    val groupOperationState: StateFlow<GroupOperationState> = _groupOperationState.asStateFlow()
    
    // 그룹 작업 상태
    sealed class GroupOperationState {
        object Idle : GroupOperationState()
        object Loading : GroupOperationState()
        data class Success(val message: String) : GroupOperationState()
        data class Error(val message: String) : GroupOperationState()
    }
    
    // 모든 그룹 조회
    fun getAllGroups() {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            
            val result = groupRepository.getAllGroups()
            result.fold(
                onSuccess = { groupList ->
                    _groups.value = groupList.groups
                    _groupOperationState.value = GroupOperationState.Success("그룹 목록을 불러왔습니다")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 목록 조회 실패")
                }
            )
        }
    }
    
    // 그룹 생성
    fun createGroup(groupname: String, starttime: String, endtime: String? = null) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            
            val groupCreate = GroupCreate(groupname, starttime, endtime)
            val result = groupRepository.createGroup(groupCreate)
            
            result.fold(
                onSuccess = { group ->
                    // 새로 생성된 그룹을 목록에 추가
                    _groups.value = _groups.value + group
                    _groupOperationState.value = GroupOperationState.Success("그룹이 생성되었습니다")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 생성 실패")
                }
            )
        }
    }
    
    // 특정 그룹 조회
    fun getGroup(groupId: String) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            
            val result = groupRepository.getGroup(groupId)
            result.fold(
                onSuccess = { group ->
                    _selectedGroup.value = group
                    _groupOperationState.value = GroupOperationState.Success("그룹 정보를 불러왔습니다")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 조회 실패")
                }
            )
        }
    }
    
    // 그룹 수정
    fun updateGroup(groupId: String, groupUpdate: GroupUpdate) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            
            val result = groupRepository.updateGroup(groupId, groupUpdate)
            result.fold(
                onSuccess = { updatedGroup ->
                    // 목록에서 해당 그룹 업데이트
                    _groups.value = _groups.value.map { 
                        if (it._id == groupId) updatedGroup else it 
                    }
                    _selectedGroup.value = updatedGroup
                    _groupOperationState.value = GroupOperationState.Success("그룹이 수정되었습니다")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 수정 실패")
                }
            )
        }
    }
    
    // 그룹 삭제
    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            
            val result = groupRepository.deleteGroup(groupId)
            result.fold(
                onSuccess = {
                    // 목록에서 해당 그룹 제거
                    _groups.value = _groups.value.filter { it._id != groupId }
                    if (_selectedGroup.value?._id == groupId) {
                        _selectedGroup.value = null
                    }
                    _groupOperationState.value = GroupOperationState.Success("그룹이 삭제되었습니다")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 삭제 실패")
                }
            )
        }
    }
    
    // 그룹 참여
    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            
            val result = groupRepository.joinGroup(groupId)
            result.fold(
                onSuccess = { message ->
                    _groupOperationState.value = GroupOperationState.Success(message.message)
                    // 그룹 목록 새로고침
                    getAllGroups()
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 참여 실패")
                }
            )
        }
    }
    
    // 그룹 탈퇴
    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            
            val result = groupRepository.leaveGroup(groupId)
            result.fold(
                onSuccess = { message ->
                    _groupOperationState.value = GroupOperationState.Success(message.message)
                    // 그룹 목록 새로고침
                    getAllGroups()
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 탈퇴 실패")
                }
            )
        }
    }
    
    // 그룹 선택
    fun selectGroup(group: GroupResponse?) {
        _selectedGroup.value = group
    }
    
    // 작업 상태 초기화
    fun resetOperationState() {
        _groupOperationState.value = GroupOperationState.Idle
    }
} 