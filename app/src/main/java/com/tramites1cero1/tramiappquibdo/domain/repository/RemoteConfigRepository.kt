package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.data.model.CarouselImage

interface RemoteConfigRepository {
    suspend fun getCarouselImages(): List<CarouselImage>
}