package com.tramites1cero1.tramiappquibdo.data.model

import com.google.gson.annotations.SerializedName

data class LoginDTO(
    @SerializedName("email")
    val email:String,
    @SerializedName("password")
    val password:String
)
