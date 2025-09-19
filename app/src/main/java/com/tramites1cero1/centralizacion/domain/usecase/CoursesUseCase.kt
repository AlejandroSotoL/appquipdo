package com.tramites1cero1.centralizacion.domain.usecase

import com.tramites1cero1.centralizacion.domain.repository.CourseRepository
import com.tramites1cero1.centralizacion.domain.model.CourseRegistration

class GetCoursesUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke() = courseRepository.getCourses()
}

class RegisterForCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(registration: CourseRegistration) = courseRepository.registerForCourse(registration)
}