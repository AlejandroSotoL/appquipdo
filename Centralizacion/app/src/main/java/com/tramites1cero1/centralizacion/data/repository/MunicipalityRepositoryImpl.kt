package com.tramites1cero1.centralizacion.data.repository

import com.tramites1cero1.centralizacion.data.mapper.toDomainModel
import com.tramites1cero1.centralizacion.data.network.MunicipalityApiService
import com.tramites1cero1.centralizacion.domain.model.Design
import com.tramites1cero1.centralizacion.domain.model.IntegrationTypeModel
import com.tramites1cero1.centralizacion.domain.model.Municipality
import com.tramites1cero1.centralizacion.domain.model.MunicipalityModel
import com.tramites1cero1.centralizacion.domain.repository.MunicipalityRepository
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MunicipalityRepositoryImpl
@Inject constructor(
    private  val apiMunicipality : MunicipalityApiService
) : MunicipalityRepository{

    // Obtiene la lista de nombres de municipios
    private val municipalityCache : MutableMap<Int, MunicipalityModel> = ConcurrentHashMap()

    override suspend fun getMunicipalitiesByDepartment(departmentID: Int): List<Municipality> {
        return try {
            val allMunicipalities = apiMunicipality.getDepartmentMunicipalitiesById(departmentID)

            allMunicipalities.filter { it.isActive }
        } catch (ex: Exception) {
            ex.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getMunicipalityData(municipalityId: Int): MunicipalityModel {
        if (municipalityCache.containsKey(municipalityId)) {
            return municipalityCache[municipalityId]!!
        }

        return try {
            val municipalityDto = apiMunicipality.getMunicipalitiesById(municipalityId)
            //  Llama a la función de extensión directamente
            val mappedModel = municipalityDto.toDomainModel()
            municipalityCache[municipalityId] = mappedModel
            mappedModel
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Es mejor relanzar la excepción para que el ViewModel la maneje
        }
    }
}