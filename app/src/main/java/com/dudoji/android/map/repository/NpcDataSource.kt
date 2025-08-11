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
        return RetrofitClient.npcQuestApiService.getNpcs(
            radius.toInt(),
            lat,
            lng
        )
    }

    fun getNpcs(): List<Npc> {
        return dataList
    }
}