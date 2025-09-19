package com.tramites1cero1.centralizacion.data.model.UserDTOs

data class UpdatePasswordRequestDto(
    val currentPassword: String,
    val newPassword: String
)