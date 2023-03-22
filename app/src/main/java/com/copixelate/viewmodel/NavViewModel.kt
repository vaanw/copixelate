package com.copixelate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavViewModel : ViewModel() {

    private val _authState = MutableStateFlow(Auth.state)
    val authState = _authState.asStateFlow()

    fun updateAuthState(authState: Auth.State) {
        viewModelScope.launch {
            _authState.value = authState
        }
    }

}
