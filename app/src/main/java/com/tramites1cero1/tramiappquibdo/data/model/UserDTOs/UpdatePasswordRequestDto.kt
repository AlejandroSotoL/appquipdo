package com.tramites1cero1.tramiappquibdo.data.model.UserDTOs

data class UpdatePasswordRequestDto(
    val currentPassword: String,
    val newPassword: String
)