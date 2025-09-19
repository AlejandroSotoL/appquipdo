package com.tramites1cero1.centralizacion.domain.model


data class Course(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)


data class CourseRegistration(
    val courseId: Int,
    val courseTitle: String,
    val documentNumber: String,
    val firstName: String,
    val lastName: String,
    val age: String,
    val email: String,
    val phone: String
)