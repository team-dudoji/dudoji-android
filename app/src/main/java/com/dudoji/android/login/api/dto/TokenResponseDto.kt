package com.dudoji.android.login.api.dto

data class TokenResponseDto (
    val token: TokenDto
)

data class TokenDto (
    val grantType: String,
    val accessToken: String
)