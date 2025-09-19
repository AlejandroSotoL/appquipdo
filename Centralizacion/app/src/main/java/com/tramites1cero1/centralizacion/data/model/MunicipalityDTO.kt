package com.tramites1cero1.centralizacion.data.model

import com.google.gson.annotations.SerializedName
import com.tramites1cero1.centralizacion.domain.model.CourseX
import com.tramites1cero1.centralizacion.domain.model.Department
import com.tramites1cero1.centralizacion.domain.model.MunicipalityProcedure
import com.tramites1cero1.centralizacion.domain.model.MunicipalitySocialMedia
import com.tramites1cero1.centralizacion.domain.model.QueryField
import com.tramites1cero1.centralizacion.domain.model.SportsFacility
import com.tramites1cero1.centralizacion.domain.model.Theme

data class MunicipalityDTO(
    val courses: List<CourseX>,
    val department: Department,
    val domain: String,
    val entityCode: String,
    val id: Int,
    val isActive: Boolean,
    val municipalityProcedures: List<MunicipalityProcedure>,
    val municipalitySocialMedia: List<MunicipalitySocialMedia>,
    val name: String,
    val passwordFintech: String,
    val queryFields: List<QueryField>,
    val sportsFacilities: List<SportsFacility>,
    val theme: Theme,
    val userFintech: String,
    val bank : BankDTO,
    val idShield : ShieldDTO,
    @SerializedName("dataPrivacy")
    val dataPrivacy: String?,
    @SerializedName("dataProcessingPrivacy")
    val dataProcessingPrivacy: String?,
    @SerializedName("newsByMunicipalities")
    val newsByMunicipalities: List<NewsByMunicipality>

)