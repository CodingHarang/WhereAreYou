package com.whereareyounow.ui.home.schedule.newschedule

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.whereareyounow.ui.home.schedule.scheduleedit.ScheduleEditScreen
import com.whereareyounow.ui.home.schedule.scheduleedit.ScheduleEditViewModel

@Composable
fun NewScheduleScreen(
    initialYear: Int,
    initialMonth: Int,
    initialDate: Int,
    moveToSearchLocationScreen: () -> Unit,
    moveToFriendsListScreen: () -> Unit,
    moveToBackScreen: () -> Unit,
    viewModel: ScheduleEditViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.updateScheduleDate(
            year = initialYear,
            month = initialMonth,
            date = initialDate
        )
    }
    val scheduleEditScreenUIState = viewModel.scheduleEditScreenUIState.collectAsState().value
    val scheduleEditScreenSideEffectFlow = viewModel.scheduleEditScreenSideEffectFlow
    ScheduleEditScreen(
        scheduleEditScreenUIState = scheduleEditScreenUIState,
        scheduleEditScreenSideEffectFlow = scheduleEditScreenSideEffectFlow,
        updateScheduleName = viewModel::updateScheduleName,
        updateScheduleDate = viewModel::updateScheduleDate,
        updateScheduleTime = viewModel::updateScheduleTime,
        updateMemo = viewModel::updateMemo,
        onComplete = viewModel::addNewSchedule,
        moveToSearchLocationScreen = moveToSearchLocationScreen,
        moveToFriendsListScreen = moveToFriendsListScreen,
        moveToBackScreen = moveToBackScreen
    )
}