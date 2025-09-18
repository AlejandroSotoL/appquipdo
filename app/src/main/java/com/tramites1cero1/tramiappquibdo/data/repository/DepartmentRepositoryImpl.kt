package com.tramites1cero1.tramiappquibdo.data.repository

import com.tramites1cero1.tramiappquibdo.data.network.DepartmentApiService
import com.tramites1cero1.tramiappquibdo.domain.model.Department
import com.tramites1cero1.tramiappquibdo.domain.repository.DepartmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DepartmentRepositoryImpl @Inject constructor(
    private val apiDepartment: DepartmentApiService
) : DepartmentRepository  {
    override suspend fun getDepartments(): List<Department> {
        return apiDepartment.getDepartments()
    }
}