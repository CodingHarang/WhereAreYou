package com.whereareyou.ui.home.friends.addfriend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddFriendScreen(
    viewModel: AddFriendViewModel = hiltViewModel()
) {
    val inputId = viewModel.inputId.collectAsState().value
    val imageUrl = viewModel.imageUrl.collectAsState().value
    val userName = viewModel.userName.collectAsState().value
    val buttonState = viewModel.buttonState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AddFriendScreenTopBar()
        Spacer(modifier = Modifier.height(20.dp))
        FriendIdTextField(
            inputId = inputId,
            updateInputId = { viewModel.updateInputId(it) },
            clearInputId = { viewModel.clearInputId() }
        )
        Spacer(modifier = Modifier.height(20.dp))
        UserInfoContent(
            imageUrl = imageUrl,
            userName = userName
        )
        BottomButton(
            searchFriend = {
                when (buttonState) {
                    AddFriendViewModel.ButtonState.SEARCH -> viewModel.searchFriend()
                    AddFriendViewModel.ButtonState.REQUEST -> viewModel.sendFriendRequest()
                }
            },
            text = when (buttonState) {
                AddFriendViewModel.ButtonState.SEARCH -> "검색"
                AddFriendViewModel.ButtonState.REQUEST -> "친구추가"
            }
        )
    }
}