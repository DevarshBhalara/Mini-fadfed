package com.example.mini_fadfed.data.remote

data class RegisterUserResponse(
    val uuid: String,
    val token: String
)

data class RegisterUserRequest(
    val name: String,
    val password: String
)