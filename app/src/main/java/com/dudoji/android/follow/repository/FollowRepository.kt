package com.dudoji.android.follow.repository

import com.dudoji.android.follow.domain.User

object FollowRepository {
    private val followings = mutableListOf<User>(
        User("유미르", "시조의 거인"),
        User("에렌 예거", "진격의 거인"),
        User("지크 예거", "짐승 거인"),
        User("라이너 브라운", "갑옷 거인"),
        User("아르민 알레르토", "초대형 거인"),
        User("타이버", "전퇴의 거인"),
        User("피크 핑거", "차력 거인"),
        User("갤리어드 포르코", "턱 거인"),
        User("애니 레온하트", "여성형 거인")
    )

    fun getFollowings(): List<User> {
        return followings
    }

    suspend fun addFollowing(user: User) {
        followings.add(user)
        RetrofitClient.followApiService.addFriend(user.id)
    }

    suspend fun getRecommendedUsers(email: String): List<User> {
        val response = RetrofitClient.followApiService.getRecommendedUsers()
        if (response.isSuccessful) {
            return response.body() ?: emptyList();
        }
        return emptyList()
    }

    fun clearFollowings() {
        followings.clear()
    }

    suspend fun loadFollowings() {
        val response = RetrofitClient.followApiService.getFriends()
        if (response.isSuccessful) {
            response.body()?.let { users ->
                followings.clear()
                followings.addAll(users)
            }
        }
    }
}