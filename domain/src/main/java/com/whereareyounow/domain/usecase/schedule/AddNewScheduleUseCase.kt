package com.whereareyounow.domain.usecase.schedule

import com.whereareyounow.domain.entity.apimessage.schedule.AddNewScheduleRequest
import com.whereareyounow.domain.entity.apimessage.schedule.AddNewScheduleResponse
import com.whereareyounow.domain.repository.ScheduleRepository
import com.whereareyounow.domain.util.NetworkResult

class AddNewScheduleUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(
        token: String,
        body: AddNewScheduleRequest
    ): NetworkResult<AddNewScheduleResponse> {
        return repository.addNewSchedule(token, body)
    }
}