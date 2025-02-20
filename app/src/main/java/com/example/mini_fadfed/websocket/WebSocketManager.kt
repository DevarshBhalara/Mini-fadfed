package com.example.mini_fadfed.websocket

import android.util.Log
import com.example.mini_fadfed.data.remote.MatchedUser
import com.example.mini_fadfed.utils.PreferenceHelper
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.*
import okio.ByteString
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    @Named("web_socket_client") private val okHttpClient: OkHttpClient,
    private val preferenceHelper: PreferenceHelper
) {

    var isSetFeatureOn = false
    private var webSocket: WebSocket? = null

    private val _messageFlow = MutableStateFlow<String?>(null)  // Observed by ViewModel
    val messageFlow = _messageFlow.asStateFlow()

    private val _matchFoundData = MutableStateFlow(MatchedUser())  // Observed by ViewModel
    val matchFoundData = _matchFoundData.asStateFlow()

    private val _matchedFoundData = MutableStateFlow(MatchedUser())
    val matchedFoundData = _matchedFoundData.asStateFlow()

    private var url = "wss://dev.wefaaq.net/?"

    fun connect() {
        val token = preferenceHelper.getString(PreferenceHelper.TOKEN)
        val devId = preferenceHelper.getString(PreferenceHelper.DEVICE_ID)
        val sessionId = preferenceHelper.getString(PreferenceHelper.SESSION_ID)
        val authToken = preferenceHelper.getString(PreferenceHelper.AUTH_TOKEN)

        if (token.isEmpty() || devId.isEmpty() || sessionId.isEmpty() || authToken.isEmpty()) {
            println("WebSocketManager: Missing credentials!")
            return
        }

        val url =
            "wss://dev.wefaaq.net/?token=$token&devid=$devId&session-id=$sessionId&auth=$authToken"

        Log.e("web_url", url)
        val request = Request.Builder()
            .url(url)
            .addHeader("X-SESSION-ID", sessionId)
            .addHeader("User-Agent", "Fadfed/4.7.7(Android/14)")
            .addHeader("Sec-WebSocket-Protocol", "v2.fadfedly.com")
            .build()
        webSocket = okHttpClient.newWebSocket(request, WebSocketListenerImpl())
    }


    fun sendMessage(message: String) {
        webSocket?.send(message)
        println("Sent Message: $message")
    }



    fun close() {
        isSetFeatureOn = false
        _messageFlow.value = ""
        webSocket?.close(1000, "Closing Connection")
    }

    private inner class WebSocketListenerImpl : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "Connected")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d("WebSocket", "Received: $text")
            _messageFlow.value = text

            try {
                // Parse incoming JSON as an array
                val jsonArray = JsonParser.parseString(text).asJsonArray
                val type = jsonArray[0].asString // First element: message type
                val data = jsonArray[1].asJsonObject // Second element: JSON object

                handleIncomingMessage(webSocket, type, data)

            } catch (e: Exception) {
                println("Error parsing WebSocket message: ${e.message}")
            }

        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("WebSocket", "Received Bytes: $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Closing: $code / $reason")
            webSocket.close(1000, null)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "Error: ${t.message}")
        }
    }

    fun handleIncomingMessage(webSocket: WebSocket, type: String, data: JsonObject) {
        when (type) {
            "session" -> handleSessionMessage(webSocket, data)
            "matched" -> handleMatchedUser(data)
//            "error" -> handleErrorMessage(data)
            else -> println("⚠️ Unknown message type: $type")
        }
    }

    private fun handleMatchedUser(data: JsonObject) {
        try {

            println(data.toString())
            if(!data.has("accepted")){
                val matchedData = Gson().fromJson(data, MatchedUser::class.java)
                _matchFoundData.value = matchedData
                println("Matched User Searech: $matchedData")

            } else {
                val matchedFoundData = Gson().fromJson(data, MatchedUser::class.java)

                _matchedFoundData.value = matchedFoundData.copy(
                    myAcceptance = _matchedFoundData.value.myAcceptance
                )
                println("Matched User  accept: $matchedFoundData")

            }


        } catch (e: Exception) {
            Log.e("matched_ex", e.printStackTrace().toString())
        }
    }

    private fun handleSessionMessage(webSocket: WebSocket, data: JsonObject) {
        val state = data.get("state").asString
        val sessionId = data.get("sessionId").asString
        val udid = data.get("udid").asString

        println("Session Updated: $sessionId | State: $state | Device ID: $udid")

        if (state == "ready") {
            sendFeatureOn(webSocket, sessionId)
        }
    }

    private fun sendFeatureOn(webSocket: WebSocket, sessionId: String?) {
        val jsonArray = JsonArray().apply {
            add("set")
            add(JsonObject().apply {
                add("features", JsonObject().apply {
                    addProperty("accept", true)
                })
            })
        }

        webSocket.send(jsonArray.toString())
        isSetFeatureOn = true
        println("Sent Message: $jsonArray")
    }

    fun onAcceptButton(toString: String) {

        if(_matchedFoundData.value.chatId.isEmpty()) {
            _matchedFoundData.update {
                it.copy(
                    chatId = _matchFoundData.value.chatId,
                    accepted = _matchFoundData.value.accepted,
                    myAcceptance = true,
                    initiate = _matchFoundData.value.initiate,
                )
            }
        }

        _matchedFoundData.update {
            it.copy(
                myAcceptance = true
            )
        }
        webSocket?.send(toString)
        Log.e("my_accept", _matchedFoundData.value.toString())
    }

    fun clearMatchedUserData() {
        _matchedFoundData.value = MatchedUser()
        _matchFoundData.value = MatchedUser()
    }
}