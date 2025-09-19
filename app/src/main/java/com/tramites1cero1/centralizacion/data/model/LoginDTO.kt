package com.tramites1cero1.centralizacion.data.model

import com.google.gson.annotations.SerializedName

data class LoginDTO(
    @SerializedName("email")
    val email:String,
    @SerializedName("password")
    val password:String
)
