package com.dudoji.android.friend.domain

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
    val profileImageUrl: String
)