package com.tramites1cero1.centralizacion.data.repository

import com.tramites1cero1.centralizacion.data.model.BancolombiaGatewayRequestDTO
import com.tramites1cero1.centralizacion.data.model.BancolombiaGatewayResponseDTO
import com.tramites1cero1.centralizacion.data.model.TaxInfoDTO
import com.tramites1cero1.centralizacion.data.model.TaxQueryRequestDTO
import com.tramites1cero1.centralizacion.data.network.PaymentApiService
import com.tramites1cero1.centralizacion.data.network.TaxApiService
import com.tramites1cero1.centralizacion.domain.model.PaymentGatewayInfo
import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.domain.repository.TaxRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TaxRepositoryImpl(
    private val taxApiService: TaxApiService,
    private val paymentApiService: PaymentApiService
) : TaxRepository {

    override suspend fun getTaxes(entityCode: String, queryData: String, queryField: String, taxId: Int): Result<List<Tax>> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = TaxQueryRequestDTO(
                    entityCode = entityCode,
                    queryData = queryData,
                    queryField = queryField,
                    taxId = taxId,
                    invoice = ""
                )
                // Usa la dependencia del constructor
                val response = taxApiService.getTaxes(requestBody)
                if (response.isSuccessful && response.body() != null) {val informationList = response.body()!!.information
                    if (informationList != null) {
                        val domainTaxes = informationList.map { it.toDomain() }
                        Result.success(domainTaxes)
                    } else {
                        Result.success(emptyList())
                    }
                } else {
                    Result.failure(Exception("Error al obtener impuestos: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun createTransaction(tax: Tax, email: String): Result<PaymentGatewayInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = BancolombiaGatewayRequestDTO(
                    reference = tax.reference,
                    invoice = tax.reference,
                    municipalityCode = tax.entityCode,
                    documentType = "CC",
                    identification = tax.document,
                    name = tax.name,
                    total = tax.value,
                    taxId = tax.taxId,
                    email = email,
                    phone = "",
                    paymentSource = 1,
                    implementationType = 1
                )
                // Usa la dependencia del constructor
                val response = paymentApiService.createTransaction(requestBody)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.toDomain())
                } else {
                    Result.failure(Exception("Error al crear transacción: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getInvoicePdfUrl(tax: Tax): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = TaxQueryRequestDTO(
                    entityCode = tax.entityCode,
                    queryData = tax.document,
                    queryField = "ced",
                    taxId = tax.taxId,
                    invoice = tax.reference
                )


                val response = taxApiService.downloadInvoice(requestBody)
                if (response.isSuccessful && response.body() != null) {
                    val base64Url = response.body()!!.replace("\"", "")
                    val fullUrl = "http://apidatamovil.1cero1.com/$base64Url"
                    Result.success(fullUrl)
                } else {
                    Result.failure(Exception("Error al descargar factura: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

fun TaxInfoDTO.toDomain(): Tax {
    return Tax(
        entity = this.entity,
        entityCode = this.entityCode,
        document = this.document,
        name = this.name,
        taxName = this.taxName,
        taxId = this.taxId,
        value = this.value,
        reference = this.reference,
        dueDate = this.dueDate,
        invoice = this.FacturaCode,
        pdfUrltoApi = this.detail?.url
    )
}

fun BancolombiaGatewayResponseDTO.toDomain(): PaymentGatewayInfo {
    return PaymentGatewayInfo(
        url = this.url
    )
}