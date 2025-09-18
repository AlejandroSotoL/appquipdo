package com.tramites1cero1.tramiappquibdo.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

import kotlinx.parcelize.Parcelize

@Parcelize
data class QueryField(
    @SerializedName("id") val id: Int,
    @SerializedName("queryFieldType") val queryFieldType: String,
    @SerializedName("fieldName") val fieldName: String,
    @SerializedName("municipality") val municipality: Municipality? = null
) : Parcelable