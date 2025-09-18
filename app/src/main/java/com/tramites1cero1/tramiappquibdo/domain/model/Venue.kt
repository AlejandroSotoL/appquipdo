// File: domain/model/Venue.kt
package com.tramites1cero1.tramiappquibdo.domain.model

data class Venue(
    val id: Int,
    val title: String,
    val address: String,
    val description: String,
    val capacity: Int,
    val imageUrl: String
)


data class Reservation(
    val venueId: Int,
    val venueTitle: String,
    val firstName: String,
    val lastName: String,
    val documentType: String,
    val documentNumber: String,
    val email: String,
    val date: String, // "YYYY-MM-DD"
    val time: String  // "HH:MM:SS"
)