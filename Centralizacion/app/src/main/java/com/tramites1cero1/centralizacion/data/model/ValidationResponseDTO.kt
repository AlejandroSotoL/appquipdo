package com.tramites1cero1.centralizacion.data.model

data class ValidationResponseDTO(
    val codeStatus: Int = 0,
    val booleanStatus: Boolean = false,
    val sentencesError: String = ""
)