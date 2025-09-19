package com.tramites1cero1.centralizacion.data.model


data class RemindersByUserDto(
    val id:Int?,
    val expirationDate: String?,
    val vigenciaDate: String?,
    val reminderType: String?,
    val idProcedureMunicipalityNavigation: MunicipalityProcedureDto?,
    val idUserNavigation: UserDtoReminders?
)

data class MunicipalityProcedureDto(
    val id: Int,
    val integrationType: String?,
    val isActive: Boolean,
    val procedures: ProceduresDto?,
    val municipality: JustMunicipalityDto?
)

data class ProceduresDto(
    val id: Int,
    val name: String
)

data class JustMunicipalityDto(
    val id: Int,
    val name: String?,
    val domain: String?,
    val isActive: Boolean?
)

data class UserDtoReminders(
    val firstName: String?,
    val lastName: String?
)
