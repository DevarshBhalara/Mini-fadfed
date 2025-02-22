package com.example.mini_fadfed.data.remote

import com.example.mini_fadfed.utils.Utils

data class SendMessage(
    val content: String,
    val id: String = Utils.generateRandomId(),
    val to: String,
    val ts: Int = Utils.generateTimeStamp(),
)

data class ReceivedMessage(
    val content: String,
    val id: String,
    val by: String,
    val chatId: String,
    val ts: Long
)

data class SendTyping(
    val to: String,  //send receiver's name (udid)
)

data class ReceiveTyping(
    val chatId: String,
    val by: String
)

data class AckwonledgeReceived(
    val status: String,
    val id: String,
    val ref: String,
    val by: String
)

data class SendAckwonledge(
    val status: String,
    val id: Long,
    val ref: String
)

data class LeaveChat(
    val chatId: String
)