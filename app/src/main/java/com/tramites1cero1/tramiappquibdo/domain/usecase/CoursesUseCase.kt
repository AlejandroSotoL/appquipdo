package com.tramites1cero1.tramiappquibdo.domain.usecase

import com.tramites1cero1.tramiappquibdo.domain.repository.CourseRepository
import com.tramites1cero1.tramiappquibdo.domain.model.CourseRegistration

class GetCoursesUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke() = courseRepository.getCourses()
}

class RegisterForCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(registration: CourseRegistration) = courseRepository.registerForCourse(registration)
}