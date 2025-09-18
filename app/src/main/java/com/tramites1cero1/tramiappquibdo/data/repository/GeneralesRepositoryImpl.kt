package com.tramites1cero1.tramiappquibdo.data.repository

import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.Ciudad
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.Departamento
import com.tramites1cero1.tramiappquibdo.data.network.GeneralesApiService
import com.tramites1cero1.tramiappquibdo.domain.repository.GeneralesRepository
import javax.inject.Inject


class GeneralesRepositoryImpl @Inject constructor(
    private val generalesApiService: GeneralesApiService
) : GeneralesRepository {
    override suspend fun getDepartamentos(): List<Departamento> {
        return generalesApiService.getDepartamentos()
    }

    override suspend fun getCiudadesPorDepartamento(departamentoId: String): List<Ciudad> {
        return generalesApiService.getCiudadesPorDepartamento(departamentoId)
    }
}