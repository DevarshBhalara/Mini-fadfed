package com.example.mini_fadfed.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHelper @Inject constructor(context: Context) {

    companion object {
        const val TOKEN = "token"
        const val UDID = "udid"
        const val SESSION_ID = "session_id"
        const val AUTH_TOKEN = "auth_token"
        const val DEVICE_ID = "device_id"
    }

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun getString(key: String, defValue: String = ""): String {
        return preferences.getString(key, defValue) ?: defValue
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getInt(key: String, defValue: Int = 0): Int {
        return preferences.getInt(key, defValue)
    }

    fun getLong(key: String, defValue: Long = 0L): Long {
        return preferences.getLong(key, defValue)
    }

    fun getFloat(key: String, defValue: Float = 0f): Float {
        return preferences.getFloat(key, defValue)
    }

    fun clearPreferences() {
        preferences.edit().clear().apply()
    }
}