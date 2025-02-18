package com.example.mini_fadfed.data.repository

import android.util.Log
import com.example.mini_fadfed.utils.Resource
import retrofit2.Response

abstract class BaseRepository {

    fun <T> handleResponse(response: Response<T>): Resource<T> {
        return try {
            if (response.isSuccessful) {
                return Resource.Success(response.body())
            } else {
                if (response.code() in 400..499) {
                    Log.e("api_error", response.errorBody().toString())
                    response.errorBody().let {
                        return Resource.Error("Something went wrong!")
                    }
                } else {
                    return Resource.Error(response.message())
                }
            }
        } catch (e: Error) {
            Resource.Error(e.message ?: "Something went wrong!")
        }
    }
}