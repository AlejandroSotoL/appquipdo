package com.tramites1cero1.tramiappquibdo.data.model.historyPaymentDTOs

data class CreatePaymentHistory (
    val userId: Int?,
    val amount: Float,
    val paymentDate: String?,
    val status: Boolean,
    val municipalityProceduresId: Int,
    val statusType: Int,
    val CodigoEntidad: String,
    val Factura : String,
    val IDImpuesto: String
);