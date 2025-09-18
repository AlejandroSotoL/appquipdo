package com.tramites1cero1.tramiappquibdo.data.repository

import com.tramites1cero1.tramiappquibdo.data.model.NewsDTO
import com.tramites1cero1.tramiappquibdo.data.network.NewsApiService
import com.tramites1cero1.tramiappquibdo.domain.repository.NewsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService
) : NewsRepository {

    private val noticiasCache = mutableMapOf<String, List<NewsDTO>>()
    override suspend fun getNews(baseUrl: String): List<NewsDTO> {

        if(noticiasCache.containsKey(baseUrl)){
            return noticiasCache[baseUrl]!!
        }

        val fullUrl = "$baseUrl/NuestraAlcaldia/SaladePrensa/_api/web/lists/getbytitle('P%C3%A1ginas')/items"
        val response = newsApiService.getNews(fullUrl)
        val newsList = response.d.results

        noticiasCache[baseUrl] = newsList
        return newsList
    }

    override suspend fun getNewById(id: Int, baseUrl: String): NewsDTO? {
        // Asegúrate de que la caché esté llena primero
        getNews(baseUrl)
        return noticiasCache[baseUrl]?.find { it.id == id }
    }

}