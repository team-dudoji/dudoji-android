package com.dudoji.android.domain.model

import java.time.LocalDate

data class User (
    val id: Long,
    val password : String,
    val role: String,
    val name:  String,
    val email: String,
    val createAt: LocalDate,
    val profileImageUrl: String,
    val followedAt: LocalDate? = null,
    val followingAt: LocalDate? = null
)