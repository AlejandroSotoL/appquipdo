package com.tramites1cero1.centralizacion.data.repository

import CreateReminderDto
import com.tramites1cero1.centralizacion.data.model.RemindersByUserDto
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.data.network.RemindersApiService
import com.tramites1cero1.centralizacion.domain.repository.RemindersRepository
import javax.inject.Inject

class RemindersRepositoryImpl @Inject constructor(
    private val remindersApiService : RemindersApiService
) : RemindersRepository{

    override suspend fun getRemindersByUser(userId: Int): List<RemindersByUserDto> {
        return try {
            val response = remindersApiService.getRemindersByUser(userId)
            response
        } catch (ex: Exception) {
            emptyList()
        }
    }

    override suspend fun createReminders(reminderDto: CreateReminderDto): ValidationResponseDTO {
        return try{
            val response = remindersApiService.createReminders(reminderDto)
                response
        }catch (ex : Exception){
            ValidationResponseDTO(
                sentencesError = "Tenemos problemas para crear el Recordatorio",
                booleanStatus = false
            );
        }
    }
}