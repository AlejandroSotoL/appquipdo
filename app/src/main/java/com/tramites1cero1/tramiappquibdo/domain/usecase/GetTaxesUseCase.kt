package com.tramites1cero1.tramiappquibdo.domain.usecase

import com.tramites1cero1.tramiappquibdo.domain.repository.TaxRepository

class GetTaxesUseCase(private val taxRepository: TaxRepository) {
    suspend operator fun invoke(entityCode: String, queryData: String, queryField: String, taxId: Int) =
        taxRepository.getTaxes(entityCode, queryData, queryField, taxId)
}