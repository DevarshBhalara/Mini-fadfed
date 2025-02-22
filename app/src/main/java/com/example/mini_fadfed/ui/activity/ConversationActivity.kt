package com.example.mini_fadfed.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mini_fadfed.data.model.Chat
import com.example.mini_fadfed.data.model.MessageType
import com.example.mini_fadfed.databinding.ActivityConversationBinding
import com.example.mini_fadfed.ui.adapter.ChatAdapter
import com.example.mini_fadfed.websocket.WebSocketConversationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConversationActivity : AppCompatActivity() {

    private val adapter = ChatAdapter()
    private lateinit var binding: ActivityConversationBinding
    private val viewModel: WebSocketConversationViewModel by viewModels()
    private var chatId = ""
    private var recName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConversationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        addListeners()
        bindObservables()
    }

    private fun bindObservables() {
        lifecycleScope.launch {
            launch {
                viewModel.lastReceivedChat.collectLatest {
                    it?.let {
                       if(it.chatId.isNotEmpty()) {
                           adapter.addChat(it)
                           binding.rvChat.scrollToPosition(adapter.itemCount - 1)
                           viewModel.messageRead(it.receiverName + ":" + it.senderId)
                       }
                    }
                }
            }

            launch {
                viewModel.lastSentChat.collectLatest {
                    it.let {
                        if (it.chatId.isNotEmpty()) {
                            Log.e("chat_adapter_", it.toString())
                            delay(100)
                            adapter.updateStatusSent(it.senderId)
                        }
                    }
                }
            }

            launch {
                viewModel.ackwonledge.collectLatest {
                    it?.let {
                        if(it.status.isNotEmpty()) {
                            if(it.status == "seen") {
                                Log.e("chat_adapter", "observ act")
                                adapter.updateStatusSeen(it.ref)
                            }
                        }
                    }

                }
            }

            launch {
                viewModel.leaveChat.collectLatest {
                    if(it) {
                        val intent = Intent(this@ConversationActivity, SearchingScreenActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }


    private fun addListeners() {
        binding.btnSend.setOnClickListener {

            if (binding.edtMessage.text.toString().trim().isNotEmpty()) {
                val chat = Chat(
                    chatId = chatId,
                    content = binding.edtMessage.text.toString(),
                    messageType = MessageType.SEND,
                    receiverName = recName
                )
                viewModel.sendChat(chat)
                adapter.addChat(chat)
                binding.rvChat.scrollToPosition(adapter.itemCount - 1)
                binding.edtMessage.setText("")
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun init() {
        chatId = intent.getStringExtra("chatId") ?: ""
        recName = intent.getStringExtra("recName") ?: ""
        binding.ivUserName.text = recName
        binding.rvChat.adapter = adapter

        viewModel.setLastUserName(recName)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("leave_chat", "conv_bacl")
                viewModel.leaveChat(chatId)
                val intent = Intent(this@ConversationActivity, SearchingScreenActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDes", "covEnd")
    }


}