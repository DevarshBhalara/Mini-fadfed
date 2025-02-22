package com.example.mini_fadfed.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mini_fadfed.data.remote.MatchRequest
import com.example.mini_fadfed.databinding.ActivitySearchingScreenBinding
import com.example.mini_fadfed.websocket.WebSocketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchingScreenActivity : AppCompatActivity() {

    private val webSocketViewModel: WebSocketViewModel by viewModels()
    private lateinit var binding: ActivitySearchingScreenBinding
    private val handler = Handler(Looper.getMainLooper())

    private var progress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        addListeners()
        bindObservables()
    }

    private fun bindObservables() {
        lifecycleScope.launch {
            webSocketViewModel.matchFoundData.collect { matchedData ->
                if(matchedData.chatId.isNotEmpty()) {
                    navigateToMatchedUserScreen(matchedData.udid)
                }
            }
        }
    }

    private val leaveChatLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val isChatLeft = result.data?.getBooleanExtra("chat_left", false) ?: false
            if (isChatLeft) {
                Log.e("chat_left", "true")
                init()
            }
        }
    }


    private fun navigateToMatchedUserScreen(name: String) {
        leaveChatLauncher.launch(Intent(this, MatchedUserActivity::class.java).apply {
            putExtra("name", name)
        })
    }

    private fun addListeners() {

    }

    private fun init() {
        progress = 0
        startProgress()
        webSocketViewModel.searchUserForChat(MatchRequest("R", "modern"))
    }

    private fun startProgress() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (progress >= 100) {
                    progress = 0 // Reset to 0
                } else {
                    progress++
                }

                binding.progressBar.progress = progress
                handler.postDelayed(this, 1000) // 1 second = 1 progress
            }
        }, 1000)
    }

}
