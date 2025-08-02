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
                NpcDto(1, 35.2323, 129.0790, "NPC 1", "https://cdn-icons-png.flaticon.com/512/1995/1995525.png", true),
                NpcDto(2, 35.2327, 129.0788, "NPC 2", "https://cdn-icons-png.flaticon.com/512/1995/1995526.png", false),
                NpcDto(3, 35.2321, 129.0795, "NPC 3", "https://cdn-icons-png.flaticon.com/512/1995/1995527.png", true),
                NpcDto(4, 35.2319, 129.0785, "NPC 4", "https://cdn-icons-png.flaticon.com/512/1995/1995528.png", false),
                NpcDto(5, 35.2330, 129.0793, "NPC 5", "https://cdn-icons-png.flaticon.com/512/1995/1995529.png", true),
                NpcDto(6, 35.2317, 129.0782, "NPC 6", "https://cdn-icons-png.flaticon.com/512/1995/1995530.png", false),
                NpcDto(7, 35.2325, 129.0796, "NPC 7", "https://cdn-icons-png.flaticon.com/512/1995/1995531.png", true),
                NpcDto(8, 35.2329, 129.0789, "NPC 8", "https://cdn-icons-png.flaticon.com/512/1995/1995532.png", true),
                NpcDto(9, 35.2315, 129.0791, "NPC 9", "https://cdn-icons-png.flaticon.com/512/1995/1995533.png", false),
                NpcDto(10, 35.2320, 129.0787, "NPC 10", "https://cdn-icons-png.flaticon.com/512/1995/1995534.png", true)
            )
        )
    }

    fun getNpcs(): List<Npc> {
        return dataList
    }
}