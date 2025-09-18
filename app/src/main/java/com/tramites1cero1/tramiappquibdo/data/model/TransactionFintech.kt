package com.tramites1cero1.tramiappquibdo.data.model

data class TransactionFintech(
    val idTramite: Int,
    val pagador: PayerDto,
    val fuentePago: Int,
    val tipoImplementacion: Int,
    val estado_Url: Boolean,
    val url: String,
    val valorPagar: Int,
    val factura: String,
    val referencia: String,
    val descripcion: String
)
