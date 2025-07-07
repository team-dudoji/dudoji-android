package com.dudoji.android.mypage.mapper

import com.dudoji.android.mypage.domain.UserProfile
import com.dudoji.android.mypage.dto.UserProfileDto

fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        name = this.name,
        profileImageUrl = this.profileImageUrl,
        email = this.email,
        pinCount = this.pinnedCount,
        followerCount = this.followerCount,
        followingCount = this.followingCount
    )
}