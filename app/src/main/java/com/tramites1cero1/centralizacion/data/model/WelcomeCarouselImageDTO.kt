package com.tramites1cero1.centralizacion.data.model

data class CarouselImage(
    val imageUrl: String,
    val clickUrl: String
)


data class CarouselImageDTO(val imageUrl: String, val clickUrl: String)
data class CarouselConfigDTO(val images: List<CarouselImageDTO>)