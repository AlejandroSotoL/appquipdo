package com.tramites1cero1.centralizacion.ui.screen.taxpayments

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.tramites1cero1.centralizacion.data.model.Date
import com.tramites1cero1.centralizacion.data.model.UserDTO
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import com.tramites1cero1.centralizacion.data.model.historyPaymentDTOs.CreatePaymentHistory
import com.tramites1cero1.centralizacion.domain.model.QueryField
import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.domain.repository.AuthRepository
import com.tramites1cero1.centralizacion.domain.repository.HistoryPayRepository
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import com.tramites1cero1.centralizacion.domain.usecase.GetTaxesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import com.tramites1cero1.centralizacion.R
import java.time.LocalDate
import javax.inject.Inject

data class TaxQueryUiState(
    val title: String = "Consulta de Impuesto",
    val documentTypeOptions: List<QueryField> = emptyList(),
    val selectedQueryField: QueryField? = null,
    val taxIconResId: Int = R.drawable.icopredial,
    val documentNumber: String = "",
    val email: String = "",
    val dataPolicyUrl: String = "",
    val privacyPolicyUrl: String = "",
    val acceptsPolicies: Boolean = false,
    val acceptsConditions: Boolean = false,
    val isQueryButtonEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val queryError: String? = null,
    val isDropdownVisible: Boolean = false,
    val querySuccess: List<Tax>? = null,
    val showNoResultsDialog: Boolean = false,

)

@HiltViewModel
class TaxQueryViewModel @Inject constructor(
    private val getTaxesUseCase: GetTaxesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val taxId: Int = savedStateHandle.get<Int>("taxId") ?: -1
    private val entityCode: String = savedStateHandle.get<String>("entityCode") ?: ""


    private val _uiState = MutableStateFlow(TaxQueryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val title: String = URLDecoder.decode(savedStateHandle.get<String>("title") ?: "Impuesto", "UTF-8")
        val queryFieldsJson: String = URLDecoder.decode(savedStateHandle.get<String>("queryFieldsJson") ?: "[]", "UTF-8")
        val dataPolicyUrl: String = URLDecoder.decode(savedStateHandle.get<String>("dataPolicyUrl") ?: "", "UTF-8")
        val privacyPolicyUrl: String = URLDecoder.decode(savedStateHandle.get<String>("privacyPolicyUrl") ?: "", "UTF-8")

        val type = object : TypeToken<List<QueryField>>() {}.type
        val queryFields: List<QueryField> = Gson().fromJson(queryFieldsJson, type)

        val iconRes = when (taxId) {
            1 -> R.drawable.icopredial
            2 -> R.drawable.icoica
            else -> R.drawable.icopredial
        }
        _uiState.update { it.copy(
            title = title,
            documentTypeOptions = queryFields,
            taxIconResId = iconRes,
            dataPolicyUrl = dataPolicyUrl,
            privacyPolicyUrl = privacyPolicyUrl
        ) }
    }

    fun onDropdownVisibilityChanged(isVisible: Boolean) {
        _uiState.update { it.copy(isDropdownVisible = isVisible) }
    }

    fun onDocumentTypeChanged(value: QueryField) {
        _uiState.update { it.copy(selectedQueryField = value, isDropdownVisible = false) }
        validateInputs()
    }

    private fun validateInputs() {
        val currentState = _uiState.value
        val isFormValid = currentState.selectedQueryField != null &&
                currentState.documentNumber.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches() &&
                currentState.acceptsPolicies &&
                currentState.acceptsConditions
        _uiState.update { it.copy(isQueryButtonEnabled = isFormValid) }
    }

    fun onDocumentNumberChanged(value: String) {
        _uiState.update { it.copy(documentNumber = value) }
        validateInputs()
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value) }
        validateInputs()
    }

    fun onAcceptsPoliciesChanged(accepted: Boolean) {
        _uiState.update { it.copy(acceptsPolicies = accepted) }
        validateInputs()
    }

    fun onAcceptsConditionsChanged(accepted: Boolean) {
        _uiState.update { it.copy(acceptsConditions = accepted) }
        validateInputs()
    }

    fun onQueryHandled() {
        _uiState.update { it.copy(querySuccess = null, queryError = null) }
    }

    fun onQueryClicked() {
        if (!_uiState.value.isQueryButtonEnabled) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentState = _uiState.value
            getTaxesUseCase(
                entityCode = entityCode,
                queryData = currentState.documentNumber,
                queryField = currentState.selectedQueryField!!.fieldName,
                taxId = taxId
            ).onSuccess { taxes ->
                if (taxes.isNotEmpty()) {
                    // Si la lista tiene elementos, procedemos a la siguiente pantalla
                    _uiState.update { it.copy(isLoading = false, querySuccess = taxes) }
                } else {
                    // Si la lista está vacía, mostramos el diálogo de "sin resultados"
                    _uiState.update { it.copy(isLoading = false, showNoResultsDialog = true) }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, queryError = error.message) }
            }
        }
    }
    fun onNoResultsDialogDismissed() {
        _uiState.update { it.copy(showNoResultsDialog = false) }
    }
}