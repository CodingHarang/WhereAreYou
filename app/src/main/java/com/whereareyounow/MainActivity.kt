package com.whereareyounow

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.whereareyounow.data.GlobalValue
import com.whereareyounow.ui.MainNavigation
import com.whereareyounow.ui.theme.WhereAreYouTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: GlobalViewModel by lazy {
        ViewModelProvider(this)[GlobalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getToken()
        // 화면 크기 정보 저장
        updateGlobalValue()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            WhereAreYouTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val systemUiController = rememberSystemUiController()

                    systemUiController.setStatusBarColor(
                        color = Color(0xFFFAFAFA),
                        darkIcons = true
                    )
                    MainNavigation()
                }
            }
        }
    }

    // 모든 영역의 단위는 기본적으로 pixel
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    private fun updateGlobalValue() {
        val resources = application.resources
        val metrics = resources.displayMetrics
        val density = metrics.density
        val xdpi = metrics.xdpi
        val ydpi = metrics.ydpi
        val statusBarResourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        // getDimension(): dimen.xml에 정의한 dp값을 기기에 맞게 px로 변환하여 반올림한 값을 int로 반환한다.
        val screenHeight = metrics.heightPixels
        val screenWidth = metrics.widthPixels
        val statusBarHeight = resources.getDimension(statusBarResourceId)
        // 상단 상태바, 시스템 네비게이션 바 제외한 화면 높이
        GlobalValue.screenHeightWithoutStatusBar = screenHeight.toFloat()
        GlobalValue.screenWidth = screenWidth.toFloat()
        // 하단 네비게이션 바 높이는 전체 화면의 1/15
        GlobalValue.bottomNavBarHeight = GlobalValue.screenHeightWithoutStatusBar / 15
        // 상단 영역 높이는 전체 화면의 1/15
        GlobalValue.topBarHeight = GlobalValue.screenHeightWithoutStatusBar / 15
        // 캘린더 뷰의 높이는 상단 상태바, 상단 영역, 하단 네비게이션 바, 시스템 네비게이션 바를 제외한 영역의 2/5
        GlobalValue.calendarViewHeight = GlobalValue.screenHeightWithoutStatusBar * 26 / 75
        // 일별 간략 정보 뷰의 높이는 상단 상태바, 상단 영역, 하단 네비게이션 바, 시스템 네비게이션 바를 제외한 영역의 3/5
        GlobalValue.dailyScheduleViewHeight = GlobalValue.screenHeightWithoutStatusBar * 39 / 75
        Log.e("ScreenValue", "screenHeightWithoutStatusBar: ${GlobalValue.screenHeightWithoutStatusBar} ${GlobalValue.screenHeightWithoutStatusBar / density}\n" +
                "screenWidth: ${GlobalValue.screenWidth} ${GlobalValue.screenWidth / density}\n" +
                "bottomNavBarHeight: ${GlobalValue.bottomNavBarHeight} ${GlobalValue.bottomNavBarHeight / density}\n" +
                "topBarHeight: ${GlobalValue.topBarHeight} ${GlobalValue.topBarHeight / density}\n" +
                "calendarViewHeight: ${GlobalValue.calendarViewHeight} ${GlobalValue.calendarViewHeight / density}\n" +
                "dailyScheduleViewHeight: ${GlobalValue.dailyScheduleViewHeight} ${GlobalValue.dailyScheduleViewHeight / density}"
        )
    }
}

