package com.tramites1cero1.centralizacion.data.model

data class PayerDto (
    val documento: String,
    val tipoDocumento: Int,
    val nombre_Completo: String?,
    val dv: Int,
    val primernombre: String,
    val segundonombre: String,
    val primerapellido: String,
    val segundoapellido: String,
    val telefono: String,
    val email: String,
    val direccion: String
)

