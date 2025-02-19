package com.example.mini_fadfed.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Utils {

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
    }

    fun generateSessionId(): String {
        return UUID.randomUUID().toString()
    }

    fun generateToken(deviceId: String, sessionId: String): String {
        val algorithm = "HmacMD5"
        val keySpec = SecretKeySpec(sessionId.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(keySpec)
        val hashBytes = mac.doFinal(deviceId.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}