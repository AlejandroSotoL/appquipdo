package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.data.model.pqrddto.Ciudad
import com.tramites1cero1.centralizacion.data.model.pqrddto.Departamento

interface GeneralesRepository {
    suspend fun getDepartamentos(): List<Departamento>
    suspend fun getCiudadesPorDepartamento(departamentoId: String): List<Ciudad>
}