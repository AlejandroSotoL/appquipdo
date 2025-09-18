package com.tramites1cero1.tramiappquibdo.data.repository


import com.tramites1cero1.tramiappquibdo.data.model.CourseRegistrationRequestDTO
import com.tramites1cero1.tramiappquibdo.data.model.MetadataDTO
import com.tramites1cero1.tramiappquibdo.data.network.CourseApiService
import com.tramites1cero1.tramiappquibdo.data.network.RetrofitClient
import com.tramites1cero1.tramiappquibdo.domain.model.Course
import com.tramites1cero1.tramiappquibdo.domain.model.CourseRegistration
import com.tramites1cero1.tramiappquibdo.domain.repository.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseRepositoryImpl(
    private val getBaseUrl: String,
    private val postBaseUrl: String
) : CourseRepository {

    // Creamos el servicio bajo demanda con la URL correcta
    private val courseGetService: CourseApiService by lazy {
        RetrofitClient.createService(getBaseUrl, CourseApiService::class.java)
    }

    // Podríamos necesitar otro si la URL de POST es diferente
    private val coursePostService: CourseApiService by lazy {
        RetrofitClient.createService(postBaseUrl, CourseApiService::class.java)
    }

    override suspend fun getCourses(): Result<List<Course>> = withContext(Dispatchers.IO) {
        try {

            val response = courseGetService.getCourses()
            if (response.isSuccessful) {
                val courses = response.body()?.d?.results?.map { it.toDomain() } ?: emptyList()
                Result.success(courses)
            } else {
                Result.failure(Exception("Error al obtener cursos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerForCourse(registration: CourseRegistration): Result<Unit> = withContext(Dispatchers.IO) {
        try {

            val tokenResponse = coursePostService.getSharePointToken()
            if (!tokenResponse.isSuccessful) {
                return@withContext Result.failure(Exception("Error al obtener token: ${tokenResponse.code()}"))
            }
            val token = tokenResponse.body()?.d?.GetContextWebInformation?.FormDigestValue
                ?: return@withContext Result.failure(Exception("Token de SharePoint nulo"))

            // Paso 2: Enviar la inscripción con el token
            val requestDTO = CourseRegistrationRequestDTO(
                metadata = MetadataDTO(),
                title = "Inscripción al curso ${registration.courseTitle}",
                firstName = registration.firstName,
                lastName = registration.lastName,
                email = registration.email,
                phone = registration.phone,
                courseId = registration.courseId,
                documentNumber = registration.documentNumber.toInt(),
                age = registration.age.toInt()
            )

            val registrationResponse = coursePostService.sendCourseRegistration(token, requestDTO)
            if (registrationResponse.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al enviar inscripción: ${registrationResponse.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Función de extensión para mapear el DTO a un modelo de dominio
private fun com.tramites1cero1.tramiappquibdo.data.model.CourseDTO.toDomain(): Course {
    return Course(
        id = this.id,
        title = this.title,
        description = this.description ?: "Sin descripción",
        imageUrl = this.imageUrl ?: ""
    )
}