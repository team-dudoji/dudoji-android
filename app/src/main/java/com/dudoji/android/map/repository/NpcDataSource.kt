package com.dudoji.android.map.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.map.domain.Npc
import com.dudoji.android.mypage.api.dto.NpcDto
import retrofit2.Response

object NpcDataSource: RangeSearchDataSource<NpcDto, Npc>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchFromApi(
        lat: Double,
        lng: Double,
        radius: Double
    ): Response<List<NpcDto>> {
//        return RetrofitClient.npcQuestApiService.getNpcs(
//            radius.toInt(),
//            lat,
//            lng
//        )
        return Response.success(
            listOf(
                NpcDto(1, 35.229267, 129.011336, "NPC 1", "https://cdn-icons-png.flaticon.com/512/1995/1995525.png", true),
                NpcDto(2, 35.229367, 129.011436, "NPC 2", "https://cdn-icons-png.flaticon.com/512/1995/1995525.png", true)
            )
        )
    }

    fun getNpcs(): List<Npc> {
        return dataList
    }
}