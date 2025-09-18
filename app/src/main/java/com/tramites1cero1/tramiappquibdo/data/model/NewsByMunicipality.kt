package com.tramites1cero1.tramiappquibdo.data.model

import com.google.gson.annotations.SerializedName

data class NewsByMunicipality (
    @SerializedName("getUrlNew")
    val url: String,
    @SerializedName("idMunicipality")
    val idMunicipality : Int,

)