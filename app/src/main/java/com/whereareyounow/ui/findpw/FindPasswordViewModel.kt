package com.whereareyounow.ui.findpw

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.whereareyounow.data.findpw.EmailState
import com.whereareyounow.data.findpw.FindPasswordScreenSideEffect
import com.whereareyounow.data.findpw.FindPasswordScreenUIState
import com.whereareyounow.data.findpw.PasswordCheckingState
import com.whereareyounow.data.findpw.PasswordResettingScreenSideEffect
import com.whereareyounow.data.findpw.PasswordResettingScreenUIState
import com.whereareyounow.data.findpw.PasswordState
import com.whereareyounow.data.findpw.ResultState
import com.whereareyounow.domain.entity.apimessage.signin.ResetPasswordRequest
import com.whereareyounow.domain.entity.apimessage.signin.VerifyPasswordResetCodeRequest
import com.whereareyounow.domain.entity.apimessage.signup.AuthenticateEmailRequest
import com.whereareyounow.domain.usecase.signin.ResetPasswordUseCase
import com.whereareyounow.domain.usecase.signin.VerifyPasswordResetCodeUseCase
import com.whereareyounow.domain.usecase.signup.AuthenticateEmailUseCase
import com.whereareyounow.domain.util.LogUtil
import com.whereareyounow.domain.util.NetworkResult
import com.whereareyounow.util.InputTextValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val application: Application,
    private val inputTextValidator: InputTextValidator,
    private val authenticateEmailUseCase: AuthenticateEmailUseCase,
    private val verifyPasswordResetCodeUseCase: VerifyPasswordResetCodeUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : AndroidViewModel(application) {

    private val _findPasswordScreenUIState = MutableStateFlow(FindPasswordScreenUIState())
    val findPasswordScreenUIState = _findPasswordScreenUIState.asStateFlow()
    private val _passwordResettingScreenUIState = MutableStateFlow(PasswordResettingScreenUIState())
    val passwordResettingScreenUIState = _passwordResettingScreenUIState.asStateFlow()
    val findPasswordScreenSideEffectFlow = MutableSharedFlow<FindPasswordScreenSideEffect>()
    val passwordResettingScreenSideEffectFlow = MutableSharedFlow<PasswordResettingScreenSideEffect>()
    private var startTimer: Job? = null

    fun updateInputUserId(id: String) {
        _findPasswordScreenUIState.update {
            it.copy(inputUserId = id)
        }
    }

    fun updateInputEmail(email: String) {
        _findPasswordScreenUIState.update {
            it.copy(
                inputEmail = email,
                inputEmailState = if (inputTextValidator.validateEmail(email).result) EmailState.SATISFIED else EmailState.UNSATISFIED
            )
        }
    }

    fun updateInputVerificationCode(code: String) {
        _findPasswordScreenUIState.update {
            it.copy(inputVerificationCode = code)
        }
    }

    fun updateInputPassword(password: String) {
        _passwordResettingScreenUIState.update {
            it.copy(
                inputPassword = password,
                inputPasswordState = if (inputTextValidator.validatePassword(password).result) PasswordState.SATISFIED else PasswordState.UNSATISFIED
            )
        }
    }

    fun updateInputPasswordForChecking(passwordForChecking: String) {
        _passwordResettingScreenUIState.update {
            it.copy(
                inputPasswordForChecking = passwordForChecking,
                passwordCheckingState = if (it.inputPassword == passwordForChecking) PasswordCheckingState.SATISFIED else PasswordCheckingState.UNSATISFIED
            )
        }
    }

    fun sendEmailVerificationCode() {
        viewModelScope.launch(Dispatchers.Default) {
            when (_findPasswordScreenUIState.value.inputEmailState) {
                EmailState.EMPTY -> { findPasswordScreenSideEffectFlow.emit(FindPasswordScreenSideEffect.Toast("이메일을 입력해주세요.")) }
                EmailState.UNSATISFIED -> { findPasswordScreenSideEffectFlow.emit(FindPasswordScreenSideEffect.Toast("이메일을 확인해주세요.")) }
                EmailState.SATISFIED -> {
                    if (_findPasswordScreenUIState.value.emailVerificationLeftTime > 120) {
                        findPasswordScreenSideEffectFlow.emit(FindPasswordScreenSideEffect.Toast("${_findPasswordScreenUIState.value.emailVerificationLeftTime - 120}초 후에 다시 발송할 수 있습니다."))
                        return@launch
                    }
                    startTimer?.cancel()
                    startTimer = launch {
                        _findPasswordScreenUIState.update {
                            it.copy(
                                isVerificationCodeSent = true,
                                emailVerificationLeftTime = 180
                            )
                        }
                        while (_findPasswordScreenUIState.value.emailVerificationLeftTime > 0) {
                            _findPasswordScreenUIState.update {
                                it.copy(emailVerificationLeftTime = it.emailVerificationLeftTime - 1)
                            }
                            delay(1000)
                        }
                    }
                    val request = AuthenticateEmailRequest(_findPasswordScreenUIState.value.inputEmail)
                    val response = authenticateEmailUseCase(request)
                    LogUtil.printNetworkLog(request, response, "이메일 인증")
                    when (response) {
                        is NetworkResult.Success -> { findPasswordScreenSideEffectFlow.emit(FindPasswordScreenSideEffect.Toast("인증 코드가 발송되었습니다.")) }
                        is NetworkResult.Error -> {}
                        is NetworkResult.Exception -> { findPasswordScreenSideEffectFlow.emit(FindPasswordScreenSideEffect.Toast("오류가 발생했습니다.")) }
                    }
                }
            }
        }
    }

    fun verifyPasswordResetCode(
        moveToPasswordResettingScreen: (String, ResultState) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            // 유효시간이 지나면 인증을 다시 받아야 한다.
            if (_findPasswordScreenUIState.value.emailVerificationLeftTime <= 0) {
                findPasswordScreenSideEffectFlow.emit(FindPasswordScreenSideEffect.Toast("유효시간이 만료되었습니다. 인증 코드를 재전송해주세요."))
                return@launch
            }
            val request = VerifyPasswordResetCodeRequest(
                userId = _findPasswordScreenUIState.value.inputUserId,
                email = _findPasswordScreenUIState.value.inputEmail,
                code = _findPasswordScreenUIState.value.inputVerificationCode)
            val response = verifyPasswordResetCodeUseCase(request)
            LogUtil.printNetworkLog(request, response, "비밀번호 재설정 코드 인증")
            when (response) {
                is NetworkResult.Success -> {
                    withContext(Dispatchers.Main) {
                        moveToPasswordResettingScreen(
                            _findPasswordScreenUIState.value.inputUserId,
                            ResultState.OK
                        )
                    }
                }
                is NetworkResult.Error -> {
                    when (response.code) {
                        400 -> {
                            withContext(Dispatchers.Main) {
                                moveToPasswordResettingScreen(
                                    "",
                                    ResultState.MEMBER_MISMATCH
                                )
                            }
                        }
                        404 -> {
                            withContext(Dispatchers.Main) {
                                moveToPasswordResettingScreen(
                                    "",
                                    ResultState.EMAIL_NOT_FOUND
                                )
                            }
                        }
                    }
                }
                is NetworkResult.Exception -> { findPasswordScreenSideEffectFlow.emit(FindPasswordScreenSideEffect.Toast("오류가 발생했습니다.")) }
            }
        }
    }

    fun resetPassword(
        userId: String,
        moveToSignInScreen: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            when (_passwordResettingScreenUIState.value.inputPasswordState) {
                PasswordState.EMPTY -> {
                    passwordResettingScreenSideEffectFlow.emit(PasswordResettingScreenSideEffect.Toast("비밀번호를 입력해주세요."))
                    return@launch
                }
                PasswordState.UNSATISFIED -> {
                    passwordResettingScreenSideEffectFlow.emit(PasswordResettingScreenSideEffect.Toast("비밀번호를 확인해주세요."))
                    return@launch
                }
                PasswordState.SATISFIED -> {}
            }
            when (_passwordResettingScreenUIState.value.passwordCheckingState) {
                PasswordCheckingState.EMPTY -> {
                    passwordResettingScreenSideEffectFlow.emit(PasswordResettingScreenSideEffect.Toast("비밀번호를 다시 한 번 입력해주세요."))
                    return@launch
                }
                PasswordCheckingState.UNSATISFIED -> {
                    passwordResettingScreenSideEffectFlow.emit(PasswordResettingScreenSideEffect.Toast("비밀번호가 일치하지 않습니다."))
                    return@launch
                }
                PasswordCheckingState.SATISFIED -> {
                    val request = ResetPasswordRequest(
                        userId = userId,
                        password = _passwordResettingScreenUIState.value.inputPassword,
                        checkPassword = _passwordResettingScreenUIState.value.inputPasswordForChecking)
                    val response = resetPasswordUseCase(request)
                    LogUtil.printNetworkLog(request, response, "비밀번호 재설정")
                    when (response) {
                        is NetworkResult.Success -> {
                            withContext(Dispatchers.Main) {
                                moveToSignInScreen()
                            }
                        }
                        is NetworkResult.Error -> {

                        }
                        is NetworkResult.Exception -> { passwordResettingScreenSideEffectFlow.emit(PasswordResettingScreenSideEffect.Toast("오류가 발생했습니다.")) }
                    }
                }
            }
        }
    }
}