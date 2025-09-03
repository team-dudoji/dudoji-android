package com.dudoji.android.domain.usecase

import com.dudoji.android.domain.repository.FollowRepository
import javax.inject.Inject

class FollowUseCase @Inject constructor(
  followRepository: FollowRepository
) {
    val getFollowersNum = followRepository::getFollowersNum
    val getFollowingsNum = followRepository::getFollowingsNum
    val followUser = followRepository::followUser
    val unfollowUser = followRepository::unfollowUser
    val getUsers = followRepository::getUsers
}