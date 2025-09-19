package com.tramites1cero1.centralizacion.domain.model

data class MunicipalitySocialMedia(
    val id: Int,
    val isActive: Boolean,
    val municipality: Municipality,
    val socialMediaType: SocialMediaType,
    val url: String
)