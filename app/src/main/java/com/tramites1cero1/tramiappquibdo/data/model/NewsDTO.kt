package com.tramites1cero1.tramiappquibdo.data.model

import com.google.gson.annotations.SerializedName

data class NewsDTO(
    @SerializedName("ID") val id: Int, // En tu XML de ejemplo es 'Id', pero la API de SP suele usar 'ID' en JSON. Mantén el que te funcione.
    @SerializedName("Title") val title: String,
    @SerializedName("Modified") val modified: String, // El campo para ordenar y filtrar
    @SerializedName("FechaHoraNoticia") val fechaHoraNoticia: String?, // El nuevo campo de fecha
    @SerializedName("PublishingPageContent") val publishingPageContent: String?,
    @SerializedName("FieldValuesAsHtml") val fieldValuesAsHtml: FieldValuesAsHtml?
)

data class FieldValuesAsHtml(
    // Este campo contiene el <img> de la imagen principal
    @SerializedName("PublishingPageImage") val publishingPageImage: String?
)

data class NewsResponseWrapper(
    val d: NewsResponse
)

data class NewsResponse(
    val results: List<NewsDTO>
)