package com.dudoji.android.mypage.dto

data class UserProfileDto(
    val name: String,
    val profileImageUrl: String?,
    val email: String,
    val pinnedCount: Int,
    val followerCount: Int,
    val followingCount: Int
)