package com.example.mini_fadfed.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mini_fadfed.databinding.ActivityHomeScreenBinding
import com.example.mini_fadfed.utils.PreferenceHelper
import com.example.mini_fadfed.websocket.WebSocketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeScreenActivity : AppCompatActivity() {

    private val viewModel: WebSocketViewModel by viewModels()
    private lateinit var binding: ActivityHomeScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("devid", PreferenceHelper(this).getString(PreferenceHelper.DEVICE_ID, ""))
        Log.e("devid", PreferenceHelper(this).getString(PreferenceHelper.UDID, ""))
        Log.e("devid", PreferenceHelper(this).getString(PreferenceHelper.SESSION_ID, ""))
        Log.e("devid", PreferenceHelper(this).getString(PreferenceHelper.AUTH_TOKEN, ""))
        Log.e("devid", PreferenceHelper(this).getString(PreferenceHelper.TOKEN, ""))

        init()
        addListeners()
        bindObservables()
    }

    private fun bindObservables() {

        lifecycleScope.launch {
            launch {
                viewModel.messageFlow.collectLatest { message ->
                    message?.let { Log.e("web_soc", it) }
                }
            }
        }
    }

    private fun addListeners() {
        binding.ivSearch.setOnClickListener {
            viewModel.connect()
        }
    }

    private fun init() {

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeConnection()
    }
}