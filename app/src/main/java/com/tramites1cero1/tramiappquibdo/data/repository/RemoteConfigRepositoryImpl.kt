package com.tramites1cero1.tramiappquibdo.data.repository

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.tramites1cero1.tramiappquibdo.data.model.CarouselConfigDTO
import com.tramites1cero1.tramiappquibdo.data.model.CarouselImage
import com.tramites1cero1.tramiappquibdo.domain.repository.RemoteConfigRepository

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RemoteConfigRepositoryImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val gson: Gson
) : RemoteConfigRepository {
    override suspend fun getCarouselImages(): List<CarouselImage> {
        return try {
            remoteConfig.fetchAndActivate().await()
            val jsonString = remoteConfig.getString("welcome_carousel_images")
            if (jsonString.isBlank()) return emptyList()

            val config = gson.fromJson(jsonString, CarouselConfigDTO::class.java)
            config.images.map { CarouselImage(it.imageUrl, it.clickUrl) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}