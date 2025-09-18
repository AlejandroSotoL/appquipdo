package com.tramites1cero1.tramiappquibdo.data.model

import com.google.gson.annotations.SerializedName

// DTO para un escenario deportivo
data class VenueDTO(
    @SerializedName("ID") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("Direcci_x00f3_n") val address: String?,
    @SerializedName("Description") val description: String?,
    @SerializedName("Capacidad") val capacity: Int?,
    @SerializedName("EncodedAbsUrl") val imageUrl: String?
)

// DTOs para la respuesta de la lista de escenarios
data class VenueListResponseWrapperDTO(val d: VenueListResponseDTO)
data class VenueListResponseDTO(val results: List<VenueDTO>)

// DTO para la solicitud de reserva
data class ReservationRequestDTO(
    @SerializedName("__metadata") val metadata: MetadataDTO,
    @SerializedName("Title") val title: String,
    @SerializedName("Nombre") val firstName: String,
    @SerializedName("Apellido") val lastName: String,
    @SerializedName("Tipo_x0020_de_x0020_Documento") val documentType: String,
    @SerializedName("OData__x0023_Documento") val documentNumber: String,
    @SerializedName("Correo_x0020_Electronico") val email: String,
    @SerializedName("ID_x002d_EscenarioId") val venueId: Int
)

// DTO para crear el evento en el calendario
data class CalendarEventRequestDTO(
    @SerializedName("__metadata") val metadata: MetadataDTO,
    @SerializedName("Title") val title: String,
    @SerializedName("EventDate") val eventDate: String, // Formato: "YYYY-MM-DDTHH:mm:ss"
    @SerializedName("EndDate") val endDate: String,   // Formato: "YYYY-MM-DDTHH:mm:ss"
    @SerializedName("ResponsableId") val responsibleId: Int,
    @SerializedName("EscenarioId") val venueId: Int
)
