package com.tramites1cero1.centralizacion.data.model

import com.google.gson.annotations.SerializedName

data class DocumentTypeDTO(
    val id: Int,
    @SerializedName("nameDocument") val name: String
)
