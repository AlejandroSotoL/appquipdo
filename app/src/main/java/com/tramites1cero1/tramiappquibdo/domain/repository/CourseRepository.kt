package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.domain.model.Course
import com.tramites1cero1.tramiappquibdo.domain.model.CourseRegistration

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
    suspend fun registerForCourse(registration: CourseRegistration): Result<Unit>
}