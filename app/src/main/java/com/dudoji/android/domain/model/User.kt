package com.dudoji.android.domain.model

import java.util.Date

data class User (
    val id: Long,
    val password : String,
    val role: String,
    val name:  String,
    val email: String,
    val createAt: Date,
    val profileImageUrl: String,
    val followedAt: Date? = null,
    val followingAt: Date? = null
){

}