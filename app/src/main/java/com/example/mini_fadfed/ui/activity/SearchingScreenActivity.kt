package com.example.mini_fadfed.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mini_fadfed.data.remote.MatchRequest
import com.example.mini_fadfed.databinding.ActivitySearchingScreenBinding
import com.example.mini_fadfed.websocket.WebSocketViewModel
import dagger.hilt.android.AndroidEntryPoint

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

    }

    private fun addListeners() {

    }

    private fun init() {
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
