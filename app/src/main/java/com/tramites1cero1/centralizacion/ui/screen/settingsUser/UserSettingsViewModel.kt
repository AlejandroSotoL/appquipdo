package com.tramites1cero1.centralizacion.ui.screen.settingsUser

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.data.model.EmailsDtos.EmailDto
import com.tramites1cero1.centralizacion.data.model.UserDTO
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordByForgetDto
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordRequestDto
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.domain.repository.AuthRepository
import com.tramites1cero1.centralizacion.domain.repository.SendEmailRepository
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val  authRepository: AuthRepository,
    private val sendEmailRepository: SendEmailRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
): ViewModel(){

    val user: StateFlow<UserDTO?> = authRepository.user
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = null
        )

    val isActiveSendEmail = userPreferencesRepository.remindersSendIsVisibleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setSendEmail(isActive: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setRemindersSendVisible(isActive)
        }
    }

    suspend fun createNewPassword(
        userId: Int,
        request: UpdatePasswordRequestDto
    ): ValidationResponseDTO {
        return try {
            val currentUser = user.value
            if (currentUser == null || currentUser.loginStatus == false) {
                return ValidationResponseDTO(
                    booleanStatus = false,
                    sentencesError = "No puedes cambiar la contraseña, el usuario no está logueado."
                )
            }

            val response = authRepository.updatePasswordUser(userId, request)
            if (response.booleanStatus) {
                val email = EmailDto(
                    to = currentUser.email,
                    subject = "🔒 Contraseña cambiada - ${currentUser.firstName} ${currentUser.lastName}",
                    body = """
        Hola ${currentUser.firstName},
        
        Queremos informarte que tu contraseña fue cambiada exitosamente el día 
        
        Si fuiste tú, no necesitas hacer nada más.
        Si no reconoces este cambio, por favor contacta de inmediato con nuestro equipo de soporte.
        
        Saludos,  
        El equipo de Centralización
    """.trimIndent()
                )
                val responseEmail = sendEmailRepository.sendEmail(email)
                return if (responseEmail.booleanStatus) {
                     response
                } else {
                    ValidationResponseDTO(
                        sentencesError = "Tenemos problemas con el email de verificación: Error -> ${responseEmail.sentencesError}",
                        booleanStatus = false
                    )
                }
            } else {
                ValidationResponseDTO(
                    booleanStatus = false,
                    sentencesError = response.sentencesError
                )
            }
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false,
                sentencesError = "Tenemos problemas, intenta más tarde"
            )
        }
    }

    suspend fun updatePasswordByForget(
        userId: Int,
        request: UpdatePasswordByForgetDto
    ): ValidationResponseDTO {
        return try{
            var respose = authRepository.updatePasswordUserByForget(userId , request);
            if(!respose.booleanStatus){
                authRepository.clearUserSession()
            }
            respose
        }catch (ecx: Exception){
            ValidationResponseDTO(
                booleanStatus = false,
                sentencesError = "Teneos problemas"
            );
        }
    }
}