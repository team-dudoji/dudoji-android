package com.dudoji.android.presentation.follow

import androidx.lifecycle.ViewModel
import com.dudoji.android.domain.usecase.FollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    followUseCase: FollowUseCase
): ViewModel() {
    val getFollowersNum = followUseCase.getFollowersNum
    val getFollowingsNum = followUseCase.getFollowingsNum
    val followUser = followUseCase.followUser
    val unfollowUser = followUseCase.unfollowUser
    val getUsers = followUseCase.getUsers
}