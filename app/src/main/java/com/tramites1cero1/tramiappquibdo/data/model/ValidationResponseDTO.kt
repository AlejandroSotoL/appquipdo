package com.tramites1cero1.tramiappquibdo.data.model

data class ValidationResponseDTO(
    val codeStatus: Int = 0,
    val booleanStatus: Boolean = false,
    val sentencesError: String = ""
)