package com.tramites1cero1.tramiappquibdo.data.model

data class ValidationResponseExtraDto (
    val codeStatus: Int = 0,
    val booleanStatus: Boolean = false,
    val sentencesError: String = "",
    val extraData: String = ""
)