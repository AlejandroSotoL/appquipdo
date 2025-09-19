package com.tramites1cero1.centralizacion.domain.usecase

import com.tramites1cero1.centralizacion.domain.repository.VenueRepository
import com.tramites1cero1.centralizacion.domain.model.Reservation

class GetVenuesUseCase(private val venueRepository: VenueRepository) {
    suspend operator fun invoke() = venueRepository.getVenues()
}

class CreateReservationUseCase(private val venueRepository: VenueRepository) {
    suspend operator fun invoke(reservation: Reservation) = venueRepository.createReservation(reservation)
}