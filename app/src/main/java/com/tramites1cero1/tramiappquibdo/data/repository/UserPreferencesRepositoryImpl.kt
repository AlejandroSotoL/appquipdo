package com.tramites1cero1.tramiappquibdo.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.tramites1cero1.tramiappquibdo.data.model.PeopleInvitated
import com.tramites1cero1.tramiappquibdo.data.model.StatusTransactionDto
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.data.network.PeopleInvitatedService

import com.tramites1cero1.tramiappquibdo.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.longPreferencesKey
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val api: PeopleInvitatedService
) : UserPreferencesRepository {

    private object PreferencesKey {
        val departmentSelectedId = intPreferencesKey("departamento_seleccionado_id")
        val municipalitySelectedId = intPreferencesKey("municipio_seleccionado_id")
        val municipioSelect = stringPreferencesKey("municipio_seleccionado")
        val guardarUbicacion = booleanPreferencesKey("guardar_preferencia")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val remindersIsVible = booleanPreferencesKey("remindersIsVible")
        val remindersSendByEmail = booleanPreferencesKey("remindersSendByEmail")
        val MODAL_FORM_COMPLETED = booleanPreferencesKey("modal_form_completed")
        val GUEST_USER_DATA = stringPreferencesKey("guest_user_data")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val LIST_PAYMENTS = stringPreferencesKey("LIST_PAYMENTS")
        val IS_BLOQUED_BOTTOM = longPreferencesKey("IS_BLOQUED_BOTTOM")
    }

    override fun getGuestUserData(): Flow<UserDTO?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.GUEST_USER_DATA]?.let { json ->
                Gson().fromJson(json, UserDTO::class.java)
            }
        }
    }

    override suspend fun saveGuestUserData(user: UserDTO) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.GUEST_USER_DATA] = Gson().toJson(user)
        }
    }

    override fun getModalFormCompleted(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.MODAL_FORM_COMPLETED] ?: false
        }
    }

    override suspend fun saveModalFormCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.MODAL_FORM_COMPLETED] = completed
        }
    }

    override suspend fun saveUbicationPreferences(
        departmentId: Int, municipalityId: Int, municipio: String, guardar: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.departmentSelectedId] = departmentId
            preferences[PreferencesKey.municipalitySelectedId] = municipalityId
            preferences[PreferencesKey.municipioSelect] = municipio
            preferences[PreferencesKey.guardarUbicacion] = guardar
        }
    }

    override suspend fun saveDepartmentSelected(departmentId: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.departmentSelectedId]
        }
    }

    override fun getSavedUbication(): Flow<PreferenciaUsuario> {
        return context.dataStore.data.map { preferences ->
            PreferenciaUsuario(
                departmentId = preferences[PreferencesKey.departmentSelectedId] ?: 0,
                municipalityId = preferences[PreferencesKey.municipalitySelectedId] ?: 0,
                municipio = preferences[PreferencesKey.municipioSelect] ?: "",
                guardado = preferences[PreferencesKey.guardarUbicacion] ?: false
            )
        }
    }

    override fun getTheme(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKey.IS_DARK_THEME] ?: false
        }
    }

    //-------------------REMINDERS-----------------------

    override suspend fun saveSelectionSendByEmail(isActive: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.remindersSendByEmail] = isActive
        }
    }


    override val remindersSendIsVisibleFlow: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[PreferencesKey.remindersSendByEmail] ?: false
        }

    override suspend fun setRemindersSendVisible(isActive: Boolean) {
        dataStore.edit { prefr ->
            prefr[PreferencesKey.remindersSendByEmail] = isActive
        }
    }


    //Login - Token
    override suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKey.AUTH_TOKEN] = token
        }
    }

    override suspend fun getAuthToken(): String {
        val prefs = context.dataStore.data.first()
        return prefs[PreferencesKey.AUTH_TOKEN].toString()
    }

    //Save selection about reminders
    override suspend fun saveSelectionReminders(isActive: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.remindersIsVible] = isActive
        }
    }

    override val remindersIsVisibleFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKey.remindersIsVible] ?: false
    }

    // 3. Implementamos la función para cambiar el tema
    override suspend fun toggleTheme() {
        context.dataStore.edit { preferences ->
            // Lee el valor actual (o 'false' si es nulo)
            val currentTheme = preferences[PreferencesKey.IS_DARK_THEME] ?: false
            preferences[PreferencesKey.IS_DARK_THEME] = !currentTheme
        }
    }

    //Saved list by payment
    override suspend fun saveEveryPayByBlock(lista: List<StatusTransactionDto>) {
        val gson = Gson()
        val json = gson.toJson(lista)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.LIST_PAYMENTS] = json
        }
    }

    override suspend fun getEveryPay(): Flow<List<StatusTransactionDto>> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKey.LIST_PAYMENTS]?.let { json ->
                val type =
                    object : com.google.gson.reflect.TypeToken<List<StatusTransactionDto>>() {}.type
                Gson().fromJson<List<StatusTransactionDto>>(json, type)
            } ?: emptyList()
        }
    }

    override suspend fun setEveryPayByBlock() {
        TODO("Not yet implemented")
    }


    override suspend fun createPeopleInvitates(people: PeopleInvitated): ValidationResponseDTO {
            return try {
                val response = api.createPeopleInvitated(people)
                ValidationResponseDTO(
                    codeStatus = response.codeStatus,
                    booleanStatus = response.booleanStatus,
                    sentencesError = response.sentencesError
                )
            } catch (ex: Exception) {
                ValidationResponseDTO(
                    codeStatus = 500,
                    booleanStatus = false,
                    sentencesError = "Repo -> ${ex.message ?: "Unknown error"}"
                )
            }
    }

    override suspend fun saveTimeBloquedSend_Email(time: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.IS_BLOQUED_BOTTOM] = time
        }
    }

    override suspend fun getTimeBloquedSend_Email(): Long? {
        val prefs = context.dataStore.data.first()
        return prefs[PreferencesKey.IS_BLOQUED_BOTTOM]
    }

    override suspend fun clearTimeBloquedSend_Email() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKey.IS_BLOQUED_BOTTOM)
        }
    }

    override suspend fun clearCurrentMunicipality() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKey.departmentSelectedId)
            preferences.remove(PreferencesKey.municipioSelect)
            preferences.remove(PreferencesKey.guardarUbicacion)

        }
    }

    // Un data class simple para manejar los datos.
    data class PreferenciaUsuario(
        val departmentId: Int, val municipalityId: Int, val municipio: String, val guardado: Boolean
    )
}