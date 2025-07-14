package com.example.playfriends.data.model

import java.time.LocalDateTime

// 그룹 생성 요청 모델
data class GroupCreate(
    val groupname: String,
    val starttime: String, // ISO 8601 형식의 날짜-시간 문자열
    val endtime: String? = null // ISO 8601 형식의 날짜-시간 문자열
)

// 그룹 응답 모델
data class GroupResponse(
    val _id: String,
    val groupname: String,
    val starttime: String, // ISO 8601 형식의 날짜-시간 문자열
    val endtime: String? = null, // ISO 8601 형식의 날짜-시간 문자열
    val is_active: Boolean = true,
    val owner_id: String,
    val member_ids: List<String> = emptyList()
)

// 그룹 업데이트 모델
data class GroupUpdate(
    val groupname: String? = null,
    val starttime: String? = null, // ISO 8601 형식의 날짜-시간 문자열
    val endtime: String? = null, // ISO 8601 형식의 날짜-시간 문자열
    val is_active: Boolean? = null,
    val member_ids: List<String>? = null
)

// 그룹 목록 응답 모델
data class GroupList(
    val groups: List<GroupResponse>
) 