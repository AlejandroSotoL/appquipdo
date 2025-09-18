package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.domain.model.Department

interface DepartmentRepository {
    suspend fun getDepartments(): List<Department>
}