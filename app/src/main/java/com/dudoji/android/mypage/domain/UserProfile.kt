package com.dudoji.android.mypage.domain

data class UserProfile(
    val name: String,
    val profileImageUrl: String?,
    val email: String,
    val pinCount: Int,
    val followerCount: Int,
    val followingCount: Int,
    var coin: Int
)