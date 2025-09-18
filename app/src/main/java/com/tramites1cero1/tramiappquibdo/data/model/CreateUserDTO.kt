package com.tramites1cero1.tramiappquibdo.data.model

data class CreateUserDTO(
    val id: Int = 0,
    val firstName: String = "",
    val middleName: String? = null,
    val lastName: String = "",
    val secondLastName: String? = null,
    val documentTypeId: Int = 0,
    val nationalId: String = "",
    val email: String = "",
    val password: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val birthDate: String = "",
    val loginStatus: Int? = null,
)