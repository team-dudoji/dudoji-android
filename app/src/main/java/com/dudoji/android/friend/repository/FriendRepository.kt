package com.dudoji.android.friend.repository

import com.dudoji.android.friend.Friend
import com.dudoji.android.friend.domain.User

object FriendRepository {
    private val friendList = mutableListOf<Friend>(
        Friend("유미르", "시조의 거인", true),
        Friend("에렌 예거", "진격의 거인", false),
        Friend("지크 예거", "짐승 거인", true),
        Friend("라이너 브라운", "갑옷 거인", true),
        Friend("아르민 알레르토", "초대형 거인", false),
        Friend("타이버", "전퇴의 거인", true),
        Friend("피크 핑거", "차력 거인", true),
        Friend("갤리어드 포르코", "턱 거인", false),
        Friend("애니 레온하트", "여성형 거인", true)
    )

    fun getFriends(): List<Friend> {
        return friendList
    }

    fun addFriend(friend: Friend) {
        friendList.add(friend)
        suspend {
            RetrofitClient.friendApiService.addFriend(friend.user.id)
        }
    }

    fun clearFriends() {
        friendList.clear()
    }

    suspend fun getRecommendedFriends(query: String): List<User> {
        val response = RetrofitClient.friendApiService.getRecommendedFriends()
        if (response.isSuccessful) {
            response.body()?.let { friends ->
                return friends
            }
        }
        return emptyList()
    }

    suspend fun loadFriendsFromApi() {
        val response = RetrofitClient.friendApiService.getFriends()
        if (response.isSuccessful) {
            response.body()?.let { friends ->
                friendList.clear()
                friendList.addAll(friends.map { Friend(it, true) })
            }
        }
    }
}