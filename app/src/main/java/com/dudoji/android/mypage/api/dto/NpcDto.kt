package com.dudoji.android.mypage.api.dto

data class NpcDto(
    val npcId: Long,
    val lat: Double,
    val lng: Double,
    val name: String,
    val npcSkinUrl: String,
    val hasQuest: Boolean
)
