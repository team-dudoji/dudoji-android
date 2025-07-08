package com.dudoji.android.mypage.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.mypage.domain.Achievement
import com.dudoji.android.mypage.domain.Quest
import com.dudoji.android.mypage.domain.UserProfile
import com.dudoji.android.mypage.mapper.toDomain

object MyPageRepository {

    private const val TAG = "MypageRepositoryDEBUG"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUserProfile(): UserProfile? {
        return try {
            val response = RetrofitClient.userApiService.getUserProfile()
            if (response.isSuccessful) {
                response.body()?.toDomain()
            } else {
                Log.e(TAG, "Failed to load user profile: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading user profile: ${e.message}", e)
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyQuests(): List<Quest> {
        return try {
            val response = RetrofitClient.missionApiService.getQuests()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain("DAILY") } ?: emptyList()
            } else {
                Log.e(TAG, "Failed to load daily quests: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading daily quests: ${e.message}", e)
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAchievements(): List<Achievement> {
        return try {
            val response = RetrofitClient.missionApiService.getAchievements()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain() } ?: emptyList() // Achievement DTO to Domain 매퍼는 별도로 필요
            } else {
                Log.e(TAG, "Failed to load achievements: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading achievements: ${e.message}", e)
            emptyList()
        }
    }
}