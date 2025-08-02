package com.dudoji.android.mypage.api.dto

import com.dudoji.android.mypage.domain.UserProfile

data class UserProfileDto(
    val name: String,
    val email: String,
    val profileImageUrl: String,
    val pinCount: Int,
    val followerCount: Int,
    val followingCount: Int
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            name = this.name,
            profileImageUrl = this.profileImageUrl,
            email = this.email,
            pinCount = this.pinCount,
            followerCount = this.followerCount,
            followingCount = this.followingCount
        )
    }
}