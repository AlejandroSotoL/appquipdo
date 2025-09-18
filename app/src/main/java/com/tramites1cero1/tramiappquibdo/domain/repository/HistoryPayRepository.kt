package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.data.model.StatusTransactionDto
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.data.model.historyPaymentDTOs.CreatePaymentHistory
import com.tramites1cero1.tramiappquibdo.data.model.historyPaymentDTOs.PaymentHistoryListDTO

interface HistoryPayRepository {
    suspend fun getHistoryPayByUser(id: Int) : PaymentHistoryListDTO
    suspend fun deleteHistoryByUser(idUser: Int , idHistory:Int ) : ValidationResponseDTO
    suspend fun createHistoryPayment(paymentHistoryDTO: CreatePaymentHistory): ValidationResponseDTO
    suspend fun getInformationAboutPayment(CodigoEntidad:String , Factura:String , IDImpuesto:String) : StatusTransactionDto?
    suspend fun updateHistoryStatus(idHistory: Int, newStatus: Int): ValidationResponseDTO
}