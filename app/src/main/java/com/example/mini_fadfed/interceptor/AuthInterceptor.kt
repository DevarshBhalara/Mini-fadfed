package com.example.mini_fadfed.interceptor

import android.util.Log
import com.example.mini_fadfed.utils.Utils
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val devid: String, private val sessionId: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = Utils.generateToken(devid, sessionId) // Generate token
        val credentials = Credentials.basic(devid, token)

        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val newHttpUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("devid", devid)
            .addQueryParameter("session-id", sessionId)
            .addQueryParameter("token", token)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newHttpUrl)
            .addHeader("Authorization", credentials)
            .addHeader("X-SESSION-ID", sessionId)
            .addHeader("User-Agent", "Fadfed/4.7.7(Android/14)")
            .addHeader("Content-Type", "application/json")
            .build()

        Log.e("api_req", newRequest.toString()) // Log the request

        return chain.proceed(newRequest)
    }
}