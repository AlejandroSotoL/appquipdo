package com.tramites1cero1.tramiappquibdo.data.repository

import android.util.Log
import com.tramites1cero1.tramiappquibdo.data.model.StatusTransactionDto
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.data.model.historyPaymentDTOs.CreatePaymentHistory
import com.tramites1cero1.tramiappquibdo.data.model.historyPaymentDTOs.PaymentHistoryListDTO
import com.tramites1cero1.tramiappquibdo.data.network.LoginRequest
import com.tramites1cero1.tramiappquibdo.data.network.PaymentHistory
import com.tramites1cero1.tramiappquibdo.data.network.StatusOfPayments
import com.tramites1cero1.tramiappquibdo.domain.repository.HistoryPayRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryPayRepositoryImpl @Inject constructor(
    private val paymentHistoryApi: PaymentHistory,
    private val statusOfPaymentsApi: StatusOfPayments
) : HistoryPayRepository {

    override suspend fun getHistoryPayByUser(id: Int): PaymentHistoryListDTO {
        require(id > 0) { "El ID del usuario debe ser mayor que cero" }
        return try {
            paymentHistoryApi.getHistoryPaymentByUser(id)
        } catch (e: Exception) {
            return emptyList()
        }
    }

    override suspend fun deleteHistoryByUser(
        idUser: Int, idHistory: Int
    ): ValidationResponseDTO {
        return try {
            paymentHistoryApi.deleteHistoryByUser(idUser, idHistory)
        } catch (e: Exception) {
            return ValidationResponseDTO(
                sentencesError = "Tenemos problemas, Intenta mas tarde",
                booleanStatus = false,
                codeStatus = 404
            );
        }
    }

    override suspend fun createHistoryPayment(paymentHistoryDTO: CreatePaymentHistory): ValidationResponseDTO {
        return try {
            paymentHistoryApi.createPaymentHistory(paymentHistoryDTO)
        } catch (e: Exception) {
            return ValidationResponseDTO(
                sentencesError = "Tenemos problemas para crear el historial del pago.",
                booleanStatus = false
            );
        }
    }

    override suspend fun getInformationAboutPayment(
        codigoEntidad: String,
        factura: String,
        idImpuesto: String
    ): StatusTransactionDto? {
        return try {
            val tokenResponse = statusOfPaymentsApi.authenticate(
                LoginRequest(username = "HLF26+ab0xEMPbPlEBe1cl2dNkVtolspdhG+3K3t6e8=", password = "HLF26+ab0xEMPbPlEBe1ci4JWvvUJsZagnsjbRJZWPn14keMkZIoEyomwLIE/oo+")
            )
            val token = tokenResponse.replace("\"", "")
            Log.d("Token Generado" , token)
            var generado = statusOfPaymentsApi.getStatusOfPayment(
                token = "Bearer $token",
                codigoEntidad = codigoEntidad,
                factura = factura,
                idImpuesto = idImpuesto
            )
            generado
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateHistoryStatus(
        idHistory: Int,
        newStatus: Int
    ): ValidationResponseDTO {
        return try {
            paymentHistoryApi.updateStatusHistory(idHistory, newStatus)
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false,
                sentencesError = e.message ?: "Tenemos problemas"
            )
        }
    }
}
