package com.tramites1cero1.tramiappquibdo.ui.screen.venues

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.DocumentTypeDTO
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.repository.VenueRepositoryImpl
import com.tramites1cero1.tramiappquibdo.domain.model.Reservation
import com.tramites1cero1.tramiappquibdo.domain.model.Venue
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.VenueRepository
import com.tramites1cero1.tramiappquibdo.domain.usecase.CreateReservationUseCase
import com.tramites1cero1.tramiappquibdo.domain.usecase.GetVenuesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject
import kotlin.collections.orEmpty

data class VenuesUiState(
    val isLoading: Boolean = true,
    val venues: List<Venue> = emptyList(),
    val error: String? = null,
    val selectedVenue: Venue? = null,
    val reservationSuccess: Boolean = false
)

data class ReservationFormState(
    val firstName: String = "",
    val lastName: String = "",
    val documentType: String = "Cédula de Ciudadanía",
    val documentNumber: String = "",
    val email: String = "",
    val date: String = "",
    val time: String = "",
    val isEditable: Boolean = false,
    val documentTypes: List<String> = emptyList(),
    val selectedDocumentType: String = "",
    val isDropdownExpanded : Boolean = false,
    val documentTypeError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val documentNumberError: String? = null,
    val emailError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null,
    val hasAcceptedTerms: Boolean = false,
    val hasAcceptedPrivacyPolicy: Boolean = false,
    val termsError: String? = null,

    )

@HiltViewModel
class VenuesViewModel  @Inject constructor(
    savedStateHandle: SavedStateHandle,
   private val authRepository: AuthRepository
) : ViewModel() {
    private val getUrl: String = URLDecoder.decode(savedStateHandle.get<String>("getUrl") ?: "", "UTF-8")
    private val postReservationUrl: String = URLDecoder.decode(savedStateHandle.get<String>("postUrlReservation") ?: "", "UTF-8")
    private val postCalendarUrl: String = URLDecoder.decode(savedStateHandle.get<String>("postUrlCalendar") ?: "", "UTF-8")
    private val venueRepository: VenueRepository = VenueRepositoryImpl(getUrl, postReservationUrl,postCalendarUrl)
    private val getVenusUseCase: GetVenuesUseCase = GetVenuesUseCase(venueRepository)
    private val createReservationUseCase: CreateReservationUseCase = CreateReservationUseCase(venueRepository)
    //  Creamos manualmente las dependencias usando las URLs dinámicas
    var uiState by mutableStateOf(VenuesUiState())
        private set

    private val _formState = MutableStateFlow(ReservationFormState())
        val formState = _formState.asStateFlow()

    private val _documentTypes = MutableStateFlow<List<DocumentTypeDTO>>(emptyList())
    val documentTypes: StateFlow<List<DocumentTypeDTO>> = _documentTypes

    init {
        loadVenues()
    }

    fun initForm(user: UserDTO?) {
        viewModelScope.launch {

            if(authRepository.getTypeDocuments()!!.isEmpty()){
                println("We have problems with Api information about Documents types.");
            }else{
                val result = authRepository.getTypeDocuments().orEmpty()
                _documentTypes.value = result
                _formState.update {
                    it.copy(
                        documentTypes = result.map { doc -> doc?.name ?: "Undefined" }
                    )
                }
            }
            _formState.value = ReservationFormState(
                firstName = user?.firstName.orEmpty(),
                lastName = user?.lastName.orEmpty(),
                documentType = (user?.documentType?.name ?: "").toString(),
                documentNumber = user?.nationalId.orEmpty(),
                email = user?.email.orEmpty(),
                date = "",
                time = "",
                isEditable = user == null
            )
        }
    }

    fun loadVenues() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)
            getVenusUseCase().onSuccess { venues ->
                uiState = uiState.copy(isLoading = false, venues = venues)
            }.onFailure { error ->
                uiState = uiState.copy(isLoading = false, error = error.message)
            }
        }
    }

    fun onReserveClick(venue: Venue) {
        uiState = uiState.copy(selectedVenue = venue)
    }

    fun onDismissBottomSheet() {
        uiState = uiState.copy(selectedVenue = null, reservationSuccess = false)
        _formState.value = ReservationFormState()
    }

    fun onFirstNameChange(newValue: String) {
        _formState.update { it.copy(firstName = newValue, firstNameError = null ) }
    }

    fun onLastNameChange(newValue: String) {
        _formState.update { it.copy(lastName = newValue, lastNameError = null ) }
    }

    fun onDocumentTypeChange(newValue: String) {
        _formState.update { it.copy(documentType = newValue, documentTypeError = null ) }
    }

    fun onDocumentNumberChange(newValue: String) {
        _formState.update { it.copy(documentNumber = newValue, documentNumberError = null ) }
    }

    fun onEmailChange(newValue: String) {
        _formState.update { it.copy(email = newValue, emailError = null ) }
    }

    fun onDateChange(newValue: String) {
       _formState.update { it.copy(date = newValue, dateError = null ) }
    }

    fun onTimeChange(newValue: String) {
        _formState.update { it.copy(time = newValue, timeError = null ) }
    }
    fun onTermsAccepted(accepted: Boolean) {
        _formState.update { it.copy(hasAcceptedTerms = accepted, termsError = null) }
    }

    fun onPrivacyPolicyAccepted(accepted: Boolean) {
        _formState.update { it.copy(hasAcceptedPrivacyPolicy = accepted, termsError = null) }
    }

    fun submitReservation() {
        if (validateInput()) {
            val venue = uiState.selectedVenue ?: return
            viewModelScope.launch {
                uiState = uiState.copy(isLoading = true, error = null)

                val reservation = Reservation(
                    venueId = venue.id,
                    venueTitle = venue.title,
                    firstName = formState.value.firstName,
                    lastName = formState.value.lastName,
                    documentType = formState.value.documentType,
                    documentNumber = formState.value.documentNumber,
                    email = formState.value.email,
                    date = formState.value.date,
                    time = formState.value.time
                )
                createReservationUseCase(reservation).onSuccess {
                    uiState = uiState.copy(isLoading = false, reservationSuccess = true)
                }.onFailure { error ->
                    uiState = uiState.copy(isLoading = false, error = error.message)
                }
            }
        }

    }
    private fun validateInput(): Boolean {
        if (_formState.value.firstName.isBlank()) {
            _formState.update { it.copy(firstNameError = "El primer nombre es requerido") }
            return false
        }else if (_formState.value.firstName.length > 100) {
            _formState.update { it.copy(firstNameError = "El primer nombre no puede tener más de 100 caracteres") }
            return false

        }else if (!_formState.value.firstName.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"))) {
            _formState.update { it.copy(firstNameError = "El primer nombre solo puede contener letras") }
            return false
        }
        if (_formState.value.lastName.isBlank()) {
            _formState.update { it.copy(lastNameError = "El primer apellido es requerido") }
            return false
        }else if(_formState.value.lastName.length > 100) {
            _formState.update { it.copy(lastNameError = "El primer apellido no puede tener más de 100 caracteres") }
            return false
        }else if (!_formState.value.lastName.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+$"))){
            _formState.update { it.copy(lastNameError = "El primer apellido solo puede contener letras") }
            return false
        }
        if (_formState.value.documentNumber.isBlank()) {
            _formState.update { it.copy(documentNumberError = "El número de documento es requerido") }
            return false
        }else if (_formState.value.documentNumber.length < 10) {
            _formState.update { it.copy(documentNumberError = "El número de documento debe tener al menos 9 digitos") }
            return false
        }else if (!_formState.value.documentNumber.any() { it.isDigit()}) {
            _formState.update { it.copy(documentNumberError = "El número de documento debe contener números") }
            return false
        }
        if (_formState.value.documentType.isBlank()) {
            _formState.update { it.copy(documentTypeError = "El tipo de documento es requerido") }
            return false
        }
        if (_formState.value.email.isBlank()) {
            _formState.update { it.copy(emailError = "El email es requerido") }
            return false
        }else if (!_formState.value.email.contains("@")) {
            _formState.update { it.copy(emailError = "El email no es válido") }
            return false

        }
        if (_formState.value.date.isBlank()) {
            _formState.update { it.copy(dateError = "La fecha es requerida") }
            return false
        }
        if (_formState.value.time.isBlank()) {
            _formState.update { it.copy(timeError = "La hora es requerida") }
            return false
        }

        if (!_formState.value.hasAcceptedTerms || !_formState.value.hasAcceptedPrivacyPolicy) {
            _formState.update { it.copy(termsError = "Debes aceptar los términos y políticas.") }
            return false
        }
        return true

    }
}