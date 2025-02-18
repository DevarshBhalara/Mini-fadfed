package com.example.mini_fadfed.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_fadfed.data.model.Gender
import com.example.mini_fadfed.data.model.RegisterUserState
import com.example.mini_fadfed.data.repository.RegisterUserRepository
import com.example.mini_fadfed.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val registerUserRepository: RegisterUserRepository
): ViewModel() {

    private val _state = MutableStateFlow(RegisterUserState())
    val state: StateFlow<RegisterUserState> = _state.asStateFlow()

    fun onNameChanged(name: String) {
        _state.value = _state.value.copy(name = name)
        _state.value = _state.value.copy(isNameValid = name.length >= 3)
    }

    fun onGenderChanged(gender: Gender) {
        _state.update {
            it.copy(gender = gender)
        }
    }

    fun registerUser() {
        viewModelScope.launch {

            registerUserRepository.registerUser(_state.value.name, "qweasd").collectLatest { resource ->

                when(resource) {
                    is Resource.Error -> {
                        Log.e("api_register", resource.message.toString())
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Success -> {

                        resource.data?.let { response ->
                            _state.update {
                                it.copy(
                                    success = response,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                        }
                    }
                }

            }
        }
    }

}