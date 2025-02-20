package com.example.mini_fadfed.websocket

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_fadfed.data.remote.AcceptMessage
import com.example.mini_fadfed.data.remote.MatchedUser
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    init {
        viewModelScope.launch {
            matchFoundData.collect { message ->
                message.let { if(it.chatId.isNotEmpty()) handleWebSocketMessage(it) }
            }
        }
    }

    private fun handleWebSocketMessage(data: MatchedUser) {
        Log.e("web_socket", data.toString())
    }

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

}