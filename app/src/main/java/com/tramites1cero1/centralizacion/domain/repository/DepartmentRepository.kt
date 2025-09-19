package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.domain.model.Department

interface DepartmentRepository {
    suspend fun getDepartments(): List<Department>
}