package com.tramites1cero1.centralizacion.domain.repository

import com.tramites1cero1.centralizacion.data.model.CreateUserDTO
import com.tramites1cero1.centralizacion.data.model.DocumentTypeDTO
import com.tramites1cero1.centralizacion.data.model.LoginDTO
import com.tramites1cero1.centralizacion.data.model.UserDTO
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordByForgetDto
import com.tramites1cero1.centralizacion.data.model.UserDTOs.UpdatePasswordRequestDto
import com.tramites1cero1.centralizacion.data.model.ValidationResponseDTO
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body

interface AuthRepository {
    val user: Flow<UserDTO?>


    suspend fun getUserInformationByEmail(Email: String) : Result<UserDTO>
    suspend fun saveUserSession(user: UserDTO)
    suspend fun clearUserSession()

    suspend fun getOutUser(id:Int , status: Boolean) : ValidationResponseDTO
    suspend fun login(loginDto: LoginDTO): ValidationResponseDTO
    suspend fun deleteAccount(id:Int) : ValidationResponseDTO
    suspend fun getTypeDocuments(): List<DocumentTypeDTO>?;
    //no used --- ?????????
    suspend fun register(user : CreateUserDTO) : ValidationResponseDTO

    //recoveryPassword
    suspend fun updatePasswordUser(userId:Int , @Body request:UpdatePasswordRequestDto) : ValidationResponseDTO
    suspend fun updatePasswordUserByForget(userId: Int, @Body request: UpdatePasswordByForgetDto) : ValidationResponseDTO
    //sign up
    suspend fun saveRegistrationDraft(draft : CreateUserDTO)
    fun getRegistrationDraft() : Flow<CreateUserDTO?>
    suspend fun registerUser(userData : CreateUserDTO) : ValidationResponseDTO

    //Delete Preferences
    suspend fun clearPreferences()

    //----frebase user new ---//


}