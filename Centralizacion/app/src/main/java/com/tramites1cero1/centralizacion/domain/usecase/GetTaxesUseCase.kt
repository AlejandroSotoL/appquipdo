package com.tramites1cero1.centralizacion.domain.usecase

import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.domain.repository.TaxRepository

class GetTaxesUseCase(private val taxRepository: TaxRepository) {
    suspend operator fun invoke(entityCode: String, queryData: String, queryField: String, taxId: Int) =
        taxRepository.getTaxes(entityCode, queryData, queryField, taxId)
}