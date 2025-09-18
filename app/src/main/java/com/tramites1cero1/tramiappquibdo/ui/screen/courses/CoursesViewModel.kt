package com.tramites1cero1.tramiappquibdo.ui.screen.courses

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.repository.CourseRepositoryImpl
import com.tramites1cero1.tramiappquibdo.domain.model.Course
import com.tramites1cero1.tramiappquibdo.domain.model.CourseRegistration
import com.tramites1cero1.tramiappquibdo.domain.repository.CourseRepository
import com.tramites1cero1.tramiappquibdo.domain.usecase.GetCoursesUseCase
import com.tramites1cero1.tramiappquibdo.domain.usecase.RegisterForCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

data class CoursesUiState(
    val isLoading: Boolean = true,
    val courses: List<Course> = emptyList(),
    val error: String? = null,
    val selectedCourse: Course? = null,
    val registrationSuccess: Boolean = false,
    val dismiss: Boolean = false
)
data class fieldConfig(
    val value: String,
    val label: String,
    val onValueChange: (String) -> Unit,
    val icon: ImageVector,
    val contentDescription: String,
    val keyboardType: KeyboardType,
    val readOnly: Boolean = true,
    val errorMessage: String?

)

data class RegistrationFormState(
    val documentNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val age: String = "",
    val email: String = "",
    val phone: String = "",
    val hasAcceptedTerms: Boolean = false,
    val hasAcceptedPrivacyPolicy: Boolean = false,
    val documentNumberError: String? = null,
    val ageError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val termsError: String? = null,
    val isEditable: Boolean = true
)



@HiltViewModel
class CoursesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(RegistrationFormState())
    val formState = _formState.asStateFlow()

    //  Obtenemos las URLs dinámicas que pasamos por navegación
    private val getUrl: String = URLDecoder.decode(savedStateHandle.get<String>("getUrl") ?: "", "UTF-8")
    private val postUrl: String = URLDecoder.decode(savedStateHandle.get<String>("postUrl") ?: "", "UTF-8")

    //  Creamos manualmente las dependencias usando las URLs dinámicas
    private val courseRepository: CourseRepository = CourseRepositoryImpl(getUrl, postUrl)
    private val getCoursesUseCase: GetCoursesUseCase = GetCoursesUseCase(courseRepository)
    private val registerForCourseUseCase: RegisterForCourseUseCase = RegisterForCourseUseCase(courseRepository)

    init {
        loadCourses()
    }

    fun initForm(user: UserDTO?) {
        viewModelScope.launch {
            val safeDocumentType = user?.nationalId.orEmpty()
            val edadCalculada = user?.birthDate?.let { fechaNacimiento ->
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val fecha = sdf.parse(fechaNacimiento)

                    if (fecha != null) {
                        val hoy = Calendar.getInstance()
                        val nacimiento = Calendar.getInstance().apply { time = fecha }

                        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
                        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                            edad--
                        }
                        edad.toString()
                    } else {
                        ""
                    }
                } catch (e: Exception) {
                    ""
                }
            } ?: ""
            _formState.value = RegistrationFormState(
                documentNumber = safeDocumentType,
                firstName = user?.firstName.orEmpty(),
                lastName = user?.lastName.orEmpty(),
                age = edadCalculada,
                email = user?.email.orEmpty(),
                phone = user?.phoneNumber.orEmpty(),
                isEditable = user == null
            )
        }
    }


    private fun loadCourses() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            getCoursesUseCase().onSuccess { courses ->
                _uiState.value = _uiState.value.copy(isLoading = false, courses = courses)
                }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = error.message)
            }
        }
    }

    fun onRegisterClick(course: Course) {
            _uiState.update {
                it.copy(selectedCourse = course)
            }

    }

    fun onDialogDismiss() {
        _uiState.update {
            it.copy(selectedCourse = null, registrationSuccess = false, dismiss = true)
        }
        _formState.value = RegistrationFormState() // Limpiar formulario
    }

    // Esta función la podemos simplificar para que el ViewModel tenga más control
    fun updateFormField(update: (RegistrationFormState) -> RegistrationFormState) {
        _formState.update {
            update(it)
        }
    }

    fun submitRegistration() {
        if (validateInput()){
         val course = _uiState.value.selectedCourse ?: return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            val registration = CourseRegistration(
                courseId = course.id,
                courseTitle = course.title,
                documentNumber = _formState.value.documentNumber,
                firstName = _formState.value.firstName,
                lastName = _formState.value.lastName,
                age = _formState.value.age,
                email = _formState.value.email,
                phone = _formState.value.phone
            )

            registerForCourseUseCase(registration).onSuccess {
                _uiState.update {
                    it.copy(isLoading = false, registrationSuccess = true)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, error = error.message)
                }
            }
        }
            }
    }

    fun onFirstnameChanged(value: String) {
        _formState.update { it.copy(firstName = value, firstNameError = null) }
    }
    fun onLastnameChanged(value: String) {
        _formState.update { it.copy(lastName = value, lastNameError = null) }
    }
    fun onAgeChanged(value: String){
        _formState.update { it.copy(age = value, ageError = null) }
    }

    fun onEmailChanged(value: String) {
        _formState.update { it.copy(email = value, emailError = null) }
    }
    fun onPhoneChanged(value: String) {
        _formState.update { it.copy(phone = value, phoneError = null) }
    }

    fun onDocumentNumberChanged(value: String) {
        _formState.update { it.copy(documentNumber = value, documentNumberError = null) }
    }

    fun onTermsAccepted(accepted: Boolean) {
        _formState.update { it.copy(hasAcceptedTerms = accepted, termsError = null) }
    }

    fun onPrivacyPolicyAccepted(accepted: Boolean) {
        _formState.update { it.copy(hasAcceptedPrivacyPolicy = accepted, termsError = null) }
    }
    private fun validateInput(): Boolean {

        if (_formState.value.documentNumber.isBlank()) {

            _formState.update { it.copy(documentNumberError = "El número de documento es requerido") }
           return false
        }else if (!_formState.value.documentNumber.any(){ it.isDigit() } ) {
            _formState.update { it.copy(documentNumberError = "El documento debe contener numeros") }
            return false
        }
        if (_formState.value.age.isBlank()) {
            _formState.update { it.copy(ageError = "La edad es requerida") }
            return false
        }

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

        if (_formState.value.email.isBlank()) {
            _formState.update { it.copy(emailError = "El email es requerido") }
            return false
        }else if (!_formState.value.email.contains("@")) {
            _formState.update { it.copy(emailError = "El email no es válido") }
            return false
        }
        if (_formState.value.phone.isBlank()) {
            _formState.update { it.copy(phoneError = "El teléfono es requerido") }
            return false
        }else if (!_formState.value.phone.all { it.isDigit()}) {
            _formState.update { it.copy(phoneError = "El teléfono solo puede contener números") }
            return false
        }else if (_formState.value.phone.length < 7 || _formState.value.phone.length > 15) {
            _formState.update { it.copy(phoneError = "El teléfono debe tener entre 7 y 15 dígitos") }
            return false
        }
        if (!_formState.value.hasAcceptedTerms || !_formState.value.hasAcceptedPrivacyPolicy) {
            _formState.update { it.copy(termsError = "Debes aceptar los términos y políticas.") }
            return false
        }
        return true

    }

}