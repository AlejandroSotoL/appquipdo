package com.tramites1cero1.tramiappquibdo.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.tramites1cero1.tramiappquibdo.data.model.CreateUserDTO
import com.tramites1cero1.tramiappquibdo.data.model.DocumentTypeDTO
import com.tramites1cero1.tramiappquibdo.data.model.LoginDTO
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.model.UserDTOs.UpdatePasswordByForgetDto
import com.tramites1cero1.tramiappquibdo.data.model.UserDTOs.UpdatePasswordRequestDto
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.data.network.AuthApiService
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.tramites1cero1.tramiappquibdo.R

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService, @ApplicationContext private val context: Context
) : AuthRepository {

    private val gson = Gson()
    private val USER_DATA = stringPreferencesKey("user_data");
    private val REGISTRATION_DRAFT_KEY = stringPreferencesKey("registration_draft")
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    //--------------------------PREFERENCES-----------------------------------
    override val user: Flow<UserDTO?> = context.dataStore.data.map { preferences ->
        val json = preferences[USER_DATA]
        Log.d("LoginOptionsScreen", "Valor crudo en DataStore: $json")
        preferences[USER_DATA]?.let { json ->
            val user = gson.fromJson(json, UserDTO::class.java)
            Log.d("LoginOptionsScreen", "Usuario mapeado a userdeto: $user")
            user
        }
    }

    override suspend fun saveUserSession(user: UserDTO) {
        val json = gson.toJson(user)
        Log.d("LoginOptionsScreen", "Usuario serializado a JSON: $json")
        context.dataStore.edit { preferences ->
            preferences[USER_DATA] = json
        }
    }

    override suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_DATA)

        }
    }

    //Delete preferences - GENERAL DELETE - ¡WARNING!
    override suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.remove(REGISTRATION_DRAFT_KEY)
        }

    }

    //--------------------------Auth-----------------------------
    override suspend fun login(loginDto: LoginDTO): ValidationResponseDTO {
        return try {
            apiService.loginUser(loginDto)
        } catch (e: Exception) {
            e.printStackTrace()
            ValidationResponseDTO(
                codeStatus = 500, booleanStatus = false, sentencesError = when (e) {
                    is java.net.UnknownHostException -> "No hay conexión a Internet."
                    is retrofit2.HttpException -> "Credenciales incorrectas o error del servidor."
                    else -> "Ha ocurrido un error inesperado. Inténtalo nuevamente."
                }
            )
        }
    }

    override suspend fun updatePasswordUser(
        userId: Int, request: UpdatePasswordRequestDto
    ): ValidationResponseDTO {
        return try {
            apiService.updatePasswordUser(userId, request)
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false,
                sentencesError = "Tenemos problemas con tu cambio de contrasena"
            );
        } as ValidationResponseDTO
    }

    override suspend fun updatePasswordUserByForget(
        userId: Int, request: UpdatePasswordByForgetDto
    ): ValidationResponseDTO {
        return try {
            apiService.updatePasswordByForget(userId, request)
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false, sentencesError = "Tenemos problemas"
            );
        }
    }

    override suspend fun getTypeDocuments(): List<DocumentTypeDTO> {
        return try {
            apiService.getDocumentType()
        } catch (e: Exception) {
            emptyList()
        }
    }


    override suspend fun getUserInformationByEmail(email: String): Result<UserDTO> {
        return try {
            val user = apiService.getUserByEmail(email)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerUser(userData: CreateUserDTO): ValidationResponseDTO {
        return try {
            apiService.createUser(userData)
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false,
                codeStatus = 500,
                sentencesError = "Tenemos problemas: ${e.message ?: "Error desconocido"}"
            )
        } as ValidationResponseDTO
    }

    override suspend fun getOutUser(id: Int, status: Boolean): ValidationResponseDTO {
        return try {
            apiService.changedStatusUser(id, status)
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false,
                codeStatus = 500,
                sentencesError = "Tenemos problemas con tu cambio de Sesión ${e.message ?: "Error Desconocido"}"
            )
        }
    }

    //-------------------------Delete Account-------------------
    override suspend fun deleteAccount(id: Int): ValidationResponseDTO {
        return try {
            apiService.deleteUser(id)
        } catch (e: Exception) {
            ValidationResponseDTO(
                booleanStatus = false, sentencesError = "Tenemos problemas Error -> ${e.message}"
            );
        }
    }

    //-------------------------Other Preferences----------------
    override suspend fun saveRegistrationDraft(draft: CreateUserDTO) {
        val json = gson.toJson(draft)
        context.dataStore.edit { preferences ->
            preferences[REGISTRATION_DRAFT_KEY] = json
        }
    }

    override fun getRegistrationDraft(): Flow<CreateUserDTO?> {
        return context.dataStore.data.map { preferences ->
            preferences[REGISTRATION_DRAFT_KEY]?.let { json ->
                gson.fromJson(json, CreateUserDTO::class.java)
            }
        }
    }

    //-------------------What do we do with this???---------------------------
    override suspend fun register(user: CreateUserDTO): ValidationResponseDTO {
        return apiService.createUser(user)
    }

}
