package com.copixelate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.model.UserModel
import com.copixelate.data.repo.UserRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepo = UserRepo

    val user: StateFlow<UserModel> =
        userRepo.userModelFlow().stateIn(
            scope = viewModelScope,
            initialValue = UserModel(),
            started = SharingStarted.WhileSubscribed()
        )

    fun signOut() = viewModelScope.launch {
        userRepo.signOut()
    }

    fun attemptSignIn(email: String, password: String) =
        viewModelScope.launch {
            userRepo.signIn(email, password)
        }

    fun attemptSignUp(email: String, password: String, displayName: String) =
        viewModelScope.launch {
            userRepo.signUp(email, password, displayName)
        }

}
