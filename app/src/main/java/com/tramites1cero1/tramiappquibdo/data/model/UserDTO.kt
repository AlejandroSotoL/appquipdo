package com.tramites1cero1.tramiappquibdo.data.model

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("id")
    val id: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("documentType")
    val documentType: DocumentTypeDTO,
    @SerializedName("documentTypeId")
    val documentTypeId: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("loginStatus")
    val loginStatus: Boolean,
    @SerializedName("middleName")
    val middleName: String? = null,
    @SerializedName("nationalId")
    val nationalId: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("secondLastName")
    val secondLastName: String? = null,
    @SerializedName("birthDate")
    val birthDate: String
)

