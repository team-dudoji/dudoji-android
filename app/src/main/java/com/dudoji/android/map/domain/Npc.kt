package com.dudoji.android.map.domain

import com.dudoji.android.domain.model.ActivityMapObject

class Npc: NonClusterMarker {
    val npcId: Long
    val name: String
    val npcSkinUrl: String
    val hasQuest: Boolean
    var activityMapObject: ActivityMapObject? = null

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