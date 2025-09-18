package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.data.model.NewsDTO

interface NewsRepository {
    // Carga las noticias si no están en caché
    suspend fun getNews(baseUrl : String): List<NewsDTO>

    // Encuentra una noticia específica por su ID
    suspend fun getNewById(id: Int, baseUrl : String): NewsDTO?


}