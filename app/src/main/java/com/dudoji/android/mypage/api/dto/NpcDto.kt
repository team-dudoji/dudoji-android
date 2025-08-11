package com.dudoji.android.mypage.api.dto

import com.dudoji.android.map.domain.Npc
import com.dudoji.android.network.api.dto.BaseDto

data class NpcDto(
    val npcId: Long,
    val lat: Double,
    val lng: Double,
    val name: String,
    val npcSkinUrl: String,
    val hasQuest: Boolean
): BaseDto<Npc> {
    override fun toDomain(): Npc {
        return Npc(
            npcId = this.npcId,
            lat = this.lat,
            lng = this.lng,
            name = this.name,
            npcSkinUrl = this.npcSkinUrl,
            hasQuest = this.hasQuest
        )
    }
}