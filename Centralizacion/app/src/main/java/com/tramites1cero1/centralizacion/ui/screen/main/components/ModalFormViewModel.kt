package com.tramites1cero1.centralizacion.ui.screen.main.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.data.model.PeopleInvitated
import com.tramites1cero1.centralizacion.data.model.UserDTO
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.first


data class ModalData(
    val identificacion: String = "",
    val nombresApellidos: String = "",
    val telefono: String = "",
    val correo: String = "",
    val aceptaPoliticas: Boolean = false,
    val aceptaCondiciones: Boolean = false,
    val isValidSaveInformation:  Boolean = false,
    val isEditable : Boolean = true,
    // --- Campos de Error ---
    val identificacionError: String? = null,
    val correoError: String? = null,
    val telefonoError: String? = null,
    val nombresApellidosError: String? = null,
    val terminosError: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ModalFormViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(ModalData())
    val uiState = _uiState.asStateFlow()
    private val _registrationResult = MutableStateFlow<ValidationResponseDTO?>(null)
    val registrationResult = _registrationResult.asStateFlow()
    fun onFormDataChange(newData: ModalData) {
        _uiState.value = newData.copy(
            nombresApellidosError = null,
            telefonoError = null,
            identificacionError = null,
            correoError = null,
            terminosError = null
        )
    }

     fun createPeopleInvitated() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val structur = PeopleInvitated(
                    completeName = _uiState.value.nombresApellidos,
                    email = _uiState.value.correo,
                    phoneNumber = _uiState.value.telefono,
                    documentationDni = _uiState.value.identificacion
                )

                val response = userPreferencesRepository.createPeopleInvitates(structur)
                _registrationResult.value = response

            } catch (ex: Exception) {
                _registrationResult.value = ValidationResponseDTO(
                    codeStatus = 500,
                    booleanStatus = false,
                    sentencesError = "Error -> ${ex.message}"
                )
            } finally {
                delay(700)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
     }


    fun initForm(loggedInUser: UserDTO?) {
        viewModelScope.launch {
            val userToDisplay = try {
                loggedInUser ?: userPreferencesRepository.getGuestUserData().first()
            } catch (e: Exception) {
                null
            }

            _uiState.value = ModalData(
                identificacion = userToDisplay?.nationalId.orEmpty(),
                nombresApellidos = "${userToDisplay?.firstName.orEmpty()} ${userToDisplay?.lastName.orEmpty()}".trim(),
                telefono = userToDisplay?.phoneNumber.orEmpty(),
                correo = userToDisplay?.email.orEmpty(),
                isEditable = loggedInUser == null
            )
        }
    }


    fun validate(): Boolean {
        val data = _uiState.value
        val nombresApellidosError = if(data.nombresApellidos.isBlank()) "Ingrese un nombre" else null
        val telefonoError = if(data.telefono.length < 10) "El teléfono debe tener al menos 10 caracteres" else null
        val idError = if (data.identificacion.isBlank() || data.identificacion.length < 8) "Ingrese una identificacion válida" else null
        val emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(data.correo)
                .matches()
        ) "El correo no es válido" else null

        val termsError =
            if (!data.aceptaPoliticas || !data.aceptaCondiciones) "Debes aceptar las políticas y condiciones" else null

        val hasErrors = listOf(nombresApellidosError, telefonoError, idError, emailError, termsError).any { it != null }

        _uiState.update {
            it.copy(
                nombresApellidosError = nombresApellidosError,
                telefonoError = telefonoError,
                identificacionError = idError,
                correoError = emailError,
                terminosError = termsError
            )
        }

        return !hasErrors
    }
}