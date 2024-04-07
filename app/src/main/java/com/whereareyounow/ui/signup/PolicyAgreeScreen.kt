package com.whereareyounow.ui.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whereareyounow.R
import com.whereareyounow.ui.component.CustomTopBar
import com.whereareyounow.ui.component.RoundedCornerButton
import com.whereareyounow.ui.signup.common.DoubleCircle
import com.whereareyounow.ui.signup.common.GrayCircle

@Composable
fun PolicyAgreeScreen(
    moveToBackScreen: () -> Unit,
    moveToSignUpScreen: () -> Unit,
    moveToTermsOfServiceDetailsScreen: () -> Unit,
    moveToPrivacyPolicyDetailsScreen: () -> Unit,
) {
    val isTermsOfServiceAgreed = rememberSaveable { mutableStateOf(false) }
    val isPrivacyPolicyAgreed = rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxSize()
    ) {
        // 상단바
        TermsOfServiceScreenTopBar(moveToBackScreen)

        Spacer(Modifier.height(20.dp))

        TopProgressContent()

        Spacer(Modifier.height(80.dp))

        InstructionContent()

        Spacer(Modifier.height(100.dp))

        AgreementSelectionContent(
            isTermsOfServiceAgreed = isTermsOfServiceAgreed,
            isPrivacyPolicyAgreed = isPrivacyPolicyAgreed,
            moveToTermsOfServiceDetailsScreen = moveToTermsOfServiceDetailsScreen,
            moveToPrivacyPolicyDetailsScreen = moveToPrivacyPolicyDetailsScreen
        )

        Spacer(Modifier.weight(1f))

        AgreeAndSignUpButton(
            isTermsOfServiceAgreed = isTermsOfServiceAgreed,
            isPrivacyPolicyAgreed = isPrivacyPolicyAgreed,
            moveToSignUpScreen = moveToSignUpScreen
        )

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun TermsOfServiceScreenTopBar(
    moveToBackScreen: () -> Unit,
) {
    CustomTopBar(
        title = "",
        onBackButtonClicked = moveToBackScreen
    )
}

private const val TOP_PROGRESS_BAR = "TOP_PROGRESS_BAR"
private const val TEXT_1 = "TEXT_1"
private const val TEXT_2 = "TEXT_2"
private const val TEXT_3 = "TEXT_3"

@Composable
private fun TopProgressContent() {
    Layout(
        content = {
            TopProgressBar(Modifier.layoutId(TOP_PROGRESS_BAR))
            Text(
                modifier = Modifier.layoutId(TEXT_1),
                text = "약관동의",
                fontSize = 16.sp,
                color = Color(0xFF5F5F5F)
            )
            Text(
                modifier = Modifier.layoutId(TEXT_2),
                text = "정보입력",
                fontSize = 16.sp,
                color = Color(0xFF959595)
            )
            Text(
                modifier = Modifier.layoutId(TEXT_3),
                text = "가입완료",
                fontSize = 16.sp,
                color = Color(0xFF959595)
            )
        },
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
    ) { measurables, constraint ->
        val text1 = measurables.first { it.layoutId == TEXT_1 }.measure(constraint)
        val text2 = measurables.first { it.layoutId == TEXT_2 }.measure(constraint)
        val text3 = measurables.first { it.layoutId == TEXT_3 }.measure(constraint)
        val topProgressBar = measurables.first { it.layoutId == TOP_PROGRESS_BAR }.measure(
            Constraints(
                minWidth = 0,
                minHeight = 0,
                maxWidth = constraint.maxWidth - text1.width + 20.dp.toPx().toInt(),
                maxHeight = constraint.maxHeight
            )
        )
        val space = 10.dp
        val height = topProgressBar.height + space.toPx() + text1.height

        layout(constraint.maxWidth, height.toInt()) {
            topProgressBar.place((constraint.maxWidth - topProgressBar.width) / 2, 0)
            text1.place(0, topProgressBar.height + space.toPx().toInt())
            text2.place((constraint.maxWidth - text2.width) / 2, topProgressBar.height + space.toPx().toInt())
            text3.place(constraint.maxWidth - text3.width, topProgressBar.height + space.toPx().toInt())
        }
    }
}

private const val DOUBLE_CIRCLE = "DOUBLE_CIRCLE"
private const val GRAY_CIRCLE_1 = "GRAY_CIRCLE_1"
private const val GRAY_CIRCLE_2 = "GRAY_CIRCLE_2"

@Composable
private fun TopProgressBar(modifier: Modifier) {
    val progressBarHeight = 20
    Layout(
        content = {
            DoubleCircle(
                Modifier
                    .layoutId(DOUBLE_CIRCLE)
                    .size(progressBarHeight.dp)
            )
            GrayCircle(
                Modifier
                    .layoutId(GRAY_CIRCLE_1)
                    .size(progressBarHeight.dp)
            )
            GrayCircle(
                Modifier
                    .layoutId(GRAY_CIRCLE_2)
                    .size(progressBarHeight.dp)
            )
        },
        modifier = modifier
            .drawBehind {
                drawRect(
                    color = Color(0xFFBBBAB8),
                    topLeft = Offset((progressBarHeight / 2).dp.toPx(), ((progressBarHeight - 2) / 2).dp.toPx()),
                    size = Size(size.width - progressBarHeight.dp.toPx(), 2.dp.toPx())
                )
            }
    ) { measurables, constraint ->
        val doubleCircle = measurables.first { it.layoutId == DOUBLE_CIRCLE }.measure(constraint)
        val grayCircle1 = measurables.first { it.layoutId == GRAY_CIRCLE_1 }.measure(constraint)
        val grayCircle2 = measurables.first { it.layoutId == GRAY_CIRCLE_2 }.measure(constraint)
        val height = listOf(doubleCircle.height, grayCircle1.height, grayCircle2.height).min()

        layout(constraint.maxWidth, height) {
            doubleCircle.place(0, 0)
            grayCircle1.place((constraint.maxWidth - grayCircle1.width) / 2, 0)
            grayCircle2.place(constraint.maxWidth - grayCircle2.width, 0)
        }
    }
}

@Composable
private fun InstructionContent() {
    Text(
        text = "회원가입에 필요한\n약관에 동의해주세요",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        style = TextStyle(
            lineHeight = 34.sp
        ),
        color = Color(0xFF373737)
    )
}

@Composable
private fun AgreementSelectionContent(
    isTermsOfServiceAgreed: MutableState<Boolean>,
    isPrivacyPolicyAgreed: MutableState<Boolean>,
    moveToTermsOfServiceDetailsScreen: () -> Unit,
    moveToPrivacyPolicyDetailsScreen: () -> Unit
) {
    Column {
        // 모두 동의하기
        AllAgreementSelectionContent(
            isTermsOfServiceAgreed = isTermsOfServiceAgreed,
            isPrivacyPolicyAgreed = isPrivacyPolicyAgreed
        )

        Spacer(Modifier.height(40.dp))

        // 서비스 이용약관
        TermsOfServiceAgreementSelectionContent(
            isTermsOfServiceAgreed = isTermsOfServiceAgreed,
            moveToTermsOfServiceDetailsScreen = moveToTermsOfServiceDetailsScreen
        )

        Spacer(Modifier.height(20.dp))

        // 개인정보 처리방침
        PrivacyPolicyAgreementSelectionContent(
            isPrivacyPolicyAgreed = isPrivacyPolicyAgreed,
            moveToPrivacyPolicyDetailsScreen = moveToPrivacyPolicyDetailsScreen
        )
    }
}

@Composable
private fun AllAgreementSelectionContent(
    isTermsOfServiceAgreed: MutableState<Boolean>,
    isPrivacyPolicyAgreed: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!(isTermsOfServiceAgreed.value && isPrivacyPolicyAgreed.value)) {
                    isTermsOfServiceAgreed.value = true
                    isPrivacyPolicyAgreed.value = true
                } else {
                    isTermsOfServiceAgreed.value = false
                    isPrivacyPolicyAgreed.value = false
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contentColor = when (isTermsOfServiceAgreed.value && isPrivacyPolicyAgreed.value) {
            true -> Color(0xFF554F8D)
            false -> Color(0xFFC1C3CA)
        }

        Image(
            painter = painterResource(R.drawable.check_box2),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = "모두 동의하기",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
private fun TermsOfServiceAgreementSelectionContent(
    isTermsOfServiceAgreed: MutableState<Boolean>,
    moveToTermsOfServiceDetailsScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { isTermsOfServiceAgreed.value = !isTermsOfServiceAgreed.value },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contentColor = when (isTermsOfServiceAgreed.value) {
            true -> Color(0xFF554F8D)
            false -> Color(0xFFC1C3CA)
        }
        Image(
            painter = painterResource(R.drawable.check),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = "서비스 이용약관 (필수)",
            fontSize = 18.sp,
            color = Color(0xFF4C4545)
        )

        Spacer(Modifier.weight(1f))

        Text(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { moveToTermsOfServiceDetailsScreen() },
            text = "보기",
            fontSize = 18.sp,
            color = Color(0xFF6E6E6E)
        )
    }
}

@Composable
private fun PrivacyPolicyAgreementSelectionContent(
    isPrivacyPolicyAgreed: MutableState<Boolean>,
    moveToPrivacyPolicyDetailsScreen: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { isPrivacyPolicyAgreed.value = !isPrivacyPolicyAgreed.value },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contentColor = when (isPrivacyPolicyAgreed.value) {
            true -> Color(0xFF554F8D)
            false -> Color(0xFFC1C3CA)
        }

        Image(
            painter = painterResource(R.drawable.check),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor)
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = "개인정보 처리방침 (필수)",
            fontSize = 18.sp,
            color = Color(0xFF4C4545)
        )

        Spacer(Modifier.weight(1f))

        Text(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { moveToPrivacyPolicyDetailsScreen() },
            text = "보기",
            fontSize = 18.sp,
            color = Color(0xFF6E6E6E)
        )
    }
}

@Composable
fun AgreeAndSignUpButton(
    isTermsOfServiceAgreed: MutableState<Boolean>,
    isPrivacyPolicyAgreed: MutableState<Boolean>,
    moveToSignUpScreen: () -> Unit
) {
    val context = LocalContext.current
    RoundedCornerButton(
        text = "동의하고 가입하기",
        onClick = {
            if (isTermsOfServiceAgreed.value && isPrivacyPolicyAgreed.value) {
                moveToSignUpScreen()
            } else {
                Toast
                    .makeText(context, "약관에 모두 동의해주세요.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PolicyAgreeScreenPreview() {
    PolicyAgreeScreen(
        moveToBackScreen = {},
        moveToSignUpScreen = {},
        moveToTermsOfServiceDetailsScreen = {},
        moveToPrivacyPolicyDetailsScreen = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TopProgressContentPreview() {
    TopProgressContent()
}

@Preview(showBackground = true)
@Composable
private fun AgreementSelectionContentPreview_AllTrue() {
    val trueState = rememberSaveable { mutableStateOf(true) }
    AgreementSelectionContent(
        isTermsOfServiceAgreed = trueState,
        isPrivacyPolicyAgreed = trueState,
        moveToTermsOfServiceDetailsScreen = {},
        moveToPrivacyPolicyDetailsScreen = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AgreementSelectionContentPreview_AllFalse() {
    val falseState = rememberSaveable { mutableStateOf(false) }
    AgreementSelectionContent(
        isTermsOfServiceAgreed = falseState,
        isPrivacyPolicyAgreed = falseState,
        moveToTermsOfServiceDetailsScreen = {},
        moveToPrivacyPolicyDetailsScreen = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AgreementSelectionContentPreview_TermsOfServiceTrue_PrivacyPolicyFalse() {
    val trueState = rememberSaveable { mutableStateOf(true) }
    val falseState = rememberSaveable { mutableStateOf(false) }
    AgreementSelectionContent(
        isTermsOfServiceAgreed = trueState,
        isPrivacyPolicyAgreed = falseState,
        moveToTermsOfServiceDetailsScreen = {},
        moveToPrivacyPolicyDetailsScreen = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun AgreementSelectionContentPreview_TermsOfServiceFalse_PrivacyPolicyTrue() {
    val trueState = rememberSaveable { mutableStateOf(true) }
    val falseState = rememberSaveable { mutableStateOf(false) }
    AgreementSelectionContent(
        isTermsOfServiceAgreed = falseState,
        isPrivacyPolicyAgreed = trueState,
        moveToTermsOfServiceDetailsScreen = {},
        moveToPrivacyPolicyDetailsScreen = {}
    )
}