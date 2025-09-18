package com.tramites1cero1.tramiappquibdo.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.LoginDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLogginIn: Boolean = false,
    val response: ValidationResponseDTO? = null

)

sealed interface AuthEvents {
    data object GoBack : AuthEvents
    data object OnLogin : AuthEvents

}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken

    fun loadAuthToken() {
        viewModelScope.launch {
            _authToken.value = userPreferences.getAuthToken()
        }
    }

    private val _eventFlow = MutableSharedFlow<AuthEvents>()
    val eventFlow = _eventFlow.asSharedFlow()

    //Informacion del usuarion cuando las PREFERENCES estan guardadas
    val user = authRepository.user.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun GoBackToOptions() {
        viewModelScope.launch {
            _eventFlow.emit(AuthEvents.GoBack)
        }
    }
    //--------------------------Auth-----------------------------
    fun authenticate(email: String, password: String) {
        if (!isValidEmail(email)) {
            _uiState.update {
                it.copy(
                    response = ValidationResponseDTO(
                        codeStatus = 400,
                        sentencesError = "Correo no válido",
                        booleanStatus = false
                    )
                )
            }
            return
        }
        if (!isValidPassword(password)) {
            _uiState.update {
                it.copy(
                    response = ValidationResponseDTO(
                        codeStatus = 400,
                        sentencesError = "La contraseña debe tener al menos 5 caracteres",
                        booleanStatus = false
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val loginDto = LoginDTO(email, password)
                val loginResult = authRepository.login(loginDto)
                if (loginResult.booleanStatus) {
                    authRepository.getUserInformationByEmail(email)
                        .onSuccess { user ->
                            authRepository.saveUserSession(user)
//                            if(loginResult.booleanStatus && !loginResult.sentencesError.isEmpty()){
//                                userPreferences.saveAuthToken(loginResult.sentencesError);
//                            }else{
//                                _uiState.update {
//                                    it.copy(
//                                        response = ValidationResponseDTO(sentencesError = "Tenemos problemas con tu inicio de sesión", booleanStatus = false),
//                                    )
//                                }
//                            }
                            _uiState.update {
                                it.copy(
                                    response = ValidationResponseDTO(
                                        sentencesError = "Inicio de sesión exitoso",
                                        booleanStatus = true
                                    ),
                                    isLogginIn = true
                                )
                            }
                            _eventFlow.emit(AuthEvents.OnLogin)
                        }
                        .onFailure {
                            _uiState.update {
                                it.copy(
                                    response = ValidationResponseDTO(sentencesError = "Usuario no encontrado",),
                                )
                            }
                        }

                } else {
                    _uiState.update {
                        it.copy(
                            response = loginResult
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        response = ValidationResponseDTO(
                            sentencesError = "No se puedo iniciar sesión, Intente Nuevamente",
                        )
                    )
                }
                e.printStackTrace()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    suspend fun getToken(): String? = userPreferences.getAuthToken()

     suspend fun onDeleteUser(id: Int): ValidationResponseDTO {
        if (id <= 0) {
            return ValidationResponseDTO(
                booleanStatus = false,
                sentencesError = "ID inválido"
            )
        }

        return try {
            val response = authRepository.deleteAccount(id)
            response
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false,
                sentencesError = "Error al eliminar el usuario: ${e.message}"
            )
        }
    }

    fun clearuserPreferences(){
        viewModelScope.launch {
            authRepository.clearUserSession()
        }
    }

    fun clearResponse() {
        _uiState.update { it.copy(response = null) }
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        if(password.length < 5){
            return false
        }
        return true
    }

    //------------------------PREFERENCES--------------------------
    suspend fun clearUserData(id: Int, status: Boolean): ValidationResponseDTO {
        val response = authRepository.getOutUser(id, status)
        if (response.booleanStatus) {
            authRepository.clearUserSession()

        }
        return response
    }
}