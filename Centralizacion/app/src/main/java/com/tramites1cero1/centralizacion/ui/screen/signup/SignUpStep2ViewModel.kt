package com.tramites1cero1.centralizacion.ui.screen.signup

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.data.model.CreateUserDTO
import com.tramites1cero1.centralizacion.data.model.DocumentTypeDTO
import com.tramites1cero1.centralizacion.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.w3c.dom.Document
import javax.inject.Inject


data class SignUpStep2UiState(
    val documentTypes: List<String> = emptyList(),
    val selectedDocumentType: String = "",
    val documentNumber: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val documentNumberError: String? = null,
    val documentTypeError: String? = null,
    val documentTypeQuery : String = "",
    val isDropdownExpanded : Boolean = false
)

// SignUpStep2Event.kt
sealed interface SignUpStep2Event {
    data object NavigateToStep3 : SignUpStep2Event
    data object NavigateBack : SignUpStep2Event
}
@HiltViewModel
class SignUpStep2ViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpStep2UiState())
    val uiState = _uiState.asStateFlow()

    private val _documentTypes = MutableStateFlow<List<DocumentTypeDTO>>(emptyList())
    val documentTypes: StateFlow<List<DocumentTypeDTO>> = _documentTypes

    private val _event = Channel<SignUpStep2Event>()
    val event = _event.receiveAsFlow()

    private var registrationDraft: CreateUserDTO? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            if(authRepository.getTypeDocuments()!!.isEmpty()){
                println("We have problems with Api information about Documents types.");
            }else{
                val result = authRepository.getTypeDocuments().orEmpty()
                _documentTypes.value = result

                _uiState.update {
                    it.copy(
                        documentTypes = result.map { doc -> doc?.name ?: "Undefined" }
                    )
                }
            registrationDraft = authRepository.getRegistrationDraft().firstOrNull()
            registrationDraft?.let { draft ->
                _uiState.update { state ->
                    state.copy(
                        selectedDocumentType = result.firstOrNull { it.id == draft.documentTypeId }?.name ?: "",
                        documentNumber = draft.nationalId ?: "",
                        email = draft.email ?: "",
                        password = draft.password ?: ""
                    )
                }
            }
            }
        }
    }

    fun onDocumentQueryChanged(query: String) {
        _uiState.update { it.copy(
            documentTypeQuery = query,
            isDropdownExpanded = true,
            documentTypeError = null) }
    }

    fun onDocumentTypeSelected(docType: String) {
        _uiState.update {
            it.copy(
                selectedDocumentType = docType,
                documentTypeQuery = docType,
                isDropdownExpanded = false,
                documentTypeError = null
            )
        }
    }

    fun onDropdownFocusChanged(isFocused: Boolean) {
        _uiState.update { it.copy(
            isDropdownExpanded = isFocused,
            selectedDocumentType =  if(isFocused) "" else _uiState.value.selectedDocumentType,
            documentTypeQuery = if(isFocused) "" else _uiState.value.documentTypeQuery,


        ) }
    }

    fun onDocumentNumberChanged(value: String) {
        _uiState.update { it.copy(documentNumber = value, documentNumberError = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onNextClicked() {
        if (validateInput()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                val currentDraft = authRepository.getRegistrationDraft().firstOrNull() ?: CreateUserDTO()
                val updatedDraft = currentDraft.copy(
                    documentTypeId = mapDocumentTypeToId(_uiState.value.selectedDocumentType),
                    nationalId = _uiState.value.documentNumber,
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )
                authRepository.saveRegistrationDraft(updatedDraft)
                _event.send(SignUpStep2Event.NavigateToStep3)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _event.send(SignUpStep2Event.NavigateBack)
        }
    }

    private fun validateInput(): Boolean {
        var result = true
        if (_uiState.value.selectedDocumentType.isBlank()) {
            // Si está vacío o menor o igual a 0, no es válido
            _uiState.update { it.copy(documentTypeError = "Selecciona un tipo de documento") }
           result = false
        }
        if (_uiState.value.documentNumber.isBlank()) {
            _uiState.update { it.copy(documentNumberError = "Ingresa un número de documento") }
            result = false
        }else if(_uiState.value.documentNumber.length >= 20){
            _uiState.update { it.copy(documentNumberError = "El documento debe tener menos de 20 dígitos") }
            result = false
        }else if (_uiState.value.documentNumber.contains(" ")) {
            _uiState.update { it.copy(documentNumberError = "El documento no debe contener espacios") }
            result = false
        }else if (!_uiState.value.documentNumber.any() { it.isDigit() }) {
            _uiState.update { it.copy(documentNumberError = "el documento debe incluir números") }
            result = false
        }

        if (_uiState.value.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Ingresa un correo electrónico") }
            result = false
        }else if(!_uiState.value.email.contains("@")){
            _uiState.update { it.copy(emailError = "Ingresa un correo electrónico valido") }
            result = false
        }
        if (_uiState.value.password.isBlank()) {
            _uiState.update { it.copy(passwordError = "El campo no puede estar vacio") }
            result = false
        } else if (_uiState.value.password.length < 8) {
            _uiState.update { it.copy(passwordError = "Debe tener mínimo 8 caracteres") }
            result = false
        } else if (!_uiState.value.password.any { it.isDigit() }) {
            _uiState.update { it.copy(passwordError = "Debe incluir al menos un número") }
            result = false
        } else if (!_uiState.value.password.any { it.isUpperCase() }) {
            _uiState.update { it.copy(passwordError = "Debe incluir al menos una mayúscula") }
            result = false
        } else if (!_uiState.value.password.any { it.isLowerCase() }) {
            _uiState.update { it.copy(passwordError = "Debe incluir al menos una minúscula") }
            result = false
        } else if (!_uiState.value.password.any { "!@#\$%^&+=¿?*._-".contains(it) }) {
            _uiState.update { it.copy(passwordError = "Debe incluir al menos un carácter especial") }
            result = false
        }
        return result
    }

    /**
     * Mapear el tipo de documento seleccionado a un ID numérico.
     * Puedes ajustar esta lógica según tu API.
     */
    private fun mapDocumentTypeToId(selectedType: String): Int {
        val docTypes = _uiState.value.documentTypes
        val index = docTypes.indexOfFirst { it.equals(selectedType, ignoreCase = true) }
        return if (index >= 0) index + 1 else 1
    }
}
