package com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.recoveryByForget

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.RecoveryPasswordUiState
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseExtraDto
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.SendEmailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class RecoveryPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sendEmailRepository: SendEmailRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf(RecoveryPasswordUiState())
    val uiState: State<RecoveryPasswordUiState> = _uiState

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            emailError = false,
            errorMessage = null
        )
    }

    fun recoveryPassword() {
        val email = _uiState.value.email
        Log.d("RecoveryPasswordVM", "Se llamó recoveryPassword con: $email")
        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(emailError = true)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, errorMessage = null)
            try {
                val sendCodeResult = withTimeout(10000L) {
                    sendEmailRepository.sendEmailVeryficatedCode(email)
                }

                if (!sendCodeResult.booleanStatus) {
                    _uiState.value = _uiState.value.copy(
                        sendCodeResponse = sendCodeResult,
                        showCodeSheet = false,
                        errorMessage = sendCodeResult.sentencesError
                    )
                    return@launch
                }

                val validationCode = sendCodeResult.extraData
                _uiState.value = _uiState.value.copy(
                    sendCodeResponse = sendCodeResult,
                    showCodeSheet = true,
                    codeValidationResponse = ValidationResponseExtraDto(
                        extraData =  validationCode
                    )
                )

            } catch (ex: TimeoutCancellationException) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "El servidor tardó demasiado en responder. Intenta nuevamente más tarde.",
                    showCodeSheet = false
                )
            } catch (ex: Exception) {
                authRepository.clearUserSession()
                _uiState.value = _uiState.value.copy(
                    sendCodeResponse = ValidationResponseExtraDto(
                        booleanStatus = false,
                        sentencesError = "Tenemos problemas de red: ${ex.message}"
                    ),
                    showCodeSheet = false
                )
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun onModalCloseWithoutCompleting(){
        try{
            viewModelScope.launch {
                authRepository.clearUserSession()
            }
        }catch (e: Exception){
            _uiState.value = _uiState.value.copy(
                sendCodeResponse = ValidationResponseExtraDto(
                    booleanStatus = false,
                    sentencesError = "Tenemos problemas de red: ${e.message}"
                ),
                showCodeSheet = false
            )
        }
    }


    fun validateCode(code: String) {
        val email = _uiState.value.email
        val backendCode = _uiState.value.sendCodeResponse?.extraData
        if (backendCode == null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loading = true,
                errorMessage = null,
                isCodeError = false
            )

            try {
                if (code != backendCode) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "El código ingresado no es correcto.",
                        isCodeError = true,
                        attempts = _uiState.value.attempts + 1,
                        loading = false
                    )
                    return@launch
                }

                val result = sendEmailRepository.verifyCode(code, backendCode)
                if (result.booleanStatus) {
                    val userInfoResult = authRepository.getUserInformationByEmail(email)
                    userInfoResult.onSuccess { userData ->
                        val changedStatus = authRepository.getOutUser(userData.id, true)
                        if (!changedStatus.booleanStatus) {
                            _uiState.value = _uiState.value.copy(
                                sendCodeResponse = ValidationResponseExtraDto(
                                    booleanStatus = false,
                                    sentencesError = "Tenemos problemas con tu cambio de Estado"
                                ),
                                showCodeSheet = false
                            )
                            return@onSuccess
                        }
                        authRepository.saveUserSession(userData)
                        _uiState.value = _uiState.value.copy(
                            sendCodeResponse = result,
                            showCodeSheet = false,
                            navigate = true
                        )
                    }.onFailure { ex ->
                        authRepository.clearUserSession()
                        _uiState.value = _uiState.value.copy(
                            sendCodeResponse = ValidationResponseExtraDto(
                                booleanStatus = false,
                                sentencesError = "Tenemos problemas con tu información: ${ex.message}"
                            ),
                            showCodeSheet = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        sendCodeResponse = result,
                        errorMessage = result.sentencesError,
                        isCodeError = true
                    )
                }

                _uiState.value = _uiState.value.copy(
                    codeValidationResponse = result
                )
            } catch (ex: Exception) {
                authRepository.clearUserSession()
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al validar código: ${ex.message}",
                    isCodeError = true
                )
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(navigate = false)
    }
}
