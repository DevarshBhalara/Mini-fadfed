package com.example.mini_fadfed.data.repository

import com.example.mini_fadfed.data.remote.RegisterUserRequest
import com.example.mini_fadfed.data.service.ApiService
import com.example.mini_fadfed.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RegisterUserRepository(
    private val apiService: ApiService
): BaseRepository() {

    fun registerUser(
        name: String, password: String) = flow {
        emit(Resource.Loading())
        try {
            apiService.createUser(name, RegisterUserRequest(name, password)).let { response ->
                val resource = handleResponse(response)
                emit(resource)
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

}