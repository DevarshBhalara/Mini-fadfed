package com.example.mini_fadfed.websocket

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_fadfed.data.remote.MatchRequest
import com.example.mini_fadfed.data.remote.MatchedUser
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebSocketViewModel @Inject constructor(private val webSocketManager: WebSocketManager) :
    ViewModel() {

    val messageFlow = webSocketManager.messageFlow.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )

    val matchFoundData = webSocketManager.matchFoundData.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        MatchedUser()
    )

    private val _sessionReadyFlow = MutableLiveData(false)
    val sessionReadyFlow: LiveData<Boolean> = _sessionReadyFlow

    init {
        viewModelScope.launch {
            launch {
                messageFlow.collect { message ->
                    message?.let { if (!webSocketManager.isSetFeatureOn) handleWebSocketMessage(it) }
                }
            }
        }
    }

    fun getIsClosed(): Boolean = webSocketManager.isClosed

    private fun handleWebSocketMessage(message: String) {
        try {
            val jsonArray = JsonParser.parseString(message).asJsonArray
            val messageType = jsonArray[0].asString
            val dataObject = jsonArray[1].asJsonObject

            if (messageType == "session" && dataObject.get("state").asString == "flush") {
                _sessionReadyFlow.value = true
            }
        } catch (e: Exception) {
            Log.e("web_soc", "Error parsing message: ${e.message}")
        }
    }

    fun connect() {
        webSocketManager.connect()
    }

    fun searchUserForChat(matchRequest: MatchRequest) {
        val message = "match"
        val list: List<Any> = listOf(
            message,
            matchRequest
        )
        val gson = Gson()
        val json = gson.toJsonTree(list).asJsonArray
        webSocketManager.sendMessage(json.toString())
    }

    fun closeConnection() {
        webSocketManager.close()
    }
}