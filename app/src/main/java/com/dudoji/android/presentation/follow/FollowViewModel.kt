package com.dudoji.android.presentation.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dudoji.android.domain.model.SortType
import com.dudoji.android.domain.model.User
import com.dudoji.android.domain.model.UserType
import com.dudoji.android.domain.usecase.FollowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val followUseCase: FollowUseCase
) : ViewModel() {

    private var type: UserType = UserType.FOLLOWER
    private var keyword: String = ""
    private var sort: SortType = SortType.NAME_ASC

    private val _uiState = MutableStateFlow(FollowUiState())
    val uiState: StateFlow<FollowUiState> = _uiState.asStateFlow()

    fun loadInitialData(initialType: UserType) {
        type = initialType
        loadData()
    }

    fun setListType(newType: UserType) {
        type = newType
        loadData()
    }

    fun setSearchKeyword(newKeyword: String) {
        keyword = newKeyword
        loadData()
    }

    fun setSortType(newSort: SortType) {
        sort = newSort
        loadData()
    }

    fun toggleFollow(user: User) {
        viewModelScope.launch {
            if (user.followingAt != null) {
                followUseCase.unfollowUser(user.id)
            } else {
                followUseCase.followUser(user.id)
            }
            loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val users = followUseCase.getUsers(
                    type,
                    0,
                    20,
                    sort,
                    keyword
                )
                val followersNum = followUseCase.getFollowersNum()
                val followingsNum = followUseCase.getFollowingsNum()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        users = users,
                        followerCount = followersNum,
                        followingCount = followingsNum,
                        currentType = type,
                        sort = sort
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "데이터를 불러오는데 실패했습니다.")
                }
            }
        }
    }
}

data class FollowUiState(
    val users: List<User> = emptyList(),
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentType: UserType = UserType.FOLLOWER,
    val sort: SortType = SortType.NAME_ASC
)
