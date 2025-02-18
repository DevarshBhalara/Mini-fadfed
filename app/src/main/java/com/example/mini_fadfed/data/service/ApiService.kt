package com.example.mini_fadfed.data.service

import com.example.mini_fadfed.data.remote.RegisterUserRequest
import com.example.mini_fadfed.data.remote.RegisterUserResponse
import com.example.mini_fadfed.utils.Resource
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    @PUT("@fadfedx/auth")
    suspend fun createUser(
        @Query("udid") name: String,
        @Body userRequest: RegisterUserRequest): retrofit2.Response<RegisterUserResponse>
}