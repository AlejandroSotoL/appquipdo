package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.domain.model.Reservation
import com.tramites1cero1.tramiappquibdo.domain.model.Venue

interface VenueRepository {
    suspend fun getVenues(): Result<List<Venue>>
    suspend fun createReservation(reservation: Reservation): Result<Unit>
}