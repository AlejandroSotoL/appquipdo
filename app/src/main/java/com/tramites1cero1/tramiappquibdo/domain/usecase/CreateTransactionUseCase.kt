package com.tramites1cero1.tramiappquibdo.domain.usecase

import com.tramites1cero1.tramiappquibdo.domain.model.PaymentGatewayInfo
import com.tramites1cero1.tramiappquibdo.domain.model.Tax
import com.tramites1cero1.tramiappquibdo.domain.repository.TaxRepository

class CreateTransactionUseCase(private val taxRepository: TaxRepository) {
    suspend operator fun invoke(tax: Tax, email: String): Result<PaymentGatewayInfo> {
        return taxRepository.createTransaction(tax, email)
    }
}