package com.example.mini_fadfed.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mini_fadfed.R
import com.example.mini_fadfed.data.model.Chat
import com.example.mini_fadfed.data.model.MessageType
import com.example.mini_fadfed.databinding.ItemMessageLeftBinding
import com.example.mini_fadfed.databinding.ItemMessageRightBinding

class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val chats: MutableList<Chat> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        return when(chats[position].messageType) {
            MessageType.SEND -> MessageType.SEND.ordinal
            MessageType.RECEIVE -> MessageType.RECEIVE.ordinal
        }
    }

    inner class LeftMessageViewHolder(private val binding: ItemMessageLeftBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            binding.chat = chat
        }
    }

    inner class RightMessageViewHolder(private val binding: ItemMessageRightBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.chat = chat
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(MessageType.entries[viewType]) {
            MessageType.SEND -> RightMessageViewHolder(ItemMessageRightBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
            MessageType.RECEIVE -> LeftMessageViewHolder(ItemMessageLeftBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LeftMessageViewHolder -> holder.bind(chats[position])
            is RightMessageViewHolder -> holder.bind(chats[position])
        }
    }

    fun addChat(chat: Chat) {
        chats.add(chat)
        notifyItemInserted(chats.count())
    }

    fun updateStatusSent(id: String) {
        Log.e("chat_adapter_", id)

        chats.find { it.senderId == id }?.let {
            val index = chats.indexOf(it)
            chats[index] = it.copy(isSend = true, isRead = false)
            notifyItemChanged(index)
        } ?: {
            Log.e("chat_adapter_", "not found ")
        }
    }

    fun updateStatusSeen(id: String) {
        Log.e("chat_adapter", id)

        chats.find { it.senderId == id }?.let {
            val index = chats.indexOf(it)
            chats[index] = it.copy(isSend = false, isRead = true)
            notifyItemChanged(index)
        } ?: {
            Log.e("chat_adapter", "not found ")
        }
    }


}