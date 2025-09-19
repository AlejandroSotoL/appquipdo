package com.tramites1cero1.centralizacion.data.model

import com.google.gson.annotations.SerializedName

data class BankDTO(
    @SerializedName("nameBank")
    val nameBank: String
)