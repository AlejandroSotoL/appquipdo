package com.tramites1cero1.centralizacion.data.repository

import android.content.Context
import com.tramites1cero1.centralizacion.data.network.DepartmentApiService
import com.tramites1cero1.centralizacion.domain.model.Department
import com.tramites1cero1.centralizacion.domain.repository.DepartmentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
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