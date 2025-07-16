package com.example.playfriends.ui.viewmodel

import android.util.Log
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
    private val _groups = MutableStateFlow<List<GroupDetailResponse>>(emptyList())
    val groups: StateFlow<List<GroupDetailResponse>> = _groups.asStateFlow()

    private val _selectedGroup = MutableStateFlow<GroupDetailResponse?>(null)
    val selectedGroup: StateFlow<GroupDetailResponse?> = _selectedGroup.asStateFlow()

    private val _groupOperationState = MutableStateFlow<GroupOperationState>(GroupOperationState.Idle)
    val groupOperationState: StateFlow<GroupOperationState> = _groupOperationState.asStateFlow()

    private val _recommendedCategories = MutableStateFlow<List<String>>(emptyList())
    val recommendedCategories: StateFlow<List<String>> = _recommendedCategories.asStateFlow()

    private val _scheduleSuggestions = MutableStateFlow<List<ScheduleSuggestionOutput>>(emptyList())
    val scheduleSuggestions: StateFlow<List<ScheduleSuggestionOutput>> = _scheduleSuggestions.asStateFlow()

    // 여러 그룹의 상세 정보를 관리하는 StateFlow
    private val _detailedGroups = MutableStateFlow<List<GroupDetailResponse>>(emptyList())
    val detailedGroups: StateFlow<List<GroupDetailResponse>> = _detailedGroups.asStateFlow()

    // 여러 그룹의 상세 정보를 한 번에 불러오는 함수
    fun fetchDetailedGroups(groupIds: List<String>) {
        viewModelScope.launch {
            val details = mutableListOf<GroupDetailResponse>()
            for (id in groupIds) {
                val result = groupRepository.getGroup(id)
                result.fold(
                    onSuccess = { group -> details.add(group) },
                    onFailure = { /* 실패 시 무시 */ }
                )
            }
            _detailedGroups.value = details
        }
    }

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
                    // 그룹 목록을 새로고침하여 새 그룹을 포함시킴
                    getAllGroups()
                    _selectedGroup.value = group
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
            Log.d("GroupViewModel", "getGroup called with groupId: $groupId")
            _groupOperationState.value = GroupOperationState.Loading

            val result = groupRepository.getGroup(groupId)
            result.fold(
                onSuccess = { group ->
                    _selectedGroup.value = group
                    _groupOperationState.value = GroupOperationState.Success("그룹 정보를 불러왔습니다")
                    Log.d("GroupViewModel", "Group data fetched successfully: $group")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "그룹 조회 실패")
                    Log.e("GroupViewModel", "Failed to fetch group data", exception)
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
                    // 그룹 정보를 다시 불러와서 UI를 업데이트
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
    fun selectGroup(group: GroupDetailResponse?) {
        _selectedGroup.value = group
    }

    // 작업 상태 초기화
    fun resetOperationState() {
        _groupOperationState.value = GroupOperationState.Idle
    }

    fun clearScheduleSuggestions() {
        _scheduleSuggestions.value = emptyList()
    }

    // 카테고리 추천
    fun recommendCategories(groupId: String) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            val result = groupRepository.recommendCategories(groupId)
            result.fold(
                onSuccess = { response ->
                    _recommendedCategories.value = response.categories
                    _groupOperationState.value = GroupOperationState.Success("카테고리를 추천받았습니다.")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "카테고리 추천 실패")
                }
            )
        }
    }

    // 스케줄 제안 생성
    fun createScheduleSuggestions(groupId: String, categories: List<String>) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            Log.d("GroupViewModel", "요청중...")
            val scheduleRequest = ScheduleRequest(categories)
            val result = groupRepository.createSchedule(groupId, scheduleRequest)
            Log.d("GroupViewModel", "요청완료...$result")
            result.fold(
                onSuccess = { response ->
                    _scheduleSuggestions.value = response.schedules
                    _groupOperationState.value = GroupOperationState.Success("스케줄 제안을 받았습니다.")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "스케줄 제안 생성 실패")
                }
            )
        }
    }

    // 스케줄 확정
    fun confirmSchedule(suggestion: ScheduleSuggestionOutput) {
        viewModelScope.launch {
            _groupOperationState.value = GroupOperationState.Loading
            val scheduleInput = ScheduleSuggestionInput(
                group_id = suggestion.group_id,
                scheduled_activities = suggestion.scheduled_activities
            )
            val result = groupRepository.confirmSchedule(scheduleInput)
            result.fold(
                onSuccess = { updatedGroup ->
                    // 현재 선택된 그룹 정보 업데이트
                    _selectedGroup.value = updatedGroup
                    _groupOperationState.value = GroupOperationState.Success("스케줄을 확정했습니다.")
                },
                onFailure = { exception ->
                    _groupOperationState.value = GroupOperationState.Error(exception.message ?: "스케줄 확정 실패")
                }
            )
        }
    }
}
