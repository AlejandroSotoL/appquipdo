package com.tramites1cero1.centralizacion.ui.screen.signup

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.tramites1cero1.centralizacion.data.model.CreateUserDTO
import com.tramites1cero1.centralizacion.data.model.LoginDTO
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class SignUpStep3UiState(
    val phone: String = "",
    val birthDate: String = "",
    val address: String = "",
    val hasAcceptedTerms: Boolean = false,
    val hasAcceptedPrivacyPolicy: Boolean = false,
    val isLoading: Boolean = false,
    val phoneError: String? = null,
    val birthDateError: String? = null,
    val addressError: String? = null,
    val termsError: String? = null,
    val showDatePicker : Boolean = false,
    val email: String = "",
    val password: String = ""
)

sealed interface SignUpStep3Event {
    data object RegistrationSuccess : SignUpStep3Event
    data object NavigateBack : SignUpStep3Event
}

@HiltViewModel
class SignUpStep3ViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpStep3UiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<SignUpStep3Event>()
    val event = _event.receiveAsFlow()

    private val _scanningLogIn = mutableStateOf<ValidationResponseDTO?>(null)
    val scanningLogIn: State<ValidationResponseDTO?> = _scanningLogIn

    private var registrationDraft: CreateUserDTO? = null



    init {
        loadDraft()
    }

    /**
     * Carga el borrador de DataStore y lo pone en el UI State
     */
    private fun loadDraft() {
        viewModelScope.launch {
            registrationDraft = authRepository.getRegistrationDraft().firstOrNull()
            registrationDraft?.let { draft ->
                _uiState.update {
                    it.copy(
                        birthDate = draft.birthDate ?: "",
                        email = draft.email ?: "",
                        password = draft.password ?: ""

                    )
                }
            }
        }
    }

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, phoneError = null) }
    }

    fun onAddressChanged(address: String) {
        _uiState.update { it.copy(address = address, addressError = null) }
    }

    fun onTermsAccepted(accepted: Boolean) {
        _uiState.update { it.copy(hasAcceptedTerms = accepted, termsError = null) }
    }

    fun onPrivacyPolicyAccepted(accepted: Boolean) {
        _uiState.update { it.copy(hasAcceptedPrivacyPolicy = accepted, termsError = null) }
    }

    fun onBirthDateInputClicked() {
        _uiState.update { it.copy(showDatePicker = true, birthDateError = null ) }
    }

    fun onDateSelected(dateInMillis: Long) {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = format.format(Date(dateInMillis))

        _uiState.update {
            it.copy(
                birthDate = dateString,
                showDatePicker = false
            )
        }
    }

    fun onDatePickerDismissed() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _event.send(SignUpStep3Event.NavigateBack)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun onFinishClicked() {
        if(validateInput()){
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                val currentDraft = authRepository.getRegistrationDraft().firstOrNull() ?: CreateUserDTO()
                val finalUserData = currentDraft.copy(
                    phoneNumber = _uiState.value.phone,
                    address = _uiState.value.address,
                    birthDate = _uiState.value.birthDate,
                    loginStatus = if (FirebaseAuth.getInstance().currentUser != null) 1 else 0
                )
                try {
                    authRepository.saveRegistrationDraft(finalUserData)
                    val response = authRepository.registerUser(finalUserData)
                    val loginDto = LoginDTO(finalUserData.email, finalUserData.password)
                    _scanningLogIn.value = response
                    authRepository.clearPreferences()
                    if (response.booleanStatus && FirebaseAuth.getInstance().currentUser != null ) {
                        //  Autenticación inmediatamente después de registrar
                        val loginResult = authRepository.login(loginDto)
                        if (loginResult.booleanStatus) {
                            authRepository.getUserInformationByEmail(loginDto.email)
                                .onSuccess { user ->
                                    authRepository.saveUserSession(user)
                                    _uiState.update {
                                        it.copy(
                                            isLoading = true
                                        )
                                    }
                                }
                                .onFailure {
                                    Log.e("REGISTRO_DEBUG", "Fallo obteniendo información del usuario")
                                }
                        }
                    }


                } catch (e: Exception) {
                    _scanningLogIn.value = ValidationResponseDTO(
                        sentencesError = "Error desconocido: ${e.message}",
                        booleanStatus = false,
                        codeStatus = 404
                    )
                }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    private fun validateInput(): Boolean {
        val address = _uiState.value.address
        // Validación teléfono
        if (_uiState.value.phone.isBlank()) {
            _uiState.update { it.copy(phoneError = "Debes ingresar un número de teléfono.") }
            return false
        } else if (_uiState.value.phone.length < 7 || _uiState.value.phone.length > 15) {
            _uiState.update { it.copy(phoneError = "El número telefónico debe ser mayor a 7 y menor a 15 dígitos.") }
            return false
        } else if (!_uiState.value.phone.all { it.isDigit() }) {
            _uiState.update { it.copy(phoneError = "El número telefónico debe ser numérico.") }
            return false
        }

        // Validación fecha de nacimiento (funciona en todas las APIs)
        if (_uiState.value.birthDate.isBlank()) {
            _uiState.update { it.copy(birthDateError = "Debes ingresar una fecha de nacimiento.") }
            return false
        } else {
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.isLenient = false // fuerza formato correcto
                val birthDate = sdf.parse(_uiState.value.birthDate)
                val today = Calendar.getInstance().time

                // Fecha futura
                if (birthDate.after(today)) {
                    _uiState.update { it.copy(birthDateError = "La fecha de nacimiento no puede ser en el futuro.") }
                    return false
                }

                // Calcular edad
                val calBirth = Calendar.getInstance().apply { time = birthDate }
                val calToday = Calendar.getInstance()

                var age = calToday.get(Calendar.YEAR) - calBirth.get(Calendar.YEAR)
                if (calToday.get(Calendar.DAY_OF_YEAR) < calBirth.get(Calendar.DAY_OF_YEAR)) {
                    age-- // aún no tiene la edad exacta
                }

                if (age < 18) {
                    _uiState.update { it.copy(birthDateError = "Debes ser mayor de 18 años.") }
                    return false
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(birthDateError = "Formato de fecha inválido (usa yyyy-MM-dd).") }
                return false
            }
        }

        // Validación dirección
        if (_uiState.value.address.isBlank()) {
            _uiState.update { it.copy(addressError = "Debes ingresar una dirección.") }
            return false
        }else if(address.length > 200){
            _uiState.update { it.copy(addressError = "La dirección debe tener menos de 200 caracteres.") }
            return false
        }

        // Validación términos
        if (!_uiState.value.hasAcceptedTerms || !_uiState.value.hasAcceptedPrivacyPolicy) {
            _uiState.update { it.copy(termsError = "Debes aceptar los términos y políticas.") }
            return false
        }

        return true
    }


}
