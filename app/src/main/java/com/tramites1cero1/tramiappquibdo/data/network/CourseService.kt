package com.tramites1cero1.tramiappquibdo.data.network

import com.tramites1cero1.tramiappquibdo.data.model.CourseListResponseWrapperDTO
import com.tramites1cero1.tramiappquibdo.data.model.CourseRegistrationRequestDTO
import com.tramites1cero1.tramiappquibdo.data.model.SharePointContextResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface CourseApiService {

    @Headers("Accept: application/json;odata=verbose")
    @GET("_api/web/lists/getbytitle('Cursos')/items?\$select=ID,Title,Description,Categoria,CupoDisponible,FechaInicio,FechaFin,EncodedAbsUrl")
    suspend fun getCourses(): Response<CourseListResponseWrapperDTO>

    @Headers("Accept: application/json;odata=verbose")
    @POST("_api/contextinfo")
    suspend fun getSharePointToken(): Response<SharePointContextResponseDTO>

    @Headers(
        "Accept: application/json;odata=verbose",
        "Content-Type: application/json;odata=verbose"
    )
    @POST("_api/web/lists/getbytitle('Inscripciones%20a%20Cursos')/items")
    suspend fun sendCourseRegistration(
        @Header("X-RequestDigest") digest: String,
        @Body payload: CourseRegistrationRequestDTO
    ): Response<Void>
}