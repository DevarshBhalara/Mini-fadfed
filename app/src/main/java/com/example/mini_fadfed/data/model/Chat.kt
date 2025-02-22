package com.example.mini_fadfed.data.model

import com.example.mini_fadfed.utils.Utils

data class Chat(
    val chatId: String = "",
    val receiverName: String = "",
    val content: String = "",
    val isRead: Boolean = false,
    val isSend: Boolean = false,
    val messageType: MessageType = MessageType.SEND,
    val timestamp: Int = Utils.generateTimeStamp(),
    val senderId: String = Utils.generateRandomId(),
) {
    fun getCurrentChatTime(): String {
        return Utils.getTimeFromTimestamp(timestamp.toLong())
    }
}

enum class MessageType {
    SEND,
    RECEIVE
}