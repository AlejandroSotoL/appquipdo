package com.tramites1cero1.centralizacion.domain.usecase

import com.tramites1cero1.centralizacion.domain.model.PaymentGatewayInfo
import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.domain.repository.TaxRepository

class CreateTransactionUseCase(private val taxRepository: TaxRepository) {
    suspend operator fun invoke(tax: Tax, email: String): Result<PaymentGatewayInfo> {
        return taxRepository.createTransaction(tax, email)
    }
}