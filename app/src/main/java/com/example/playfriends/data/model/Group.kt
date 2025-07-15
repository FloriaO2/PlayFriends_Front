package com.example.playfriends.data.model

// 위치 정보
data class GeoJson(
    val type: String = "Point",
    val coordinates: List<Double> // [경도, 위도]
)

// 개별 활동 정보
data class ScheduledActivity(
    val name: String,
    val category: String,
    val start_time: String, // ISO 8601
    val end_time: String, // ISO 8601
    val location: GeoJson? = null
)

// 그룹 멤버 정보
data class GroupMember(
    val id: String,
    val name: String
)

// 그룹 생성 요청 모델
data class GroupCreate(
    val groupname: String,
    val starttime: String, // ISO 8601 형식의 날짜-시간 문자열
    val endtime: String? = null // ISO 8601 형식의 날짜-시간 문자열
)

// 그룹 상세 정보 응답 모델
data class GroupDetailResponse(
    val _id: String,
    val groupname: String,
    val starttime: String, // ISO 8601
    val endtime: String? = null, // ISO 8601
    val is_active: Boolean = true,
    val owner_id: String,
    val member_ids: List<String> = emptyList(),
    val food_preferences: FoodPreferences? = null,
    val play_preferences: PlayPreferences? = null,
    val schedule: List<ScheduledActivity>? = null,
    val distances_km: List<Double>? = null,
    val members: List<GroupMember> = emptyList()
)

// 그룹 목록 응답 모델
data class GroupList(
    val groups: List<GroupDetailResponse>
)

// 그룹 업데이트 모델
data class GroupUpdate(
    val groupname: String? = null,
    val starttime: String? = null, // ISO 8601
    val endtime: String? = null, // ISO 8601
    val is_active: Boolean? = null,
    val member_ids: List<String>? = null,
    val schedule: List<ScheduledActivity>? = null,
    val distances_km: List<Double>? = null
)

// 스케줄 제안 입력 모델
data class ScheduleSuggestionInput(
    val group_id: String,
    val scheduled_activities: List<ScheduledActivity>
)

// 스케줄 제안 목록 응답 모델
data class ListScheduleResponse(
    val schedules: List<ScheduleSuggestionOutput>
)

// 스케줄 제안 출력 모델
data class ScheduleSuggestionOutput(
    val group_id: String,
    val scheduled_activities: List<ScheduledActivity>
)

// 카테고리 목록 응답 모델
data class CategoryListResponse(
    val categories: List<String>
)


