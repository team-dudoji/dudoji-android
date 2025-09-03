package com.dudoji.android.presentation.follow

import androidx.lifecycle.ViewModel
import com.dudoji.android.domain.model.SortType
import com.dudoji.android.domain.model.User
import com.dudoji.android.domain.model.UserType
import com.dudoji.android.domain.usecase.FollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    val followUseCase: FollowUseCase
): ViewModel() {

    var type: UserType = UserType.NONE
    var keyword: String = ""
    var sort: SortType = SortType.NAME_ASC

    val getFollowersNum = followUseCase.getFollowersNum
    val getFollowingsNum = followUseCase.getFollowingsNum
    val followUser = followUseCase.followUser
    val unfollowUser = followUseCase.unfollowUser
    val getUsers = followUseCase.getUsers

    suspend fun getUsers(): List<User> {
        return followUseCase.getUsers(
            type, 0, 20, sort, keyword
        )
    }

    suspend fun toggleFollow(user: User) {
        if (user.followingAt != null) {
            unfollowUser(user.id)
        } else {
            followUser(user.id)
        }
    }
}