package com.example.playfriends.data.repository

import com.example.playfriends.data.api.RetrofitClient
import com.example.playfriends.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroupRepository {
    private val apiService = RetrofitClient.apiService
    
    // 모든 그룹 조회
    suspend fun getAllGroups(): Result<GroupList> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllGroups()
            if (response.isSuccessful) {
                val groupList = response.body()
                if (groupList != null) {
                    Result.success(groupList)
                } else {
                    Result.failure(Exception("그룹 목록 조회 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 그룹 생성
    suspend fun createGroup(groupCreate: GroupCreate): Result<GroupDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createGroup(groupCreate)
            if (response.isSuccessful) {
                val group = response.body()
                if (group != null) {
                    Result.success(group)
                } else {
                    Result.failure(Exception("그룹 생성 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 특정 그룹 조회
    suspend fun getGroup(groupId: String): Result<GroupDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGroup(groupId)
            if (response.isSuccessful) {
                val group = response.body()
                if (group != null) {
                    Result.success(group)
                } else {
                    Result.failure(Exception("그룹 조회 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 그룹 수정
    suspend fun updateGroup(groupId: String, groupUpdate: GroupUpdate): Result<GroupDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateGroup(groupId, groupUpdate)
            if (response.isSuccessful) {
                val group = response.body()
                if (group != null) {
                    Result.success(group)
                } else {
                    Result.failure(Exception("그룹 수정 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 그룹 삭제
    suspend fun deleteGroup(groupId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteGroup(groupId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("그룹 삭제 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 그룹 참여
    suspend fun joinGroup(groupId: String): Result<Message> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.joinGroup(groupId)
            if (response.isSuccessful) {
                val message = response.body()
                if (message != null) {
                    Result.success(message)
                } else {
                    Result.failure(Exception("그룹 참여 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // 그룹 탈퇴
    suspend fun leaveGroup(groupId: String): Result<Message> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.leaveGroup(groupId)
            if (response.isSuccessful) {
                val message = response.body()
                if (message != null) {
                    Result.success(message)
                } else {
                    Result.failure(Exception("그룹 탈퇴 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 카테고리 추천
    suspend fun recommendCategories(groupId: String): Result<CategoryListResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.recommendCategories(groupId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("카테고리 추천 응답 없음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 스케줄 제안 생성
    suspend fun createSchedule(groupId: String, categories: Map<String, List<String>>): Result<ListScheduleResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createSchedule(groupId, categories)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("스케줄 제안 응답 없음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 스케줄 확정
    suspend fun confirmSchedule(scheduleInput: ScheduleSuggestionInput): Result<GroupDetailResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.confirmSchedule(scheduleInput)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("스케줄 확정 응답 없음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
