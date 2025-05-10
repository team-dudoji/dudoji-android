package com.dudoji.android.friend

import com.dudoji.android.friend.domain.User
import java.util.Date

data class Friend(
    val user: User,
    var isVisible: Boolean
) {
    constructor(name: String, email: String, isVisible: Boolean) : this(
        user = User(
            id = 0,
            password = "",
            role = "",
            name = name,
            email = email,
            createAt = Date(),
            provider = "",
            providerId = "",
            profileImageUrl = ""
        ),
        isVisible = isVisible
    )
}
