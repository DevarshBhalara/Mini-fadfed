package com.example.mini_fadfed.data.remote

data class MatchedUser(
    val chatId: String = "",
    val udid: String = "",
    val premium: Boolean = false,
    val lang: List<String> = listOf(),
    val initiate: Boolean = false,
    val accepted: Boolean = false,
    val myAcceptance: Boolean = false,
)
