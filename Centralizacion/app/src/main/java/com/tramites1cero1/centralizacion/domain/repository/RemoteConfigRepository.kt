package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.data.model.CarouselImage

interface RemoteConfigRepository {
    suspend fun getCarouselImages(): List<CarouselImage>
}