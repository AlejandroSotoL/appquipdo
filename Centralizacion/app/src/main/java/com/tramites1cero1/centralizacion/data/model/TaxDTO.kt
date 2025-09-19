package com.tramites1cero1.centralizacion.data.model

import com.google.gson.annotations.SerializedName
import org.checkerframework.checker.units.qual.s

data class TaxQueryResponseDTO(
    @SerializedName("Mensaje") val message: String,
    @SerializedName("Informacion") val information: List<TaxInfoDTO>?
)

data class TaxInfoDTO(
    @SerializedName("Entidad") val entity: String,
    @SerializedName("CodigoEntidad") val entityCode: String,
    @SerializedName("Documento") val document: String,
    @SerializedName("Nombre") val name: String,
    @SerializedName("Impuesto") val taxName: String,
    @SerializedName("Id_Impuesto") val taxId: Int,
    @SerializedName("Valor") val value: Int,
    @SerializedName("ValorAnual") val annualValue: Int,
    @SerializedName("ValorSemestre") val semesterValue: Int,
    @SerializedName("ValorTrimestre") val trimesterValue: Int,
    @SerializedName("ValorParcial") val partialValue: Int,
    @SerializedName("Referencia") val reference: String,
    @SerializedName("FechaVencimiento") val dueDate: String,
    @SerializedName("CodigoCatastral") val cadastralCode: String,
    @SerializedName("Detalle") val detail: DetalleDTO?,
    @SerializedName("Factura") val FacturaCode : String
)

data class DetalleDTO(
    @SerializedName("Url") val url: String?
)


data class TaxQueryRequestDTO(
    @SerializedName("CodigoEntidad") val entityCode: String,
    @SerializedName("DatoConsulta") val queryData: String,
    @SerializedName("CampoConsulta") val queryField: String,
    @SerializedName("IDImpuesto") val taxId: Int,
    @SerializedName("Factura") val invoice: String
)

data class BancolombiaGatewayRequestDTO(
    @SerializedName("Referencia") val reference: String,
    @SerializedName("Factura") val invoice: String,
    @SerializedName("CodigoMunicipio") val municipalityCode: String,
    @SerializedName("TipoDocumento") val documentType: String,
    @SerializedName("Identificacion") val identification: String,
    @SerializedName("Nombre") val name: String,
    @SerializedName("Total") val total: Int,
    @SerializedName("IDImpuesto") val taxId: Int,
    @SerializedName("Email") val email: String,
    @SerializedName("Telefono") val phone: String,
    @SerializedName("FuentePago") val paymentSource: Int,
    @SerializedName("TipoImplementacion") val implementationType: Int
)

data class BancolombiaGatewayResponseDTO(
    @SerializedName("URL") val url: String,
    @SerializedName("Codigo") val code: Int,
    @SerializedName("Mensaje") val message: String
)