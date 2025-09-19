package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.domain.model.Reservation
import com.tramites1cero1.centralizacion.domain.model.Venue

interface VenueRepository {
    suspend fun getVenues(): Result<List<Venue>>
    suspend fun createReservation(reservation: Reservation): Result<Unit>
}