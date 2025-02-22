package com.example.mini_fadfed.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

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


     fun generateRandomId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-"
        return (1..20)
            .map { chars[Random.nextInt(chars.length)] } // Select random character
            .joinToString("")
    }

    fun generateTimeStamp(): Int {
        return (System.currentTimeMillis() / 1000).toInt()
    }

    fun getTimeFromTimestamp(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }
}