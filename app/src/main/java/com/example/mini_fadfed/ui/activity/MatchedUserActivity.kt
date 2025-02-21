package com.example.mini_fadfed.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mini_fadfed.data.remote.MatchedUser
import com.example.mini_fadfed.databinding.ActivityMatchedUserScreenBinding
import com.example.mini_fadfed.websocket.WebSocketMatchedUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MatchedUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchedUserScreenBinding
    private val viewModel: WebSocketMatchedUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMatchedUserScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        addListeners()
        bindObservables()
    }

    private fun bindObservables() {
       lifecycleScope.launch {
           launch {
               viewModel.matchFoundData.collectLatest {
                   if(it.chatId.isNotEmpty()) {
                       handleMatchFoundUser(it)
                   }
               }
           }

           launch {
               viewModel.leaveChat.collectLatest {
                   if(it) {
                       finish()
                   }
               }
           }
       }
    }

    private fun handleMatchFoundUser(matchedUser: MatchedUser) {
        if(matchedUser.accepted && matchedUser.myAcceptance) {
            navigateToConversationScreen(matchedUser.chatId, matchedUser.udid)
        }
    }

    private fun navigateToConversationScreen(chatId: String, recName: String) {
        startActivity(Intent(this, ConversationActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("recName", recName)
        })
    }

    private fun addListeners() {

        binding.btnAccept.setOnClickListener {
            viewModel.onAcceptButton()
        }

    }

    private fun init() {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearData()
    }
}