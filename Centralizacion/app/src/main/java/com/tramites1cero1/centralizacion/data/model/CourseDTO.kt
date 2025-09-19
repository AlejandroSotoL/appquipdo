package com.tramites1cero1.centralizacion.data.model


import com.google.gson.annotations.SerializedName

// DTO para un curso individual
data class CourseDTO(
    @SerializedName("ID") val id: Int,
    @SerializedName("Title") val title: String,
    @SerializedName("Descripcion") val description: String?,
    @SerializedName("Categoria") val category: String?,
    @SerializedName("CupoDisponible") val availableSlots: Int?,
    @SerializedName("FechaInicio") val startDate: String?,
    @SerializedName("FechaFin") val endDate: String?,
    @SerializedName("EncodedAbsUrl") val imageUrl: String?
)

// DTOs para envolver la respuesta de la lista de cursos
data class CourseListResponseWrapperDTO(val d: CourseListResponseDTO)
data class CourseListResponseDTO(val results: List<CourseDTO>)

// DTO para la solicitud de inscripción
data class CourseRegistrationRequestDTO(
    @SerializedName("__metadata") val metadata: MetadataDTO,
    @SerializedName("Title") val title: String,
    @SerializedName("Nombre") val firstName: String,
    @SerializedName("Apellido") val lastName: String,
    @SerializedName("Correo_x0020_Electronico") val email: String,
    @SerializedName("Telefono") val phone: String,
    @SerializedName("ID_x002d_CursoId") val courseId: Int,
    @SerializedName("OData__x0023_Documento") val documentNumber: Int,
    @SerializedName("Edad") val age: Int,
)

data class MetadataDTO(
    val type: String = "SP.Data.Inscripciones_x0020_a_x0020_CursosListItem"
)

// DTOs para obtener el token de SharePoint
data class SharePointContextResponseDTO(val d: ContextInfoDataDTO)
data class ContextInfoDataDTO(val GetContextWebInformation: ContextTokenDTO)
data class ContextTokenDTO(val FormDigestValue: String)