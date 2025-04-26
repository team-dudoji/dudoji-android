package com.dudoji.android.network.entity

import java.util.Date

data class UserStep(val stepCount : Int, val stepDate: Date)

// post user steps
data class UserStepsPostRequestDto(
    val userSteps: List<UserStep>
)

// get user step
data class UserStepGetRequestDto(
    val stepDate: Date
)

data class UserStepGetResponseDto(
    val userStep: UserStep
)

// get user steps
data class UserStepsGetRequestDto(
    val startDate: DateRequestDto,
    val endDate: DateRequestDto
)

data class UserStepsGetResponseDto(
    val userSteps: List<UserStep>
)

data class DateRequestDto(
    val stepDate: Date
)
