package com.dudoji.android.network.entity

data class TokenResponse(
    val token: Token
)

data class Token(
    val grantType: String,
    val accessToken: String
)