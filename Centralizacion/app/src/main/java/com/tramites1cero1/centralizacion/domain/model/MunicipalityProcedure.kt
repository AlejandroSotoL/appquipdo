package com.tramites1cero1.centralizacion.domain.model

data class MunicipalityProcedure(
    val id: Int,
    val integrationType: String,
    val isActive: Boolean,
    val municipality: Municipality,
    val procedures: Procedures
)