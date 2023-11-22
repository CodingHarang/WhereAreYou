package com.whereareyou.ui.home.friends.addfriend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddFriendScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AddFriendScreenTopBar()
        FriendIdTextField()
    }
}