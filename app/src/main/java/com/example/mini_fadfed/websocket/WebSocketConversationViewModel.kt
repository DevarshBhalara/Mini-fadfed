package com.example.mini_fadfed.websocket

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_fadfed.data.model.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WebSocketConversationViewModel @Inject constructor(
    private val webSocketManager: WebSocketManager
): ViewModel() {


    val lastSentChat = webSocketManager.lastSendChat.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        Chat()
    )

    val lastReceivedChat = webSocketManager.lastReceivedChat.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )

    val ackwonledge = webSocketManager.acknowledgedData.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )

    val leaveChat = webSocketManager.leaveChatFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun sendChat(chat: Chat) {
        webSocketManager.sentChat(chat)
    }

    fun leaveChat(chatId: String) {
        Log.e("leave_chat_vm", chatId)
        webSocketManager.leaveChat(chatId)
    }

    fun messageRead(id: String) {
        webSocketManager.sendAckMessageSeen(id)
    }

    fun setLastUserName(name: String) {
        webSocketManager.lastLeaveUserId = name
    }


}