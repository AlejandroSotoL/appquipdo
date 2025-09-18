package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.domain.model.Tax
import com.tramites1cero1.tramiappquibdo.domain.model.PaymentGatewayInfo

interface TaxRepository {
    suspend fun getTaxes(entityCode: String, queryData: String, queryField: String, taxId: Int): Result<List<Tax>>
    suspend fun createTransaction(tax: Tax, email: String): Result<PaymentGatewayInfo>
    suspend fun getInvoicePdfUrl(tax: Tax): Result<String>
}