package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.data.model.CreateUserDTO
import com.tramites1cero1.tramiappquibdo.data.model.LoginDTO
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiCentralizateApps {
    @GET("api/user")
    suspend  fun  getUsers():List<UserDTO>

    @POST("api/user")
    suspend fun createUser(@Body user: CreateUserDTO): String

    @POST("api/Auth")
    suspend fun loginUser(@Body loginDto: LoginDTO): Response<ValidationResponseDTO>

    @GET("api/User/by-email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): CreateUserDTO
}