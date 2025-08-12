package com.dudoji.android.map.api.dto

data class NpcMetaDto(
    val locationName: String,
    val npcSkinId: Long,
    val questName: String,
    val numOfQuests: Int,
    val numOfClearedQuests: Int,
    val lat: Double,
    val lng: Double
)