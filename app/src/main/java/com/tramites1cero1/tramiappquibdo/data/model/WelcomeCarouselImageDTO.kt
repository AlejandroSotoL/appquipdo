package com.tramites1cero1.tramiappquibdo.data.model

data class CarouselImage(
    val imageUrl: String,
    val clickUrl: String
)


data class CarouselImageDTO(val imageUrl: String, val clickUrl: String)
data class CarouselConfigDTO(val images: List<CarouselImageDTO>)