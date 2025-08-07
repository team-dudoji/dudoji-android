package com.dudoji.android.mypage.api.dto

data class NpcQuestDto(
    val npcId: Long,
    val name: String,
    val imageUrl: String,
    val npcScript: String,
    val description: String,
    val quests: List<QuestDto>
) {
}