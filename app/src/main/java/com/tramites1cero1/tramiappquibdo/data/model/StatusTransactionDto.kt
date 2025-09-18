package com.tramites1cero1.tramiappquibdo.data.model

data class StatusTransactionDto(
    val TransactionID: Long,
    val CUS: String,
    val Factura: String,
    val Referencia: String,
    val Total: Double,
    val Impuesto: String,
    val FechaTransaccion: String,
    val EstadoTransaccion: String,
    val MedioPago: String
)
