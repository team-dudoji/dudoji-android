package com.dudoji.android.domain.model

import java.time.LocalDateTime

data class User (
    val id: Long,
    val password : String,
    val role: String,
    val name:  String,
    val email: String,
    val profileImageUrl: String,
    val followedAt: LocalDateTime? = null,
    val followingAt: LocalDateTime? = null
)