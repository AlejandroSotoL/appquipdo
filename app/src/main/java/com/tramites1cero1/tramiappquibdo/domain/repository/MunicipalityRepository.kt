package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.domain.model.Municipality
import com.tramites1cero1.tramiappquibdo.domain.model.MunicipalityModel

interface MunicipalityRepository {
    suspend fun getMunicipalitiesByDepartment(departmentID: Int): List<Municipality>
    suspend fun getMunicipalityData(municipalityId: Int): MunicipalityModel

}