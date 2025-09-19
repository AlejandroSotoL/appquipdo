package com.tramites1cero1.centralizacion.ui.screen.taxpayments
import android.os.Build
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tramites1cero1.centralizacion.data.model.Date
import com.tramites1cero1.centralizacion.data.model.UserDTO
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.data.model.historyPaymentDTOs.CreatePaymentHistory
import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.domain.repository.AuthRepository
import com.tramites1cero1.centralizacion.domain.repository.HistoryPayRepository
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import com.tramites1cero1.centralizacion.domain.usecase.CreateTransactionUseCase
import com.tramites1cero1.centralizacion.domain.usecase.DownloadInvoiceUseCase
import com.tramites1cero1.centralizacion.ui.screen.taxpayments.components.DownloadEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


data class TaxResultsUiState(
    val taxes: List<Tax> = emptyList(),
    val paymentUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val fileToOpenUri: Uri? = null,
    val fileToShareUri: Uri? = null,

)

@HiltViewModel
class TaxResultsViewModel @Inject constructor(
    private val createTransactionUseCase: CreateTransactionUseCase,
    private val downloadInvoiceUseCase: DownloadInvoiceUseCase,
    private val authRepository: AuthRepository,
    private val historyPayRepository: HistoryPayRepository,
    private val userPreferencesRepository : UserPreferencesRepository,
) : ViewModel() {

    val municipalityId: StateFlow<Int> = userPreferencesRepository
        .getSavedUbication()
        .map { it.municipalityId }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _uiState = MutableStateFlow(TaxResultsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DownloadEventBus.events.collectLatest { uri ->
                _uiState.update { it.copy(fileToOpenUri = uri) }
            }
        }
    }

    fun setTaxes(taxes: List<Tax>) {
        _uiState.update { it.copy(taxes = taxes) }
    }

    private val _validationPostCreatePayment = MutableStateFlow<ValidationResponseDTO?>(null)
    val validationPostCreate: StateFlow<ValidationResponseDTO?> = _validationPostCreatePayment

    val user: StateFlow<UserDTO?> = authRepository.user
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = null
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun createHistoryPay(amount: Float,IDImpuesto:String, factura:String, codigoEntidad:String,
                         municipalityProceduresId: Int) {
        viewModelScope.launch {
            val statusType = 2
            try {
                val currentUser = user.value
                    val today = LocalDate.now()
                    val formattedDate = today.format(DateTimeFormatter.ISO_DATE)
                    val structure = CreatePaymentHistory(
                        userId = currentUser?.id,
                        amount = amount,
                        paymentDate = formattedDate,
                        status = false,
                        municipalityProceduresId = municipalityProceduresId,
                        statusType = statusType,
                        CodigoEntidad = codigoEntidad,
                        Factura = factura,
                        IDImpuesto = IDImpuesto,
                    );

                    val response = historyPayRepository.createHistoryPayment(structure)
                    _validationPostCreatePayment.value = response

            } catch (e: Exception) {
                _validationPostCreatePayment.value = ValidationResponseDTO(
                    booleanStatus = false,
                    sentencesError = "Tenemos problemas al registrar tu pago. Pero, fue hecho!"
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onPayClicked(tax: Tax, email: String) {
        if (tax.isExpired) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            createTransactionUseCase(tax, email)
                .onSuccess { paymentInfo ->

                    _uiState.update { it.copy(isLoading = false, paymentUrl = paymentInfo.url) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onOpenPdfClicked(tax: Tax) {
        if (tax.isExpired) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            downloadInvoiceUseCase(tax)
                .onSuccess { uri ->
                    _uiState.update { it.copy(isLoading = false, fileToOpenUri = uri) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSharePdfClicked(tax: Tax) {
        if (tax.isExpired) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            downloadInvoiceUseCase(tax)
                .onSuccess { uri ->
                    Log.d("ViewModel_Share", "Uri para compartir generada: $uri")
                    _uiState.update { it.copy(isLoading = false, fileToShareUri = uri) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onFileActionHandled() {
        _uiState.update { it.copy(fileToOpenUri = null, fileToShareUri = null) }
    }

    fun onFileOpened() {
        _uiState.update { it.copy(fileToOpenUri = null) }
    }

    fun onDownloadInvoiceClicked(tax: Tax) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            downloadInvoiceUseCase(tax)
                .onSuccess { uri ->
                    _uiState.update { it.copy(isLoading = false, fileToOpenUri = uri) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(paymentUrl = null, error = null) }
    }
}