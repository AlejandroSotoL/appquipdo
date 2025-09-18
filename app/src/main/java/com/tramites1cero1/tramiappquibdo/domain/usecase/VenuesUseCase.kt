package com.tramites1cero1.tramiappquibdo.domain.usecase

import com.tramites1cero1.tramiappquibdo.domain.repository.VenueRepository
import com.tramites1cero1.tramiappquibdo.domain.model.Reservation

class GetVenuesUseCase(private val venueRepository: VenueRepository) {
    suspend operator fun invoke() = venueRepository.getVenues()
}

class CreateReservationUseCase(private val venueRepository: VenueRepository) {
    suspend operator fun invoke(reservation: Reservation) = venueRepository.createReservation(reservation)
}