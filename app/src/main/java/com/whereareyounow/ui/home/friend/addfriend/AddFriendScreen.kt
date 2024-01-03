package com.whereareyounow.ui.home.friend.addfriend

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import com.whereareyounow.R
import com.whereareyounow.data.GlobalValue
import com.whereareyounow.domain.entity.schedule.Friend
import com.whereareyounow.ui.theme.WhereAreYouTheme

@Composable
fun AddFriendScreen(
    moveToBackScreen: () -> Unit,
    viewModel: AddFriendViewModel = hiltViewModel()
) {
    val inputId = viewModel.inputId.collectAsState().value
    val friendInfo = viewModel.friendInfo.collectAsState().value
    val buttonState = viewModel.buttonState.collectAsState().value
    AddFriendScreen(
        inputId = inputId,
        updateInputId = viewModel::updateInputId,
        clearInputId = viewModel::clearInputId,
        friendInfo = friendInfo,
        buttonState = buttonState,
        searchFriend = viewModel::searchFriend,
        sendFriendRequest = viewModel::sendFriendRequest,
        moveToBackScreen = moveToBackScreen,
    )
}

@Composable
private fun AddFriendScreen(
    inputId: String,
    updateInputId: (String) -> Unit,
    clearInputId: () -> Unit,
    friendInfo: Friend?,
    buttonState: AddFriendViewModel.ButtonState,
    searchFriend: () -> Unit,
    sendFriendRequest: () -> Unit,
    moveToBackScreen: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddFriendScreenTopBar(moveToBackScreen)
        Spacer(Modifier.height(20.dp))
        FriendIdTextField(
            inputId = inputId,
            updateInputId = updateInputId,
            clearInputId = clearInputId
        )
        Spacer(Modifier.height(20.dp))
        if (friendInfo != null) {
            UserInfoContent(
                imageUrl = friendInfo.profileImgUrl,
                userName = friendInfo.name
            )
        }
        BottomButton(
            searchFriend = when (buttonState) {
                    AddFriendViewModel.ButtonState.SEARCH -> searchFriend
                    AddFriendViewModel.ButtonState.REQUEST -> sendFriendRequest
                },
            text = when (buttonState) {
                AddFriendViewModel.ButtonState.SEARCH -> "검색"
                AddFriendViewModel.ButtonState.REQUEST -> "친구추가"
            }
        )
    }
}

@Composable
fun AddFriendScreenTopBar(
    moveToBackScreen: () -> Unit
) {
    val density = LocalDensity.current.density
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((GlobalValue.topBarHeight / density).dp)
            .padding(start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            modifier = Modifier
                .size((GlobalValue.topBarHeight / density / 3 * 2).dp)
                .clip(RoundedCornerShape(50))
                .clickable { moveToBackScreen() },
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = null
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "친구추가",
                fontSize = 30.sp
            )
        }
    }
}

@Composable
fun FriendIdTextField(
    inputId: String,
    updateInputId: (String) -> Unit,
    clearInputId: () -> Unit
) {
    BasicTextField(
        value = inputId,
        onValueChange = { updateInputId(it) },
        textStyle = TextStyle(fontSize = 30.sp),
        singleLine = true,
        decorationBox = {
            Box(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .fillMaxWidth()
                    .drawBehind {
                        val borderSize = 1.dp.toPx()
                        drawLine(
                            color = Color(0xFF858585),
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = borderSize
                        )
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                it()
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Image(
                        modifier = Modifier.clickable {
                            clearInputId()
                        },
                        painter = painterResource(id = R.drawable.baseline_cancel_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = Color(0xFF858585))
                    )
                }
            }
        }
    )
}

@Composable
fun UserInfoContent(
    imageUrl: String?,
    userName: String
) {
    GlideImage(
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(50)),
        imageModel = { imageUrl ?: R.drawable.account_circle_fill0_wght200_grad0_opsz24 },
        imageOptions = ImageOptions(contentScale = ContentScale.Crop)
    )
    Spacer(Modifier.height(20.dp))
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = userName,
            fontSize = 30.sp
        )
    }
}

@Composable
fun BottomButton(
    searchFriend: () -> Unit,
    text: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    color = Color(0xFF2D2573),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { searchFriend() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddFriendScreenPreview1() {
    WhereAreYouTheme {
        AddFriendScreen(
            inputId = "",
            updateInputId = {  },
            clearInputId = {  },
            friendInfo = null,
            buttonState = AddFriendViewModel.ButtonState.REQUEST,
            searchFriend = {  },
            sendFriendRequest = {  },
            moveToBackScreen = {  }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun AddFriendScreenPreview2() {
    WhereAreYouTheme {
        AddFriendScreen(
            inputId = "",
            updateInputId = {  },
            clearInputId = {  },
            friendInfo = null,
            buttonState = AddFriendViewModel.ButtonState.REQUEST,
            searchFriend = {  },
            sendFriendRequest = {  },
            moveToBackScreen = {  }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun AddFriendScreenPreview3() {
    WhereAreYouTheme {
        AddFriendScreen(
            inputId = "",
            updateInputId = {  },
            clearInputId = {  },
            friendInfo = null,
            buttonState = AddFriendViewModel.ButtonState.REQUEST,
            searchFriend = {  },
            sendFriendRequest = {  },
            moveToBackScreen = {  }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun AddFriendScreenPreview4() {
    WhereAreYouTheme {
        AddFriendScreen(
            inputId = "",
            updateInputId = {  },
            clearInputId = {  },
            friendInfo = null,
            buttonState = AddFriendViewModel.ButtonState.REQUEST,
            searchFriend = {  },
            sendFriendRequest = {  },
            moveToBackScreen = {  }
        )
    }
}