package com.whereareyounow.ui.signup


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.whereareyounow.R
import com.whereareyounow.ui.signin.SignViewModel

fun isValidUserId(input: String): Boolean {
    val regex = Regex("^[a-z][a-z0-9]{4,11}$")
    return regex.matches(input)
}

fun checkPasswordConditions(password: String): Boolean {
    val lengthCondition = password.length in 6..20
    val lowercaseRegex = Regex(".*[a-z].*")
    val uppercaseRegex = Regex(".*[A-Z].*")
    val digitRegex = Regex(".*\\d.*")
    val specialCharRegex = Regex("[!@#\$%^&*()\\-_=+\\\\|\\[{\\]};:'\",<.>/?].*")

    val containsLowercase = lowercaseRegex.matches(password)
    val containsUppercase = uppercaseRegex.matches(password)
    val containsDigit = digitRegex.matches(password)
    val containsSpecialChar = specialCharRegex.matches(password)

    val conditionsMet = listOf(
        containsLowercase,
        containsUppercase,
        containsDigit,
        containsSpecialChar
    ).count { it } >= 2

    return lengthCondition && conditionsMet
}

@Composable
fun SignUpScreen(
    moveToBackScreen: () -> Unit,
    signInViewModel: SignViewModel = hiltViewModel(),
    viewModel: SignUpViewModel = hiltViewModel()
) {

    var user_id by remember { mutableStateOf(TextFieldValue()) }
    var user_name by remember { mutableStateOf(TextFieldValue()) }
    var check_password by remember { mutableStateOf(TextFieldValue()) }

    var password by remember { mutableStateOf(TextFieldValue()) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue()) }
    var isPasswordValid by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf(TextFieldValue()) }
    var emailCode by remember { mutableStateOf(TextFieldValue()) }
    var isEmailChecked by remember { mutableStateOf(false) }

    var isInvalidId by remember { mutableStateOf(false) }
    var isInvalidPassword by remember { mutableStateOf(false) }
    var isIdDuplicate by remember { mutableStateOf(false) }

    // 버튼 클릭 가능 여부를 저장할 변수
    var isButtonEnabled by remember { mutableStateOf(false) }
    var isButtonClicked by remember { mutableStateOf(false) }



    var EmailCodeText by remember { mutableStateOf(false) }


    // 값이 입력되고 검증이 되었을경우 true
    var name_pass by remember { mutableStateOf(false) }
    var id_pass by remember { mutableStateOf(false) }
    var password_pass by remember { mutableStateOf(false) }
    var email_pass by remember { mutableStateOf(true) } //임시로 true 설정

    val context = LocalContext.current
    val density = LocalDensity.current.density
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxSize()
            .imePadding(),
        state = listState
    ) {
        item {
            SignUpScreenTopBar(moveToBackScreen = moveToBackScreen)
            Spacer(modifier = Modifier.height(20.dp))

            // 닉네임 입력
            val inputUserName = viewModel.inputUserName.collectAsState().value
            val inputUserNameState = viewModel.inputUserNameState.collectAsState().value
            Title(text = "닉네임")
            InputBox(
                hint = "닉네임",
                inputText = inputUserName,
                onValueChange = viewModel::updateInputUserName,
                conditionState = inputUserNameState,
                isPassword = false,
                guideline = "닉네임은 4~10자의 한글, 영문 대/소문자 조합으로 입력해주세요."
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 아이디 입력
            val inputUserId = viewModel.inputUserId.collectAsState().value
            val inputUserIdState = viewModel.inputUserIdState.collectAsState().value
            Title(text = "아이디")
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
            ) {
                // 아이디 입력창
                Box(modifier = Modifier.weight(1f)) {
                    InputBox(
                        hint = "아이디",
                        inputText = inputUserId,
                        onValueChange = viewModel::updateInputUserId,
                        conditionState = inputUserIdState,
                        isPassword = false,
                        guideline = "아이디는 영문 소문자로 시작하는 4~10자의 영문 소문자, 숫자 조합으로 입력해주세요."
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                // 중복확인 버튼
                CheckingButton(text = "중복확인") {

                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            // 비밀번호 입력
            val inputPassword = viewModel.inputPassword.collectAsState().value
            val inputPasswordState = viewModel.inputPasswordState.collectAsState().value
            Title(text = "비밀번호")
            // 비밀번호 입력창
            InputBox(
                hint = "비밀번호",
                inputText = inputPassword,
                onValueChange = viewModel::updateInputPassword,
                conditionState = inputPasswordState,
                isPassword = false,
                guideline = "비밀번호는 영문 대/소문자로 시작하는 4~10자의 영문 대/소문자, 숫자 조합으로 입력해주세요." +
                        "\n* 영문 대문자, 소문자, 숫자를 최소 하나 이상씩 포함해야합니다.",
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 비밀번호 확인
            val inputPasswordForChecking = viewModel.inputPasswordForChecking.collectAsState().value
            val inputPasswordForCheckingState =
                viewModel.inputPasswordForCheckingState.collectAsState().value
            Title(text = "비밀번호 확인")
            Spacer(modifier = Modifier.height(10.dp))
            InputBox(
                hint = "비밀번호 확인",
                inputText = inputPasswordForChecking,
                onValueChange = viewModel::updateInputPasswordForChecking,
                conditionState = inputPasswordForCheckingState,
                isPassword = false,
                guideline = "비밀번호를 다시 한번 입력해주세요."
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 이메일 입력
            val inputEmail = viewModel.inputEmail.collectAsState().value
            val inputEmailState = viewModel.inputEmailState.collectAsState().value
            Title(text = "이메일")
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()
                ) {
                // 이메일 입력창
                Box(modifier = Modifier.weight(1f)) {
                    InputBox(
                        hint = "이메일",
                        inputText = inputEmail,
                        onValueChange = viewModel::updateInputEmail,
                        conditionState = inputEmailState,
                        isPassword = false,
                        guideline = "올바른 이메일 형식으로 입력해주세요"
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                // 중복확인, 인증 요청 버튼
                CheckingButton(text = "중복확인") {

                }
            }

            // 이메일 인증코드 확인
            val isVerificationInProgress = viewModel.isVerificationInProgress.collectAsState().value
            val inputVerificationCode = viewModel.inputVerificationCode.collectAsState().value
            val inputVerificationCodeState = viewModel.inputVerificationCodeState.collectAsState().value
            if (isVerificationInProgress) {
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                ) {
                    // 이메일 입력창
                    Box(modifier = Modifier.weight(1f)) {
                        InputBox(
                            hint = "이메일 인증코드",
                            inputText = inputVerificationCode,
                            onValueChange = viewModel::updateInputVerificationCode,
                            conditionState = inputVerificationCodeState,
                            isPassword = false,
                            guideline = ""
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    // 확인 버튼
                    CheckingButton(text = "확인") {

                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun Title(
    text: String
) {
    Text(
        text = text,
        fontSize = 20.sp
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun InputBox(
    hint: String,
    inputText: String,
    onValueChange: (String) -> Unit,
    conditionState: ConditionState,
    isPassword: Boolean,
    guideline: String
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = inputText,
        onValueChange = { onValueChange(it) },
        textStyle = TextStyle(fontSize = 20.sp),
        decorationBox = {
            Column {
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = when (conditionState) {
                                    ConditionState.EMPTY -> Color(0xFFE0E0E0)
                                    ConditionState.SATISFIED -> Color.Green
                                    ConditionState.UNSATISFIED -> Color.Red
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = Color(0xFFEEEEEE),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(start = 20.dp, end = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    it()
                    if (inputText == "") {
                        Text(
                            text = hint,
                            fontSize = 20.sp,
                            color = Color(0xFFBCBCBC)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (conditionState == ConditionState.SATISFIED) {
                            Image(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.check_circle_fill0_wght300_grad0_opsz24),
                                contentDescription = null
                            )
                        } else if (conditionState == ConditionState.UNSATISFIED) {
                            Image(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(id = R.drawable.cancel_fill0_wght300_grad0_opsz24),
                                contentDescription = null
                            )
                        }
                    }
                }
                Guideline(
                    text = guideline,
                    conditionState = conditionState
                )
            }
        },
        singleLine = true,
        visualTransformation = when (isPassword) {
            true -> PasswordVisualTransformation()
            false -> VisualTransformation.None
        }
    )
}

@Composable
fun Guideline(
    text: String,
    conditionState: ConditionState
) {
    if (conditionState != ConditionState.SATISFIED) {
        Spacer(Modifier.height(10.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = when (conditionState) {
                ConditionState.UNSATISFIED -> Color.Red
                else -> Color.Black
            }
        )
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun CheckingButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = Color(0xFFE9E9E9),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp),
            text = text,
            color = Color(0xFF737373),
            fontSize = 20.sp
        )
    }
}

enum class ConditionState {
    EMPTY, SATISFIED, UNSATISFIED
}

enum class IdDuplicateState {
    IDLE, DUPLICATED, UNIQUE
}