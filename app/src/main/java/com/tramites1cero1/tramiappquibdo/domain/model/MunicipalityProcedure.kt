package com.tramites1cero1.tramiappquibdo.domain.model

data class MunicipalityProcedure(
    val id: Int,
    val integrationType: String,
    val isActive: Boolean,
    val municipality: Municipality,
    val procedures: Procedures
)