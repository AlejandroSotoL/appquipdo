package com.tramites1cero1.centralizacion.ui.screen.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.data.model.CreateUserDTO
import com.tramites1cero1.centralizacion.domain.repository.AuthRepository
import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SignUpStep1State(
    val firstName: String = "",
    val middleName: String? = "",
    val lastName: String = "",
    val secondLastName: String? = "",
    val firstNameError: String? = null,
    val middleNameError: String? = null,
    val lastNameError: String? = null,
    val secondLastNameError: String? = null,
    val isLoading: Boolean = false
)

sealed interface SignUpStep1Event{
    data object NavigateToStep2 : SignUpStep1Event
    data object NavigateBack : SignUpStep1Event
}

@HiltViewModel
class SignUpStep1ViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val _uiState = MutableStateFlow(SignUpStep1State())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<SignUpStep1Event>()
    val event = _event.receiveAsFlow()

    init {
        loadDraft()
    }

    /**
     * Carga el borrador guardado en DataStore y actualiza el UI state.
     */
    private fun loadDraft() {
        viewModelScope.launch {
            authRepository.getRegistrationDraft().collect { draft ->
                draft?.let {
                    _uiState.update { state ->
                        state.copy(
                            firstName = it.firstName ?: "",
                            middleName = it.middleName ?: "",
                            lastName = it.lastName ?: "",
                            secondLastName = it.secondLastName ?: ""
                        )
                    }
                }
            }
        }
    }

    fun onFirstNameChanged(value : String) {
        _uiState.update { it.copy(firstName = value, firstNameError = null) }
    }

    fun onMiddleNameChanged(value : String) {
            _uiState.update { it.copy(middleName = value, middleNameError = null) }
    }
    fun onLastNameChanged(value : String) {
        _uiState.update { it.copy(lastName = value, lastNameError = null) }
    }
    fun onSecondLastNameChanged(value : String) {
        _uiState.update { it.copy(secondLastName = value, secondLastNameError = null) }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _event.send(SignUpStep1Event.NavigateBack)
        }
    }

    fun onNextClicked() {
        if (validateInput()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val currentDraft = authRepository.getRegistrationDraft().firstOrNull()

                val updatedDraft = (currentDraft ?: CreateUserDTO()).copy(
                    firstName = _uiState.value.firstName.trim(),
                    middleName = _uiState.value.middleName?.trim().takeIf { !it.isNullOrEmpty() },
                    lastName = _uiState.value.lastName.trim(),
                    secondLastName = _uiState.value.secondLastName?.trim().takeIf { !it.isNullOrEmpty() }
                )

                authRepository.saveRegistrationDraft(updatedDraft)

                _event.send(SignUpStep1Event.NavigateToStep2)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun validateInput(): Boolean {
        _uiState.update { it.copy(firstNameError = null, lastNameError = null, secondLastNameError = null) }
        val currentState = _uiState.value

        if (currentState.firstName.isBlank()) {
            _uiState.update { it.copy(firstNameError = "El primer nombre es requerido") }
            return false
        }else if (currentState.firstName.length > 100) {
            _uiState.update { it.copy(firstNameError = "El primer nombre no puede tener más de 100 caracteres") }
            return false
        } else if (!currentState.firstName.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"))) {
            _uiState.update { it.copy(firstNameError = "El primer nombre solo puede contener letras") }
            return false
        }

        currentState.middleName?.length?.let {
            if (it > 100) {
                _uiState.update { it.copy(middleNameError = "El segundo nombre no puede tener más de 100 caracteres") }
                return false
            } else if (!currentState.firstName.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"))) {
                _uiState.update { it.copy(middleNameError = "El segundo nombre solo puede contener letras") }
                return false
            }
        }
        if (currentState.lastName.isBlank()) {
            _uiState.update { it.copy(lastNameError = "El primer apellido es requerido") }
            return false
        }else if (currentState.lastName.length > 100) {
            _uiState.update { it.copy(lastNameError = "El primer apellido no puede tener más de 100 caracteres") }
            return false
        } else if (!currentState.firstName.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"))) {
            _uiState.update { it.copy(lastNameError = "El primer apellido solo puede contener letras") }
            return false
        }
        currentState.secondLastName?.length?.let {
            if (it > 100) {
                _uiState.update { it.copy(secondLastNameError = "El segundo apellido no puede tener más de 100 caracteres") }
                return false
            } else if (!currentState.firstName.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"))) {
                _uiState.update { it.copy(secondLastNameError = "El segundo apellido solo puede contener letras") }
                return false
            }
        }
        return true
    }
}