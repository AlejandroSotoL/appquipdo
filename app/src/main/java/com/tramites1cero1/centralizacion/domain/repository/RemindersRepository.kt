package com.tramites1cero1.centralizacion.domain.repository

import CreateReminderDto
import com.tramites1cero1.centralizacion.data.model.RemindersByUserDto
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO

interface RemindersRepository {
    suspend fun getRemindersByUser(userId :  Int) : List<RemindersByUserDto>
    suspend fun createReminders(reminderDto : CreateReminderDto) : ValidationResponseDTO
}