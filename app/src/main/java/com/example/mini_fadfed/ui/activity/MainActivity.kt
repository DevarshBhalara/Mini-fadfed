package com.example.mini_fadfed.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.mini_fadfed.R
import com.example.mini_fadfed.data.model.Gender
import com.example.mini_fadfed.databinding.ActivityMainBinding
import com.example.mini_fadfed.ui.viewmodel.RegisterUserViewModel
import com.example.mini_fadfed.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: RegisterUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        addListeners()
        bindObservable()
    }

    private fun addListeners() {
        binding.edtName.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onNameChanged(binding.edtName.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.ivMale.setOnClickListener {
            viewModel.onGenderChanged(Gender.MALE)
        }

        binding.ivFemale.setOnClickListener {
            viewModel.onGenderChanged(Gender.FEMALE)
        }

        binding.btnContinue.setOnClickListener {
            viewModel.registerUser()
        }
    }

    private fun bindObservable() {

        lifecycleScope.launch {
            launch {
                viewModel.state.collectLatest {
                    Log.e("api_res", it.toString())

                    it.success?.let {
                        PreferenceHelper(this@MainActivity).putString(PreferenceHelper.UDID, it.udid)
                        PreferenceHelper(this@MainActivity).putString(PreferenceHelper.AUTH_TOKEN, it.token)
                        startActivity(Intent(this@MainActivity, HomeScreenActivity::class.java))
                    }

                }
            }
        }
    }

    private fun init() {

        if(PreferenceHelper(this).getString("token", "").isNotEmpty()) {
            startActivity(Intent(this@MainActivity, HomeScreenActivity::class.java))
        }

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }
}