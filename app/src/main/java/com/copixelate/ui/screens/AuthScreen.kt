package com.copixelate.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.copixelate.data.Auth
import com.copixelate.data.AuthResult
import com.copixelate.ui.common.SecretTextInputField
import com.copixelate.ui.common.ValidatedTextInputField
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.nav.compareRoute
import com.copixelate.ui.nav.navigateTopLevel
import com.copixelate.ui.util.InputValidation
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.viewmodel.NavViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(navController: NavController, navViewModel: NavViewModel) {

    val onSignUp = { email: String, displayName: String, password: String ->
        Auth.createAccount(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.d("onSignUp", "successful")
                    Auth.updateAccount(displayName) {
                        navViewModel.setSignedIn()
                        if (navController.compareRoute(navInfo = NavInfo.Login)) {
                            navController.navigateTopLevel(navInfo = NavInfo.Contacts)
                        }
                    }
                }

                is AuthResult.Failure -> {
                    Log.d("onSignUp", "failed: ${result.message}")
                }
            }
        }
    }

    val onSignIn = { email: String, password: String ->
        Auth.signIn(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.d("onSignIn", "successful")
                    navViewModel.setSignedIn()
                    if (navController.compareRoute(navInfo = NavInfo.Login)) {
                        navController.navigateTopLevel(navInfo = NavInfo.Contacts)
                    }
                }

                is AuthResult.Failure -> {
                    Log.d("onSignIn", "failed: ${result.message}")
                }
            }
        }
    }

    AuthScreenContent(
        onSignUp = onSignUp,
        onSignIn = onSignIn
    )

}

@Composable
private fun AuthScreenContent(
    onSignUp: (email: String, displayName: String, password: String) -> Unit,
    onSignIn: (email: String, password: String) -> Unit
) {
    Scroller {
        AuthForm(
            onSignUp = onSignUp,
            onSignIn = onSignIn
        )
    }
}

@Composable
private fun Scroller(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}

private enum class FormAction {
    SIGN_IN, SIGN_UP;

    fun next() = when (this) {
        SIGN_IN -> SIGN_UP
        SIGN_UP -> SIGN_IN
    }
}

@Composable
private fun AuthForm(
    onSignUp: (email: String, displayName: String, password: String) -> Unit,
    onSignIn: (email: String, password: String) -> Unit
) {

    var action: FormAction by rememberSaveable { mutableStateOf(FormAction.SIGN_IN) }

    var email: String by rememberSaveable { mutableStateOf("") }
    var isEmailValid by rememberSaveable { mutableStateOf(false) }

    var displayName: String by rememberSaveable { mutableStateOf("") }
    var isDisplayNameValid by rememberSaveable { mutableStateOf(false) }

    var password: String by rememberSaveable { mutableStateOf("") }
    var isPasswordValid by rememberSaveable { mutableStateOf(true) }

    var passwordAgain: String by remember { mutableStateOf("") }
    var isPasswordAgainValid by rememberSaveable { mutableStateOf(true) }

    val composableScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp),
    ) {

        // Email input field
        ValidatedTextInputField(
            value = email,
            onValueChange = { value -> email = value },
            label = { Text(text = "Email") },
            validity = InputValidation.checkEmail(email)
                .also { validity -> isEmailValid = validity.isValid }
        )

        // Display Name input field
        AnimatedVisibility(
            visible = when (action) {
                FormAction.SIGN_IN -> false
                FormAction.SIGN_UP -> true
            }
        ) {
            ValidatedTextInputField(
                value = displayName,
                onValueChange = { value -> displayName = value },
                label = { Text(text = "Display Name") },
                validity = InputValidation.checkDisplayName(displayName)
                    .also { validity -> isDisplayNameValid = validity.isValid },
                modifier = Modifier
                    .padding(top = 4.dp)
            )
        }

        // Password input field
        SecretTextInputField(
            value = password,
            onValueChange = { value -> password = value },
            label = { Text(text = "Password") },
            imeAction = when (action) {
                FormAction.SIGN_IN -> ImeAction.Done
                FormAction.SIGN_UP -> ImeAction.Next
            },
            validity = InputValidation.checkPassword(password)
                .also { validity -> isPasswordValid = validity.isValid },
            modifier = Modifier
                .padding(top = 4.dp)
        )

        // Password Again input field
        AnimatedVisibility(
            visible = when (action) {
                FormAction.SIGN_IN -> false
                FormAction.SIGN_UP -> true
            }
        ) {
            SecretTextInputField(
                value = passwordAgain,
                onValueChange = { value -> passwordAgain = value },
                label = { Text(text = "Password... again") },
                imeAction = ImeAction.Done,
                validity = InputValidation.checkPasswordMatch(password, passwordAgain)
                    .also { validity -> isPasswordAgainValid = validity.isValid },
                modifier = Modifier
                    .padding(top = 4.dp)
            )
        }

        // Action Button
        Button(
            onClick = {
                composableScope.launch {
                    when (action) {
                        FormAction.SIGN_IN -> onSignIn(email, password)
                        FormAction.SIGN_UP -> onSignUp(email, displayName, password)
                    }
                }
            },
            enabled = when (action) {
                FormAction.SIGN_IN -> isEmailValid && isPasswordValid
                FormAction.SIGN_UP -> isEmailValid && isDisplayNameValid && isPasswordValid && isPasswordAgainValid
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .defaultMinSize(minWidth = TextFieldDefaults.MinWidth)
        ) {
            Text(
                text = when (action) {
                    FormAction.SIGN_IN -> "Log In"
                    FormAction.SIGN_UP -> "Create Account"
                }
            )
        }

        // Switch Action Button
        TextButton(
            onClick = {
                passwordAgain = ""
                action = action.next()
            },
            modifier = Modifier
                .defaultMinSize(minWidth = TextFieldDefaults.MinWidth)
        ) {
            Text(
                text = when (action) {
                    FormAction.SIGN_IN -> "Create Account"
                    FormAction.SIGN_UP -> "Log In"
                },
                style = MaterialTheme.typography.labelMedium
            )
        }

    }// End Column

}// End AuthForm

@Preview
@Composable
fun AuthScreenPreview() {

    PreviewSurface {
        AuthScreenContent(
            onSignUp = { _, _, _ -> },
            onSignIn = { _, _ -> }
        )
    }

}
