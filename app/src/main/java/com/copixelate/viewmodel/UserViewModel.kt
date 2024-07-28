package com.copixelate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.copixelate.data.repo.AuthRepo
import com.copixelate.data.repo.UserRepo
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val authRepo = AuthRepo
    private val userRepo = UserRepo

    val auth = authRepo.authModelFlow

//    val auth: StateFlow<AuthModel> =
//        authRepo.authModelFlow().stateIn(
//            scope = viewModelScope,
//            initialValue = AuthModel(),
//            started = SharingStarted.WhileSubscribed()
//        )

    fun signOut() = viewModelScope.launch {
        authRepo.signOut()
    }

    fun attemptSignIn(email: String, password: String) =
        viewModelScope.launch {
            authRepo.signIn(email, password)
        }

    fun attemptSignUp(email: String, password: String, displayName: String) =
        viewModelScope.launch {
            authRepo.signUp(email, password, displayName)
        }

}
