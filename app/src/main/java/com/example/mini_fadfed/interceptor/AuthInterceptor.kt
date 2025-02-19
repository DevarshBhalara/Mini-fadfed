package com.example.mini_fadfed.interceptor

import android.content.Context
import android.util.Log
import com.example.mini_fadfed.utils.PreferenceHelper
import com.example.mini_fadfed.utils.Utils
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context, private val devid: String, private val sessionId: String) : Interceptor {
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


        val response = chain.proceed(newRequest)

        if (response.isSuccessful) {
            saveToPreferences(context, token, devid, sessionId)
        }

        return response
    }

    private fun saveToPreferences(context: Context, token: String, devid: String, sessionId: String) {
        val pref = PreferenceHelper(context)
        pref.putString(PreferenceHelper.TOKEN, token)
        pref.putString(PreferenceHelper.DEVICE_ID, devid)
        pref.putString(PreferenceHelper.SESSION_ID, sessionId)
    }
}