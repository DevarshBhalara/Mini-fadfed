package com.example.mini_fadfed.websocket

import android.util.Log
import com.example.mini_fadfed.data.model.Chat
import com.example.mini_fadfed.data.model.MessageType
import com.example.mini_fadfed.data.remote.AckwonledgeReceived
import com.example.mini_fadfed.data.remote.LeaveChat
import com.example.mini_fadfed.data.remote.MatchRequest
import com.example.mini_fadfed.data.remote.MatchedUser
import com.example.mini_fadfed.data.remote.ReceivedMessage
import com.example.mini_fadfed.data.remote.SendAckwonledge
import com.example.mini_fadfed.data.remote.SendMessage
import com.example.mini_fadfed.utils.PreferenceHelper
import com.example.mini_fadfed.utils.Utils
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

    var isClosed = true
    private var isSessionReady = false
    var isSetFeatureOn = false

    var lastLeaveUserId = "-"

    private var webSocket: WebSocket? = null

    private val _messageFlow = MutableStateFlow<String?>(null)  // Observed by ViewModel
    val messageFlow = _messageFlow.asStateFlow()

    private val _searchMatchFoundData = MutableStateFlow(MatchedUser())  // Observed by ViewModel
    val searchMatchFoundData = _searchMatchFoundData.asStateFlow()

    private val _matchedFoundData = MutableStateFlow(MatchedUser())
    val matchedFoundData = _matchedFoundData.asStateFlow()

    private val _leaveChatFlow = MutableStateFlow(false)  // Flow to observe leave event
    val leaveChatFlow = _leaveChatFlow.asStateFlow()

    private val _lastSendChat = MutableStateFlow(Chat())
    val lastSendChat = _lastSendChat.asStateFlow()

    private val _lastReceivedChat = MutableStateFlow(Chat())
    val lastReceivedChat = _lastReceivedChat.asStateFlow()

    private val _acknowledgedData = MutableStateFlow<AckwonledgeReceived?>(null)
    val acknowledgedData = _acknowledgedData.asStateFlow()

    fun connect() {
        isClosed = false

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
        isClosed = true
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
                val jsonArray = JsonParser.parseString(text).asJsonArray
                val type = jsonArray[0].asString // First element: message type
                val data = jsonArray[1].asJsonObject // Second element: JSON object

                handleIncomingMessage(webSocket, type, data)

            } catch (e: Exception) {
                Log.e("chat_adapter", e.message ?: "")
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

    private var lastMatchedTimestamp: Long = 0L
    private val leaveIgnoreThreshold = 2000L // 2 seconds threshold

    fun handleIncomingMessage(webSocket: WebSocket, type: String, data: JsonObject) {
        when (type) {
            "session" -> handleSessionMessage(webSocket, data)
            "matched" -> if(isSessionReady)  {
                handleMatchedUser(data)
                lastMatchedTimestamp = System.currentTimeMillis() // Store matched time
            }
            "message" -> if (isSessionReady) handleIncomingChatMessage(data)
            "ack" -> if(isSessionReady) handleAcknowledge(data)
            "leave" -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastMatchedTimestamp > leaveIgnoreThreshold) {
                    handleLeaveChat(data) // Only handle if it's not immediately after "matched"
                } else {
//                    searchUserForChat(MatchRequest("R", "modern"))
                    Log.e("leave_uesr_chat", "Ignoring leave event received right after matched")
                }
            }
            else -> println("Unknown message type: $type")
        }
    }

    private fun searchUserForChat(matchRequest: MatchRequest) {
        val message = "match"
        val list: List<Any> = listOf(
            message,
            matchRequest
        )
        val gson = Gson()
        val json = gson.toJsonTree(list).asJsonArray
        sendMessage(json.toString())
        Log.e("web_socket_search", json.toString())
    }

    private fun handleLeaveChat(data: JsonObject) {
        try {
            val chatId = data.get("chatId").asString
            Log.e("leave_chat", chatId)
            if(searchMatchFoundData.value.chatId == chatId) {
                lastLeaveUserId = searchMatchFoundData.value.udid
                _leaveChatFlow.value = true
                clearMatchedUserData()
            } else {
                Log.e("leave_chat", chatId)
            }
        } catch (e: Exception) {
            Log.e("leave_chat", e.printStackTrace().toString())
            _leaveChatFlow.value = false
        }
    }

    private fun handleMatchedUser(data: JsonObject) {
        try {
            Log.e("leave_user", lastLeaveUserId + "--" + data)

            if(!data.has("udid")) return

            if(data.get("udid").asString.trim() == lastLeaveUserId)  {
                searchUserForChat(MatchRequest("R", "modern"))
                return
            }

//            lastLeaveUserId = data.get("udid").asString

            println(data.toString())

            if(!data.has("accepted")){
                val matchedData = Gson().fromJson(data, MatchedUser::class.java)
                _searchMatchFoundData.value = matchedData
                println("Matched User Search: $matchedData")

            } else {
                val matchedFoundData = Gson().fromJson(data, MatchedUser::class.java)

                _matchedFoundData.value = matchedFoundData.copy(
                    myAcceptance = _matchedFoundData.value.myAcceptance
                )
                println("Matched User accept: $matchedFoundData")
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
            isSessionReady = true
            sendFeatureOn(webSocket)
        }
    }

    private fun sendFeatureOn(webSocket: WebSocket) {
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
                    chatId = _searchMatchFoundData.value.chatId,
                    accepted = _searchMatchFoundData.value.accepted,
                    myAcceptance = true,
                    initiate = _searchMatchFoundData.value.initiate,
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

    fun sentChat(chat: Chat) {

        val message = Gson().toJsonTree(SendMessage(
            content = chat.content,
            to = chat.receiverName,
            id = chat.senderId,
            ts = chat.timestamp
        )).asJsonObject

        val list = listOf(
            "send",
            message
        )
        val json = Gson().toJsonTree(list).asJsonArray
        webSocket?.send(json.toString())
        Log.e("web_soc_chat_send", message.toString())
        _lastSendChat.value = chat.copy(isSend = true)

    }

    private fun handleIncomingChatMessage(data: JsonObject) {
        try {
            val receivedChat = Gson().fromJson(data, ReceivedMessage::class.java)
            _lastReceivedChat.value = Chat(
                receiverName = receivedChat.by,
                content = receivedChat.content,
                chatId = receivedChat.chatId,
                senderId = receivedChat.id.split(":")[1],
                messageType = MessageType.RECEIVE
            )
        } catch (e: Exception) {
            Log.e("WebSocket", "Error parsing chat message: ${e.message}")
        }
    }

    fun sendAckMessageSeen(id: String) {
        val sendAck = Gson().toJsonTree(SendAckwonledge(
            status = "seen",
            id = Utils.generateTimeStamp().toLong(),
            ref = id
        )).asJsonObject
        val list = listOf(
            "ack",
            sendAck
        )
        val json = Gson().toJsonTree(list).asJsonArray
        webSocket?.send(json.toString())
    }

    private fun handleAcknowledge(data: JsonObject) {
        if(data.has("status")) {
            Log.e("chat_adapter", "web soc ${data.toString()}")
            val ack = Gson().fromJson(data, AckwonledgeReceived::class.java)
            _acknowledgedData.value = ack
        }

    }

    fun leaveChat(chatId: String) {
        Log.e("leave_user_chat", lastLeaveUserId)

        _lastSendChat.value = Chat()
        _lastReceivedChat.value = Chat()
        clearMatchedUserData()

        val chat = Gson().toJsonTree(LeaveChat(chatId = chatId)).asJsonObject
        val list = listOf(
            "leave",
            chat
        )
        val json = Gson().toJsonTree(list).asJsonArray
        Log.e("leave_user_chat", json.toString())
        webSocket?.send(json.toString())
    }

    fun clearMatchedUserData() {
        _matchedFoundData.value = MatchedUser()
        _searchMatchFoundData.value = MatchedUser()
    }

}
