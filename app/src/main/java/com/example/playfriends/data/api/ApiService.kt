package com.example.playfriends.data.api

import com.example.playfriends.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ===== 사용자 관련 API =====
    
    // 로그인
    @POST("api/v1/token")
    suspend fun login(@Body loginRequest: LoginRequest): Response<Token>
    
    // 로그아웃
    @POST("api/v1/logout")
    suspend fun logout(): Response<Unit>
    
    // 회원가입
    @POST("api/v1/create_user/")
    suspend fun createUser(@Body userCreate: UserCreate): Response<User>
    
    // 현재 사용자 정보 조회
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<User>
    
    // 사용자의 그룹 목록 조회
    @GET("api/v1/users/{user_id}/groups")
    suspend fun getUserGroups(@Path("user_id") userId: String): Response<GroupList>
    
    

    // 현재 사용자 정보 수정
    @PUT("api/v1/users/me")
    suspend fun updateUserMe(@Body userUpdate: UserUpdate): Response<User>

    // 선호도 수정
    @PUT("api/v1/users/preferences")
    suspend fun updatePreferences(@Body preferences: PreferencesUpdate): Response<Unit>
    
    // ===== 그룹 관련 API =====
    
    // 모든 그룹 조회
    @GET("api/v1/groups/")
    suspend fun getAllGroups(): Response<GroupList>
    
    // 그룹 생성
    @POST("api/v1/groups/")
    suspend fun createGroup(@Body groupCreate: GroupCreate): Response<GroupDetailResponse>
    
    // 특정 그룹 조회
    @GET("api/v1/groups/{group_id}")
    suspend fun getGroup(@Path("group_id") groupId: String): Response<GroupDetailResponse>
    
    // 그룹 수정
    @PUT("api/v1/groups/{group_id}")
    suspend fun updateGroup(
        @Path("group_id") groupId: String,
        @Body groupUpdate: GroupUpdate
    ): Response<GroupDetailResponse>
    
    // 그룹 삭제
    @DELETE("api/v1/groups/{group_id}")
    suspend fun deleteGroup(@Path("group_id") groupId: String): Response<Unit>
    
    // 그룹 참여
    @POST("api/v1/groups/{group_id}/join")
    suspend fun joinGroup(@Path("group_id") groupId: String): Response<Message>
    
    // 그룹 탈퇴
    @DELETE("api/v1/groups/{group_id}/leave")
    suspend fun leaveGroup(@Path("group_id") groupId: String): Response<Message>

    // ===== 스케줄 관련 API =====

    // 카테고리 추천
    @POST("api/v1/groups/{group_id}/recommend-categories")
    suspend fun recommendCategories(@Path("group_id") groupId: String): Response<CategoryListResponse>

    // 스케줄 제안 생성
    @POST("api/v1/groups/{group_id}/schedules")
    suspend fun createSchedule(
        @Path("group_id") groupId: String,
        @Body categories: ScheduleRequest
    ): Response<ListScheduleResponse>

    // 스케줄 확정
    @POST("api/v1/groups/schedule")
    suspend fun confirmSchedule(@Body scheduleInput: ScheduleSuggestionInput): Response<GroupDetailResponse>
}
