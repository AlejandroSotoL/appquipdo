package com.tramites1cero1.tramiappquibdo.data.repository

import com.tramites1cero1.tramiappquibdo.data.model.CalendarEventRequestDTO
import com.tramites1cero1.tramiappquibdo.data.model.MetadataDTO
import com.tramites1cero1.tramiappquibdo.data.model.ReservationRequestDTO
import com.tramites1cero1.tramiappquibdo.data.model.VenueDTO
import com.tramites1cero1.tramiappquibdo.data.network.RetrofitClient
import com.tramites1cero1.tramiappquibdo.data.network.VenueApiService
import com.tramites1cero1.tramiappquibdo.domain.model.Reservation
import com.tramites1cero1.tramiappquibdo.domain.model.Venue
import com.tramites1cero1.tramiappquibdo.domain.repository.VenueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class VenueRepositoryImpl(
    private val getBaseUrl: String,
    private val postReservationBaseUrl: String,
    private val postCalendarBaseUrl: String,

) : VenueRepository {

    // Creamos el servicio bajo demanda con la URL correcta
    private val sportsGetService: VenueApiService by lazy {
        RetrofitClient.createService(getBaseUrl, VenueApiService::class.java)
    }

    // Podríamos necesitar otro si la URL de POST es diferente
    private val sportsPostCalendarService: VenueApiService by lazy {
        RetrofitClient.createService(postCalendarBaseUrl, VenueApiService::class.java)
    }
    private val sportsPostReservationService: VenueApiService by lazy {
        RetrofitClient.createService(postReservationBaseUrl, VenueApiService::class.java)
    }


    override suspend fun getVenues(): Result<List<Venue>> = withContext(Dispatchers.IO) {
        try {
            val response = sportsGetService.getVenues()
            if (response.isSuccessful) {
                val venues = response.body()?.d?.results?.map { it.toDomain() } ?: emptyList()
                Result.success(venues)
            } else {
                Result.failure(Exception("Error al obtener escenarios: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReservation(reservation: Reservation): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Paso 1: Obtener Token
            val tokenResponse = sportsPostReservationService.getSharePointToken()
            val token = tokenResponse.body()?.d?.GetContextWebInformation?.FormDigestValue
                ?: return@withContext Result.failure(Exception("Token de SharePoint nulo"))

            // Paso 2: Enviar Reserva
            val reservationDTO = ReservationRequestDTO(
                metadata = MetadataDTO("SP.Data.ReservasListItem"),
                title = reservation.venueTitle,
                firstName = reservation.firstName,
                lastName = reservation.lastName,
                documentType = reservation.documentType,
                documentNumber = reservation.documentNumber,
                email = reservation.email,
                venueId = reservation.venueId
            )
            val reservationResponse = sportsPostReservationService.sendReservation(token, reservationDTO)
            if (!reservationResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Error al enviar reserva: ${reservationResponse.code()}"))
            }

            // Paso 3: Enviar Evento al Calendario
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val eventDate = sdf.parse("${reservation.date}T${reservation.time}")
            calendar.time = eventDate
            val startDate = sdf.format(calendar.time)
            calendar.add(Calendar.HOUR, 1) // Todas las reservas duran una hora
            val endDate = sdf.format(calendar.time)

            val eventDTO = CalendarEventRequestDTO(
                metadata = MetadataDTO("SP.Data.CalendarioReservasListItem"),
                title = "Reserva ${reservation.venueTitle}",
                eventDate = startDate,
                endDate = endDate,
                responsibleId = 1, // Hablar con Luis para ver como es esta vuelta
                venueId = reservation.venueId
            )
            val eventResponse = sportsPostCalendarService.sendCalendarEvent(token, eventDTO)
            if (eventResponse.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al agendar en calendario: ${eventResponse.code()}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun VenueDTO.toDomain(): Venue {
    return Venue(
        id = this.id,
        title = this.title,
        address = this.address ?: "No disponible",
        description = this.description ?: "Sin descripción",
        capacity = this.capacity ?: 0,
        imageUrl = this.imageUrl ?: ""
    )
}