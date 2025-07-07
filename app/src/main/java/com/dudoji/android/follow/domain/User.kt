package com.dudoji.android.follow.domain

import java.util.Date

data class User (
    val id: Long,
    val password : String,
    val role: String ,
    val name:  String ,
    val email: String ,
    val createAt: Date ,
    val provider : String ,
    val providerId: String ,
    val profileImageUrl: String,
    val pinnedCount : Int,
    val followerCount: Int,
    val followingCount: Int
){
    constructor(name: String, email: String) : this(
        id = 0,
        password = "",
        role = "",
        name = name,
        email = email,
        createAt = Date(),
        provider = "",
        providerId = "",
        profileImageUrl = "",
        pinnedCount = 0,
        followerCount = 0,
        followingCount = 0
    )
}