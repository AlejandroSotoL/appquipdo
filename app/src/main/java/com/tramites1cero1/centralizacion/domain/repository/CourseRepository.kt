package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.domain.model.Course
import com.tramites1cero1.centralizacion.domain.model.CourseRegistration

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
    suspend fun registerForCourse(registration: CourseRegistration): Result<Unit>
}