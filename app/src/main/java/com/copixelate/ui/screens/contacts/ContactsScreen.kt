package com.copixelate.ui.screens.contacts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.screens.AddItemFab
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.toDp

@Composable
fun ContactsScreen(navController: NavHostController) {

    ContactsScreenContent(
        contacts = emptyList(),
        onAddContact = {
            navController.navigate(route = NavInfo.Contacts.Add.route)
        }
    )

}

data class FauxContactModel(
    val name: String
)

@Composable
fun ContactsScreenContent(
    contacts: List<FauxContactModel>,
    onAddContact: () -> Unit
) {

    var fabHeight by remember { mutableStateOf(0) }
    val fabClearance = fabHeight.toDp() + 16.dp + 16.dp

    Scaffold(
        floatingActionButton = {
            AddItemFab(
                onClick = { onAddContact() },
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        fabHeight = coordinates.size.height
                    }
            )
        }

    ) { contentPadding ->
        Surface(Modifier.padding(contentPadding)) {
            // Library items
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = fabClearance,
//            top = 16.dp, start = 16.dp, end = 16.dp
                ),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Header text
                item {
                    Text(
                        text = "Contacts",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                    )
                }

                // Contacts list
                itemsIndexed(items = contacts) { index, contactModel ->
                    ContactsListItem(
                        contactModel = contactModel,
                        isLast = contacts.lastIndex == index
                    )
                }

            } // End Lazy Column
        } // End Surface

    }

}

@Composable
fun ContactsListItem(
    contactModel: FauxContactModel,
    isLast: Boolean,
) {
    ListItem(
        headlineContent = {
            Text(
                text = contactModel.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 0.dp)
            )
        },
        modifier = Modifier.padding(start = 8.dp)
    )
    if (!isLast) Divider()
}


@Preview
@Composable
fun ContactsScreenPreview() {

    PreviewSurface {
        ContactsScreenContent(
            contacts = List<FauxContactModel>(20) { index ->
                FauxContactModel(name = "Preview Contact $index")
            },
            onAddContact = {}
        )
    }

}
