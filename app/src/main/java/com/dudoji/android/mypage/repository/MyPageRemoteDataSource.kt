package com.dudoji.android.mypage.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.mypage.domain.Achievement
import com.dudoji.android.mypage.domain.Quest
import com.dudoji.android.mypage.domain.UserProfile
import retrofit2.Call
import retrofit2.Response

object MyPageRemoteDataSource {

    private const val TAG = "MypageRepositoryDEBUG"

    var userProfile: UserProfile? = null
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadCoin(onCoinLoaded: (Int) -> Unit) {
        RetrofitClient.userApiService.getUserCoin().enqueue(
            object : retrofit2.Callback<Int> {
                override fun onResponse(
                    call: Call<Int?>,
                    response: Response<Int?>
                ) {
                    if (response.isSuccessful) {
                        val coin = response.body() ?: 0
                        Log.d(TAG, "Coin loaded successfully: $coin")
                        userProfile?.coin = coin // Update the coin in the user profile
                        onCoinLoaded(coin)
                    } else {
                        Log.e(TAG, "Failed to load coin: ${response.message()}")
                        onCoinLoaded(0)
                    }
                }

                override fun onFailure(
                    call: Call<Int?>,
                    t: Throwable
                ) {
                    Log.e(TAG, "Error loading coin: ${t.message}", t)
                    onCoinLoaded(0)
                }
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUserProfile(): UserProfile? {
        if (userProfile != null) {
            Log.d(TAG, "User profile already loaded: $userProfile")
            return userProfile
        }

        return try {
            val response = RetrofitClient.userApiService.getUserProfile()
            if (response.isSuccessful) {
                userProfile = response.body()?.toDomain()
                userProfile
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
    suspend fun getQuests(): List<Quest> {
        return try {
            val response = RetrofitClient.missionApiService.getQuests()
            if (response.isSuccessful) {
                Log.d(TAG, "quests loaded successfully: ${response.body()}")
                response.body()?.map { it.toDomain() } ?: emptyList()
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
                Log.d(TAG, "Achievements loaded successfully: ${response.body()}")
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