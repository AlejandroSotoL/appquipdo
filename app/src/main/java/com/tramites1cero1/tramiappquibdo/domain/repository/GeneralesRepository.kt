package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.Ciudad
import com.tramites1cero1.tramiappquibdo.data.model.pqrddto.Departamento

interface GeneralesRepository {
    suspend fun getDepartamentos(): List<Departamento>
    suspend fun getCiudadesPorDepartamento(departamentoId: String): List<Ciudad>
}