package com.example.mini_fadfed.websocket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_fadfed.data.remote.AcceptMessage
import com.example.mini_fadfed.data.remote.MatchedUser
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WebSocketMatchedUserViewModel  @Inject constructor(
    private val webSocketManager: WebSocketManager
): ViewModel(){

    val matchFoundData = webSocketManager.matchedFoundData.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        MatchedUser()
    )

    val leaveChat = webSocketManager.leaveChatFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun onAcceptButton() {
        val message = "accept"
        val acceptChat = AcceptMessage(chatId = matchFoundData.value.chatId)

        val list: List<Any> = listOf(
            message,
            acceptChat
        )
        val gson = Gson()
        val json = gson.toJsonTree(list).asJsonArray
        webSocketManager.onAcceptButton(json.toString())
    }

    fun clearData() {
        webSocketManager.clearMatchedUserData()
    }

    fun onLeaveButton() {
        webSocketManager.leaveChat(matchFoundData.value.chatId)
    }

    fun setLastUserName(recName: String) {
        webSocketManager.lastLeaveUserId = recName
    }

}