package com.example.mini_fadfed

import android.app.Application
import com.example.mini_fadfed.utils.LocaleHelper
import com.example.mini_fadfed.websocket.WebSocketManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application() {

    @Inject
    lateinit var webSocketManager: WebSocketManager

    override fun onCreate() {
        super.onCreate()
        val defaultLanguage = LocaleHelper.getDeviceDefaultLanguage()
        val appLanguage = if (defaultLanguage == "ar") "ar" else "en"
        LocaleHelper.setAppLocale(this, appLanguage)
    }

    override fun onTerminate() {
        super.onTerminate()
        webSocketManager.close()
    }

}