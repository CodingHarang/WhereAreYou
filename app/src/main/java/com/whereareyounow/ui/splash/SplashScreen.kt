package com.whereareyounow.ui.splash

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.whereareyounow.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    moveToStartScreen: () -> Unit,
    moveToMainScreen: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val screenState = viewModel.screenState.collectAsState().value
    val checkingState = viewModel.checkingState.collectAsState().value
    val isNetworkConnectionErrorDialogShowing = viewModel.isNetworkConnectionErrorDialogShowing.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )
    LaunchedEffect(checkingState) {
        when (checkingState) {
            SplashViewModel.CheckingState.NETWORK -> {
                coroutineScope.launch {
                    delay(1000)
                    if (viewModel.checkNetworkState()) {
                        viewModel.updateCheckingState(SplashViewModel.CheckingState.LOCATION_PERMISSION)
                    } else {
                        viewModel.updateIsNetworkConnectionErrorDialogShowing(true)
                    }
                }
            }
            SplashViewModel.CheckingState.LOCATION_PERMISSION -> {
                var flag = true
                for (permission in locationPermissions) {
                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                        viewModel.updateScreenState(SplashViewModel.ScreenState.PERMISSION)
                        flag = false
                        break
                    }
                }
                if (flag) viewModel.updateCheckingState(SplashViewModel.CheckingState.SIGN_IN)
            }
            SplashViewModel.CheckingState.SIGN_IN -> {
                coroutineScope.launch {
                    delay(1000)
                    if (viewModel.checkIsSignedIn()) {
                        moveToMainScreen()
                    } else {
                        moveToStartScreen()
                    }
                }
            }
        }
    }
    when (screenState) {
        SplashViewModel.ScreenState.SPLASH -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFF2D2573))
                ,
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight(),
                    painter = painterResource(id = R.drawable.splash_logo),
                    contentDescription = null
                )
            }
            if (isNetworkConnectionErrorDialogShowing) {
                NetworkConnectionErrorDialog(
                    checkNetworkState = viewModel::checkNetworkState,
                    updateCheckingState = viewModel::updateCheckingState,
                    updateIsNetworkConnectionErrorDialogShowing = viewModel::updateIsNetworkConnectionErrorDialogShowing
                )
            }
        }
        SplashViewModel.ScreenState.PERMISSION -> {
            PermissionCheckingScreen(
                locationPermissions = locationPermissions,
                updateScreenState = viewModel::updateScreenState,
                updateCheckingState = viewModel::updateCheckingState
            )
        }
    }
}

@Composable
fun NetworkConnectionErrorDialog(
    checkNetworkState: () -> Boolean,
    updateCheckingState: (SplashViewModel.CheckingState) -> Unit,
    updateIsNetworkConnectionErrorDialogShowing: (Boolean) -> Unit
) {
    val context = LocalContext.current
    Dialog(
        onDismissRequest = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "네트워크 연결을 확인해주세요"
            )
            Row {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f)
                        .height(40.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(
                            color = Color(0xFF2D2573),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            if (checkNetworkState()) {
                                updateIsNetworkConnectionErrorDialogShowing(false)
                                updateCheckingState(SplashViewModel.CheckingState.LOCATION_PERMISSION)
                            } else {
                                Toast
                                    .makeText(context, "네트워크 연결을 확인해주세요", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "확인",
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .weight(1f)
                        .height(40.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(
                            color = Color(0xFFD9DCE7),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {

                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "닫기"
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionCheckingScreen(
    locationPermissions: Array<String>,
    updateScreenState: (SplashViewModel.ScreenState) -> Unit,
    updateCheckingState: (SplashViewModel.CheckingState) -> Unit
) {

    val context = LocalContext.current
    // ACCESS_FINE_LOCATION: 정확한 위치
    // ACCESS_COARSE_LOCATION: 대략적인 위치
    val permissionRequestLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if(
            result.entries.contains(mapOf(Manifest.permission.ACCESS_FINE_LOCATION to true).entries.first())
            && result.entries.contains(mapOf(Manifest.permission.ACCESS_COARSE_LOCATION to true).entries.first())) {
            Toast.makeText(context, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            updateScreenState(SplashViewModel.ScreenState.SPLASH)
            updateCheckingState(SplashViewModel.CheckingState.SIGN_IN)
        } else {
            Toast.makeText(context, "위치 권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth()
                .height(100.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    permissionRequestLauncher.launch(locationPermissions)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "위치 권한 허용하기"
            )
        }
        Text(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp),
            text = "사용자의 위치 정보는 약속 인원들에게 자신의 위치를 공유하는 앱의 핵심 기능에 반드시 필요합니다." +
                    "\n위치 정보는 자신의 위치를 사용자가 직접 전송 버튼을 눌러 일정의 인원들에게 자신의 위치를 공유할 때에만 사용되며, 다른 기능에는 일절 사용되지 않습니다.",
            fontSize = 20.sp
        )
    }
}