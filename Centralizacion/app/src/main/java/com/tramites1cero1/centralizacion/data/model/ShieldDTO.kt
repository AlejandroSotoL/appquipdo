package com.tramites1cero1.centralizacion.data.model

import com.google.gson.annotations.SerializedName

data class ShieldDTO(
    @SerializedName("nameOfMunicipality")
    val municipalityName: String,
    @SerializedName("url")
    val url : String,
)