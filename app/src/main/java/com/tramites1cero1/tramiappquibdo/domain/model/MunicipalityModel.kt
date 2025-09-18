package com.tramites1cero1.tramiappquibdo.domain.model

import androidx.compose.ui.graphics.Color
data class MunicipalityModel(
    val idMunicipio: Int,
    val codigoEntidad: String,
    val nombreMunicipio: String,
    val departamento: String,
    val design: Design,
    val tipoIntegracion: IntegrationTypeModel,
    val domain: String,
    val bank: String,
    val privacyPolicyUrl: String,
    val dataPolicyUrl: String,
    val newsUrl: String,
    val tramitesPrincipales: List<InfoTramite> = emptyList(),
    val otrosTramites: List<InfoTramite> = emptyList(),
    val socialLinks: List<InfoTramite> = emptyList(),
    val municipalityProcedures: List<MunicipalityProcedure> = emptyList()
)


data class Design(
    val NombreAlcaldia: String = "TramiApp",
    val escudoUrl: String = "",
    val primaryColor: Color = Color(0xFF007AFF),
    val secondaryColor: Color = Color(0xFF007AFF),
    val secondaryColorDark: Color = Color.White,
    val onPrimaryColorLight : Color = Color.White,
    val onPrimaryColorDark : Color = Color.White
)
