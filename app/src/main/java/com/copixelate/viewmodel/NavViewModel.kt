package com.copixelate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NavViewModel : ViewModel() {

    private val _isSignedIn = MutableStateFlow(
        when (Auth.state) {
            Auth.State.SIGNED_IN -> true
            Auth.State.SIGNED_OUT -> false
        }
    )
    val isSignedIn = _isSignedIn.asStateFlow()

    fun setSignedIn() = viewModelScope.launch { _isSignedIn.value = true }
    fun setSignedOut() = viewModelScope.launch { _isSignedIn.value = false }

}
