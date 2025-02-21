package com.example.mini_fadfed.ui.binding

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.mini_fadfed.R
import com.example.mini_fadfed.data.model.Chat

@BindingAdapter("messageStatus")
fun ImageView.setMessageStatus(chat: Chat) {
    val statusIcon = when {
        chat.isSend && !chat.isRead -> {
            Log.e("clock_ic", "sent")
            R.drawable.ic_msg_send
        }     // Message sent
        chat.isRead -> {
            Log.e("clock_ic", "read")
            R.drawable.ic_msg_read
        }       // Message seen
        else -> {
            Log.e("clock_ic", "else")
            R.drawable.ic_clock              // Default (optional)
        }
    }
    setImageResource(statusIcon)
}