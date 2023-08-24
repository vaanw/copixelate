package com.copixelate.ui.screens.contacts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.copixelate.ui.common.ValidatedTextInputField
import com.copixelate.ui.theme.disableIf
import com.copixelate.ui.util.InputValidation
import com.copixelate.ui.util.PreviewSurface
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun AddContactsScreen(navController: NavHostController) {

    fun randomSegment() =
        abs(Random(System.nanoTime()).nextInt())
            .toString()
            .take(n = 4)

    fun randomCode() = "${randomSegment()}-${randomSegment()}-${randomSegment()}"

    var isActive by remember { mutableStateOf(false) }
    var contactCode by remember { mutableStateOf(randomCode()) }

    AddContactsScreenContent(
        contactCode = contactCode,
        onClickBack = { navController.navigateUp() },
        isActiveContactCode = isActive,
        onActivateContactCode = { isActive = true },
        onDeactivateContactCode = { isActive = false },
        onRefreshContactCode = { contactCode = randomCode() },
    )
}

@Composable
private fun AddContactsScreenContent(
    contactCode: String,
    onClickBack: () -> Unit = {},
    isActiveContactCode: Boolean = false,
    onActivateContactCode: () -> Unit = {},
    onDeactivateContactCode: () -> Unit = {},
    onRefreshContactCode: () -> Unit = {},
) {

    var isKeyboardOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBar(onClickBack) }
    ) { contentPadding ->
        Surface(Modifier.padding(contentPadding)) {


            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 16.dp)
            ) {

                AnimatedVisibility(visible = !isKeyboardOpen) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {


                        ShareCodeTool(
                            contactCode = contactCode,
                            isActive = isActiveContactCode,
                            onActivate = onActivateContactCode,
                            onDeactivate = onDeactivateContactCode,
                            onRenew = onRefreshContactCode
                            // modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = "Or",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                        )
                    }

                } // End AnimatedVisibility


                EnterCodeTool(onFocusChanged = { isFocused ->
                    isKeyboardOpen = isFocused
                })

            }


        }
    }

}

@Composable
private fun ShareCodeTool(
    contactCode: String,
    isActive: Boolean,
    onActivate: () -> Unit,
    onDeactivate: () -> Unit,
    onRenew: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Share Code label
            Text(
                text = "Share Code",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(all = 16.dp)
            )

            Text(
                text = when (isActive) {
                    true -> contactCode
                    false -> "0000-0000-0000"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface.disableIf(!isActive),
                fontFamily = FontFamily.Monospace,
//                    modifier = Modifier
//                    .padding(vertical = 8.dp)
            )


            // Icon button row
            AnimatedVisibility(visible = isActive) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp)
                ) {

                    // "More" menu
                    ShareCodeToolMenu(
                        onDeactivate = onDeactivate,
                        onRenew = onRenew
                    )

                    // Share icon button
                    IconButton(
                        onClick = { /* Handle click */ }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Localized description"
                        )
                    }
                    // Copy icon button
                    IconButton(
                        onClick = { /* Handle click */ }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Localized description"
                        )
                    }

                } // End Row
            } // End icon button row

            // Activate button
            AnimatedVisibility(visible = !isActive) {
                Button(
                    onClick = { onActivate() },
                    modifier = Modifier.padding(all = 8.dp)
                ) {
                    Text(text = "Activate")
                }
            } // End activate button

        } // End Column
    } // End OutlinedCard
} // End ShareCodeTool

@Composable
private fun ShareCodeToolMenu(
    onDeactivate: () -> Unit,
    onRenew: () -> Unit,
) {
    Box {

        var isMenuExpanded by remember { mutableStateOf(false) }

        IconButton(
            onClick = { isMenuExpanded = true }
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Localized description"
            )
        }

        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = { isMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Deactivate") },
                onClick = {
                    isMenuExpanded = false
                    onDeactivate()
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null
                    )
                })
            DropdownMenuItem(
                text = { Text("Recreate") },
                onClick = { onRenew() },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = null
                    )
                })
        } // End DropdownMenu

    } // End Box
} // End ShareCodeToolMenu

@Composable
private fun EnterCodeTool(
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    class ContactCodeVisualTransformation : VisualTransformation {

        override fun filter(text: AnnotatedString): TransformedText {
            return TransformedText(
                text = AnnotatedString(text.text.formatAsContactCode()),
                offsetMapping = offsetMapping
            )
        }

//        private fun String.formatAsContactCode(): String = StringBuilder(
//            // Remove all except digits
////            replace(Regex("\\D"), "")
//            replace(Regex(" "), " ")
//        ).apply {
//            if (length > 8) insert(8, "-")
//            if (length > 4) insert(4, "-")
//        }.toString()

        private fun String.formatAsContactCode(): String =
            StringBuilder(this)
                .apply {
                    if (length > 8) insert(8, "-")
                    if (length > 4) insert(4, "-")
                }.toString()

        private val offsetMapping = object : OffsetMapping {

            // Has to also account for removed characters
            override fun originalToTransformed(offset: Int): Int = when {
                offset <= 4 -> offset
                offset <= 8 -> offset + 1
                else -> offset + 2
            }

            // Transform is never disabled, so this is never used
            override fun transformedToOriginal(offset: Int): Int = offset
        }

    }


    OutlinedCard(modifier = modifier) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Enter Code label
            Text(
                text = "Enter Code",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(all = 16.dp)
            )

            var inputValue by remember { mutableStateOf("") }

//            var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = "")) }

            // Contact code text input
            ValidatedTextInputField(
                // label = { Text(text = "Contact Code") },
                onValueChange = { value ->
//                    if (value.length > inputValue.length + 1) {
////                        moveCursorToEnd()
//                    }
//                    inputValue = value.formatCode()

//                    val cursorIndex = when {
//                        value.length > textFieldValueState.text.length + 1 -> {
//
//                        }
//
//                    }

//                    val selectionOffset = textFieldValueState.selection.length - textFieldValueState.text.length
//                    val selectionIndex = value.length + selectionOffset

//                    textFieldValueState = TextFieldValue(
//                        text = value.formatCode(),
////                        selection = TextRange(index = selectionIndex)
//                    )
                    inputValue = value.replace(Regex("\\D"), "")
                },
                visualTransformation = ContactCodeVisualTransformation(),
                value = inputValue,
                validity = InputValidation.checkContactCode(inputValue),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
                modifier = Modifier
                    .onFocusEvent { focus ->
                        onFocusChanged(focus.isFocused)
                    }
            )

            // Send request Button
            Button(
                onClick = { /* Do something */ },
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(text = "Send Request")
            }


        } // End Column
    } // End OutlinedCard
} // End EnterCodeTool

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onClickBack: () -> Unit
) {
    TopAppBar(
        title = { Text("Add Contacts") },
        navigationIcon = {
            IconButton(
                onClick = onClickBack
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        }
    )
}


@Preview
@Composable
private fun AddContactsScreenPreview() {

    var isActive by remember { mutableStateOf(false) }

    PreviewSurface {
        AddContactsScreenContent(
            contactCode = "3487-3896-5634",
            isActiveContactCode = isActive,
            onActivateContactCode = { isActive = true }
        )
    }

}

@Preview
@Composable
private fun ShareCodeToolPreview() {
    PreviewSurface {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            ShareCodeTool(
                contactCode = "3487-3896-5634",
                isActive = false,
                onActivate = {},
                onDeactivate = {},
                onRenew = {},
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ShareCodeTool(
                contactCode = "3487-3896-5634",
                isActive = true,
                onActivate = {},
                onDeactivate = {},
                onRenew = {}
            )
        }
    }
}
