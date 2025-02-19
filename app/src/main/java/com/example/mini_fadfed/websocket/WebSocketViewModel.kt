package com.example.mini_fadfed.websocket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(private val webSocketManager: WebSocketManager) : ViewModel() {

    val messageFlow = webSocketManager.messageFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )

    fun connect() {
        webSocketManager.connect()
    }

    fun sendMessage(message: String) {
        webSocketManager.sendMessage(message)
    }

    fun closeConnection() {
        webSocketManager.close()
    }
}