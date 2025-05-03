package com.dudoji.android.network.dto

data class TokenResponse(
    val token: Token
)

data class Token(
    val grantType: String,
    val accessToken: String
)