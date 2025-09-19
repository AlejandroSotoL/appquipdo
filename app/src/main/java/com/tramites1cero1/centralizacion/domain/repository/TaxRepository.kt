package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.domain.model.PaymentGatewayInfo

interface TaxRepository {
    suspend fun getTaxes(entityCode: String, queryData: String, queryField: String, taxId: Int): Result<List<Tax>>
    suspend fun createTransaction(tax: Tax, email: String): Result<PaymentGatewayInfo>
    suspend fun getInvoicePdfUrl(tax: Tax): Result<String>
}