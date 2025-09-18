package com.tramites1cero1.tramiappquibdo.data.model.historyPaymentDTOs

data class PaymentHistoryDTO(
    val id: Int,
    val userFirtName: String,
    val amount: Double,
    val paymentDate: String,
    val status: Boolean,
    val idStatusType: Int,
    val municipalityName: String,
    val procedureName: String,
    val statusType: String,
    val idimpuesto:String,
    val factura:String,
    val codigoEntidad:String
)
typealias PaymentHistoryListDTO = List<PaymentHistoryDTO>
