package com.copixelate.ui.screens.friend

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
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
import com.copixelate.ui.common.AddItemFab
import com.copixelate.ui.nav.NavInfo
import com.copixelate.ui.nav.navigate
import com.copixelate.ui.util.PreviewSurface
import com.copixelate.ui.util.ScreenSurface
import com.copixelate.ui.util.toDp

@Composable
fun FriendScreen(navController: NavHostController) {

    ScreenSurface {
        FriendScreenContent(
            friends = emptyList(),
            onAddFriend = {
                navController.navigate(navInfo = NavInfo.Friend.Add)
            }
        )
    }

}

data class FauxFriendModel(
    val name: String
)

@Composable
fun FriendScreenContent(
    friends: List<FauxFriendModel>,
    onAddFriend: () -> Unit
) {

    var fabHeight by remember { mutableStateOf(0) }
    val fabClearance = fabHeight.toDp() + 16.dp + 16.dp

    Scaffold(
        floatingActionButton = {
            AddItemFab(
                onClick = { onAddFriend() },
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
                        text = "Friends",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                    )
                }

                // Friends list
                itemsIndexed(items = friends) { index, friendModel ->
                    FriendListItem(
                        friendModel = friendModel,
                        isLast = friends.lastIndex == index
                    )
                }

            } // End Lazy Column
        } // End Surface

    }

}

@Composable
fun FriendListItem(
    friendModel: FauxFriendModel,
    isLast: Boolean,
) {
    ListItem(
        headlineContent = {
            Text(
                text = friendModel.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 0.dp)
            )
        },
        modifier = Modifier.padding(start = 8.dp)
    )
    if (!isLast) HorizontalDivider()
}


@Preview
@Composable
fun FriendScreenPreview() {

    PreviewSurface {
        FriendScreenContent(
            friends = List<FauxFriendModel>(20) { index ->
                FauxFriendModel(name = "Preview Friend $index")
            },
            onAddFriend = {}
        )
    }

}
