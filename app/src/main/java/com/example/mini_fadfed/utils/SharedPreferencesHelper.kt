package com.example.mini_fadfed.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesHelper @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getToken(): String? = prefs.getString("token", null)
    fun getDeviceId(): String? = prefs.getString("devid", null)
    fun getSessionId(): String? = prefs.getString("session-id", null)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun saveDeviceId(devid: String) {
        prefs.edit().putString("devid", devid).apply()
    }

    fun saveSessionId(sessionId: String) {
        prefs.edit().putString("session-id", sessionId).apply()
    }
}