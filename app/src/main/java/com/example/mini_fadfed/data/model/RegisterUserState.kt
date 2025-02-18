package com.example.mini_fadfed.data.model

import com.example.mini_fadfed.data.remote.RegisterUserResponse

data class RegisterUserState(
    var name: String = "",
    var gender: Gender = Gender.MALE,
    var isNameValid: Boolean = false,
    var isLoading: Boolean = false,
    var errorMessage: String? = null,
    var success: RegisterUserResponse? = null
)

enum class Gender {
    MALE, FEMALE
}