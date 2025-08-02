package com.dudoji.android.map.domain

class Npc: NonClusterMarker {
    val npcId: Long
    val name: String
    val npcSkinUrl: String
    val hasQuest: Boolean

    constructor(
        npcId: Long,
        lat: Double,
        lng: Double,
        name: String,
        npcSkinUrl: String,
        hasQuest: Boolean
    ) : super(lat, lng, npcSkinUrl) {
        this.npcId = npcId
        this.name = name
        this.npcSkinUrl = npcSkinUrl
        this.hasQuest = hasQuest
    }
}