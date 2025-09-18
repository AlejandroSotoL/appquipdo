package com.tramites1cero1.tramiappquibdo.data.network

import com.tramites1cero1.tramiappquibdo.data.model.NewsResponseWrapper
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

//NOTICIAS
interface NewsApiService {
    @Headers(
        "Accept: application/json;odata=verbose",
        "Content-Type: application/json"
    )
    @GET()
    suspend fun getNews(
        @Url url : String, // URL BASE DEL MUNICIPIO CARGADA DESDE LA API EN MUNICIAPLITY MODEL
        @Query("\$select") fields: String = "ID,Title,Modified,FechaHoraNoticia,PublishingPageContent,FieldValuesAsHtml/PublishingPageImage",
        @Query("\$expand") expand: String = "FieldValuesAsHtml",
        @Query("\$orderby") order: String = "Modified desc,FechaHoraNoticia desc"
    ): NewsResponseWrapper
}