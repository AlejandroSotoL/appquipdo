package com.tramites1cero1.centralizacion.data.network

import com.tramites1cero1.centralizacion.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface VenueApiService {
    @Headers("Accept: application/json;odata=verbose")
    @GET("_api/web/lists/getbytitle('Escenarios Deportivos')/items?\$select=ID,Title,Direcci_x00f3_n,Description,Capacidad,EncodedAbsUrl")
    suspend fun getVenues(): Response<VenueListResponseWrapperDTO>

    @Headers("Accept: application/json;odata=verbose")
    @POST("_api/contextinfo")
    suspend fun getSharePointToken(): Response<SharePointContextResponseDTO>

    @Headers("Accept: application/json;odata=verbose", "Content-Type: application/json;odata=verbose")
    @POST("_api/web/lists/getbytitle('Reservas')/items")
    suspend fun sendReservation(
        @Header("X-RequestDigest") digest: String,
        @Body reservation: ReservationRequestDTO
    ): Response<Void>

    @Headers("Accept: application/json;odata=verbose", "Content-Type: application/json;odata=verbose")
    @POST("_api/web/lists/getbytitle('CalendarioReservas')/items")
    suspend fun sendCalendarEvent(
        @Header("X-RequestDigest") digest: String,
        @Body event: CalendarEventRequestDTO
    ): Response<Void>
}