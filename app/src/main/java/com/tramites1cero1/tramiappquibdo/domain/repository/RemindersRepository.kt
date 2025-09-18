package com.tramites1cero1.tramiappquibdo.domain.repository

import CreateReminderDto
import com.tramites1cero1.tramiappquibdo.data.model.RemindersByUserDto
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO

interface RemindersRepository {
    suspend fun getRemindersByUser(userId :  Int) : List<RemindersByUserDto>
    suspend fun createReminders(reminderDto : CreateReminderDto) : ValidationResponseDTO
}