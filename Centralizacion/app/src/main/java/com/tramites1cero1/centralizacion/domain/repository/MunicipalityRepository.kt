package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.data.model.MunicipalityDTO
import com.tramites1cero1.centralizacion.domain.model.Municipality
import com.tramites1cero1.centralizacion.domain.model.MunicipalityModel

interface MunicipalityRepository {
    suspend fun getMunicipalitiesByDepartment(departmentID: Int): List<Municipality>
    suspend fun getMunicipalityData(municipalityId: Int): MunicipalityModel

}