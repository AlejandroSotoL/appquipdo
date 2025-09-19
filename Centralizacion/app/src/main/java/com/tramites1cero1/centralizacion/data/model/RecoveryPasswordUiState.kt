package com.tramites1cero1.centralizacion.data.model

data class RecoveryPasswordUiState(
    val loading: Boolean = false,
    val emailError: Boolean = false,
    val email: String = "",
    val sendCodeResponse: ValidationResponseExtraDto? = null,
    val codeValidationResponse: ValidationResponseExtraDto? = null,
    val showCodeSheet: Boolean = false,
    val navigate: Boolean = false,
    val errorMessage: String? = null,
    val verificationCode: String = "",
    val isCodeError: Boolean = false,
    val attempts: Int = 0,
    val numbersOfRequests:Int = 2,
    val isBloquedBotton:Boolean = false,
)
