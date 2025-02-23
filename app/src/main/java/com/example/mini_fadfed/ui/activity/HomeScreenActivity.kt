package com.example.mini_fadfed.ui.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mini_fadfed.databinding.ActivityHomeScreenBinding
import com.example.mini_fadfed.utils.LocaleHelper
import com.example.mini_fadfed.utils.PreferenceHelper
import com.example.mini_fadfed.websocket.WebSocketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeScreenActivity : AppCompatActivity() {

    private val viewModel: WebSocketViewModel by viewModels()
    private lateinit var binding: ActivityHomeScreenBinding

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        val defaultLanguage = LocaleHelper.getDeviceDefaultLanguage()
        val appLanguage = if (defaultLanguage == "ar") "ar" else "en"
        LocaleHelper.setAppLocale(this, appLanguage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            launch {
                viewModel.sessionReadyFlow.observe(this@HomeScreenActivity) { isReady ->
                    if (isReady) {
                        navigateToNextActivity()
                    }
                }
            }
        }
    }

    private fun navigateToNextActivity() {
        startActivity(Intent(this, SearchingScreenActivity::class.java))
    }

    private fun addListeners() {
        binding.ivSearch.setOnClickListener {
            if (viewModel.getIsClosed()) {
                viewModel.connect()
            } else {
                navigateToNextActivity()
            }
        }

        binding.ivFemale.setOnClickListener {
            binding.ivFemale.alpha = 1f
            binding.ivMale.alpha = 0.3f
            binding.ivBoth.alpha = 0.3f
        }

        binding.ivMale.setOnClickListener {
            binding.ivFemale.alpha = 0.3f
            binding.ivMale.alpha = 1f
            binding.ivBoth.alpha = 0.3f
        }

        binding.ivBoth.setOnClickListener {
            binding.ivFemale.alpha = 0.3f
            binding.ivMale.alpha = 0.3f
            binding.ivBoth.alpha = 1f
        }
    }


    private fun init() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.closeConnection()
                finishAffinity()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.closeConnection()
    }
}