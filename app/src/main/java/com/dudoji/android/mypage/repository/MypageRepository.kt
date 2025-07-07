package com.dudoji.android.mypage.repository

import android.util.Log
import com.dudoji.android.mypage.domain.Quest
import com.dudoji.android.mypage.domain.Achievement // Achievement 도메인 모델 임포트
import com.dudoji.android.mypage.mapper.toDomain // 매퍼 임포트
import com.dudoji.android.mypage.domain.UserProfile

object MypageRepository {

    private const val TAG = "MypageRepositoryDEBUG"

    suspend fun getUserProfile(): UserProfile? {
        return try {
            val response = RetrofitClient.userApiService.getUserProfile()
            if (response.isSuccessful) {
                response.body()?.toDomain() // UserProfileDto를 UserProfile로 변환
            } else {
                Log.e(TAG, "Failed to load user profile: ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading user profile: ${e.message}", e)
            null
        }
    }

    suspend fun getDailyQuests(): List<Quest> {
        return try {
            val response = RetrofitClient.questApiService.getDailyQuests()
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

    suspend fun getLandmarkQuests(): List<Quest> {
        return try {
            val response = RetrofitClient.questApiService.getLandmarkQuests()
            if (response.isSuccessful) {
                response.body()?.map { it.toDomain("LANDMARK") } ?: emptyList()
            } else {
                Log.e(TAG, "Failed to load landmark quests: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading landmark quests: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getAchievements(): List<Achievement> {
        return try {
            val response = RetrofitClient.achievementApiService.getAchievements()
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