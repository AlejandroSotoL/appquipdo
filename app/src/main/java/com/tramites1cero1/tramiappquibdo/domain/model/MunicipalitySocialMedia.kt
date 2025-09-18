package com.tramites1cero1.tramiappquibdo.domain.model

data class MunicipalitySocialMedia(
    val id: Int,
    val isActive: Boolean,
    val municipality: Municipality,
    val socialMediaType: SocialMediaType,
    val url: String
)