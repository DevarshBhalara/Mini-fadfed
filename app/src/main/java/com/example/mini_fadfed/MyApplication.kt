package com.example.mini_fadfed

import android.app.Application
import android.util.Log
import com.example.mini_fadfed.websocket.WebSocketManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application() {

    @Inject
    lateinit var webSocketManager: WebSocketManager

    override fun onTerminate() {
        super.onTerminate()
        Log.e("myapp" ,"onterminate")
        webSocketManager.close()
    }

}