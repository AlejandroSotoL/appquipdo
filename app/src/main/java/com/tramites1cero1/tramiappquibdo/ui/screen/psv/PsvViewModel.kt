package com.tramites1cero1.tramiappquibdo.ui.screen.psv

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.data.model.DocumentTypeDTO
import com.tramites1cero1.tramiappquibdo.domain.model.Tax
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.MunicipalityRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.UserPreferencesRepository
import com.tramites1cero1.tramiappquibdo.domain.usecase.CreateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.net.URLDecoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PsvUiState(

    val taxIconResId: Int = R.drawable.icopredial,
    val paymentDateTime: String = "",

    val showValidationErrorDialog: Boolean = false,
    val dataPolicyUrl: String = "",
    val privacyPolicyUrl: String = "",

    // --- Control de Flujo ---
    val currentStep: Int = 1,
    val taxName: String = "",
    val isNextButtonEnabled: Boolean = false,

    // --- Paso 1: Datos del Ciudadano ---
    val documentTypeOptions: List<DocumentTypeDTO> = emptyList(),
    val selectedDocumentType: DocumentTypeDTO? = null,
    val documentNumber: String = "",
    val firstName: String = "",
    val secondName: String = "",
    val firstLastName: String = "",
    val secondLastName: String = "",

    val documentNumberError: String? = null,
    val firstNameError: String? = null,
    val firstLastNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val invoiceNumberError: String? = null,
    val valueToPayError: String? = null,
    val termsError: String? = null,

    // --- Paso 2: Datos de Facturación ---
    val email: String = "",
    val phone: String = "",
    val invoiceNumber: String = "",
    val valueToPay: String = "",
    val acceptsDataPolicy: Boolean = false,
    val acceptsTerms: Boolean = false,

    // --- Estado de la Operación ---
    val isProcessingPayment: Boolean = false,
    val paymentUrl: String? = null,
    val error: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PsvPaymentViewModel @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val authRepository: AuthRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PsvUiState())
    val uiState = _uiState.asStateFlow()


    private val _documentTypes = MutableStateFlow<List<DocumentTypeDTO>>(emptyList())
    val documentTypes: StateFlow<List<DocumentTypeDTO>> = _documentTypes

    private val taxId: Int = savedStateHandle.get<Int>("taxId") ?: 0
    private var entityCode: String = "" // Guardaremos el entityCode aquí

    init {
        val dataPolicyUrl: String =
            URLDecoder.decode(savedStateHandle.get<String>("dataPolicyUrl") ?: "", "UTF-8")
        val privacyPolicyUrl: String =
            URLDecoder.decode(savedStateHandle.get<String>("privacyPolicyUrl") ?: "", "UTF-8")


        val taxName =
            URLDecoder.decode(savedStateHandle.get<String>("taxName") ?: "Impuesto", "UTF-8")
        val iconRes = when (taxId) {
            1 -> R.drawable.icopredial
            2, 3 -> R.drawable.icoica
            else -> R.drawable.icopredial
        }

        _uiState.update {
            it.copy(
                taxName = taxName,
                taxIconResId = iconRes,
                dataPolicyUrl = dataPolicyUrl,
                privacyPolicyUrl = privacyPolicyUrl
            )
        }
        loadInitialData()
        loadDocumentTypes()
        setCurrentDateTime()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Obtenemos el ID del municipio guardado en las preferencias
            val municipalityId = userPreferencesRepository.getSavedUbication().first().municipalityId
            if (municipalityId == 0) return@launch

            // Cargamos la configuración completa del municipio
            val config = municipalityRepository.getMunicipalityData(municipalityId)
            entityCode = config.codigoEntidad

            // Cargamos los tipos de documento
            val docTypes = authRepository.getTypeDocuments()

            // Actualizamos el estado con las URLs y las opciones del dropdown
            _uiState.update {
                it.copy(
                    documentTypeOptions = docTypes ?: emptyList(),
                    dataPolicyUrl = config.dataPolicyUrl,
                    privacyPolicyUrl = config.privacyPolicyUrl
                )
            }
        }
    }

    private fun loadDocumentTypes() {
        viewModelScope.launch {
            val docTypes = authRepository.getTypeDocuments()
            _uiState.update { it.copy(documentTypeOptions = docTypes ?: emptyList()) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCurrentDateTime() {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formatted = now.format(formatter)

        _uiState.update { it.copy(paymentDateTime = formatted) }
    }

    private fun validateAllSteps(): Boolean {
        val state = _uiState.value
        val isStep1Valid = state.selectedDocumentType != null &&
                state.documentNumber.isNotBlank() &&
                state.firstName.isNotBlank() &&
                state.firstLastName.isNotBlank()

        val isStep2Valid = state.email.isNotBlank() &&
                state.phone.isNotBlank() &&
                state.invoiceNumber.isNotBlank() &&
                state.valueToPay.isNotBlank() &&
                state.acceptsDataPolicy &&
                state.acceptsTerms

        return isStep1Valid && isStep2Valid
    }

    // --- MANEJADORES DE NAVEGACIÓN ENTRE PASOS ---
    fun onNextStep() {
        if (_uiState.value.currentStep < 3) {
            _uiState.update {
                it.copy(
                    currentStep = it.currentStep + 1,
                    isNextButtonEnabled = false
                )
            }
            validateForm()
        }
    }

    fun onPreviousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update {
                it.copy(
                    currentStep = it.currentStep - 1,
                    isNextButtonEnabled = true
                )
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(paymentUrl = null) }
    }

    fun onPayClicked() {
        if (validateAllSteps()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isProcessingPayment = true, error = null) } // Limpiar errores previos
                val state = _uiState.value

                val taxInfo = Tax(
                    entityCode = entityCode,
                    taxId = taxId,
                    name = "${state.firstName} ${state.firstLastName}".trim(),
                    document = state.documentNumber,
                    reference = state.invoiceNumber,
                    value = state.valueToPay.toIntOrNull() ?: 0,
                    entity = "",
                    taxName = state.taxName,
                    dueDate = "",
                    invoice = "",
                    pdfUrltoApi = ""
                )

                createTransactionUseCase(taxInfo, state.email)
                    .onSuccess { paymentInfo ->
                        _uiState.update {
                            it.copy(
                                isProcessingPayment = false,
                                paymentUrl = paymentInfo.url
                            )
                        }
                    }
                    .onFailure { exception ->
                        _uiState.update {
                            it.copy(
                                isProcessingPayment = false,
                                error = exception.localizedMessage ?: "Ocurrió un error al procesar el pago."
                            )
                        }
                    }
            }
        } else {
            _uiState.update { it.copy(showValidationErrorDialog = true) }
        }
    }

    fun onValidationDialogDismissed() {
        _uiState.update { it.copy(showValidationErrorDialog = false) }
    }

    fun onDocumentTypeSelected(docType: DocumentTypeDTO) {
        _uiState.update { it.copy(selectedDocumentType = docType) }
        validateForm()
    }

    fun onDocumentNumberChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            _uiState.update { it.copy(documentNumber = newValue) }
            validateForm()
        }
    }
    fun onFirstNameChange(newValue: String) {
        if (newValue.all { it.isLetter() || it.isWhitespace() }) {
            _uiState.update { it.copy(firstName = newValue) }
            validateForm()
        }
    }

    fun onSecondNameChange(newValue: String) {
        if (newValue.all { it.isLetter() || it.isWhitespace() }) {
            _uiState.update { it.copy(secondName = newValue) }
        }
    }

    fun onFirstLastNameChange(newValue: String) {

        if (newValue.all { it.isLetter() || it.isWhitespace() }) {
            _uiState.update { it.copy(firstLastName = newValue) }
            validateForm()
        }
    }

    fun onSecondLastNameChange(newValue: String) {
        if (newValue.all { it.isLetter() || it.isWhitespace() }) {
            _uiState.update { it.copy(secondLastName = newValue) }
        }
    }

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue) }
        validateForm()
    }

    fun onPhoneChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            _uiState.update { it.copy(phone = newValue) }
            validateForm()
        }
    }

    fun onInvoiceNumberChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
        _uiState.update { it.copy(invoiceNumber = newValue) }
        validateForm()
        }
    }

    fun onValueToPayChange(newValue: String) {
        if (newValue.all { it.isDigit() }) {
            _uiState.update { it.copy(valueToPay = newValue) }
            validateForm()
        }
    }

    fun onAcceptsDataPolicyChange(isChecked: Boolean) {
        _uiState.update { it.copy(acceptsDataPolicy = isChecked) }
        validateForm()
    }

    fun onAcceptsTermsChange(isChecked: Boolean) {
        _uiState.update { it.copy(acceptsTerms = isChecked) }
        validateForm()
    }

    // --- LÓGICA DE VALIDACIÓN ---
    private fun validateForm(): Boolean {
        val state = _uiState.value
        val isValid = when (state.currentStep) {
            1 -> {

                state.selectedDocumentType != null &&
                        state.documentNumber.isNotBlank() &&
                        state.firstName.isNotBlank() &&
                        state.firstLastName.isNotBlank()
            }
            2 -> {
                Patterns.EMAIL_ADDRESS.matcher(state.email).matches() &&
                        state.phone.isNotBlank() &&
                        state.invoiceNumber.isNotBlank() &&
                        state.valueToPay.isNotBlank() &&
                        state.acceptsDataPolicy &&
                        state.acceptsTerms
            }
            3 -> true
            else -> false
        }
        _uiState.update { it.copy(isNextButtonEnabled = isValid) }
        return isValid
    }
}
