package com.example.mini_fadfed.websocket

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_fadfed.data.model.Chat
import com.example.mini_fadfed.data.remote.MatchedUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    init {
        viewModelScope.launch {
            launch {
                lastSentChat.collect { message ->
                    message.let { if(it.chatId.isNotEmpty()) handleLastSendChat(it) }
                }

            }
        }
    }

    fun sendChat(chat: Chat) {
        webSocketManager.sentChat(chat)
    }

    private fun handleLastSendChat(chat: Chat) {



    }

    fun leaveChat(chatId: String) {
        webSocketManager.leaveChat(chatId)
    }

    fun messageRead(id: String) {
        webSocketManager.sendAckMessageSeen(id)
    }


}