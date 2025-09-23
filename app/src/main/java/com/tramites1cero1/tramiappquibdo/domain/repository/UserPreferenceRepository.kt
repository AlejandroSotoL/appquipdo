package com.tramites1cero1.tramiappquibdo.domain.repository

import com.tramites1cero1.tramiappquibdo.data.model.PeopleInvitated
import com.tramites1cero1.tramiappquibdo.data.model.StatusTransactionDto
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.data.repository.UserPreferencesRepositoryImpl.PreferenciaUsuario
import kotlinx.coroutines.flow.Flow


interface UserPreferencesRepository {
    suspend fun saveDepartmentSelected(departmentID: Int)
    suspend fun saveUbicationPreferences(departmentId: Int,  municipalityId : Int, municipio: String, guardar: Boolean)
    fun getSavedUbication(): Flow<PreferenciaUsuario>
    fun getTheme(): Flow<Boolean>
    suspend fun toggleTheme()
    suspend fun clearCurrentMunicipality()
    fun getModalFormCompleted(): Flow<Boolean>
    suspend fun saveModalFormCompleted(completed: Boolean)
    fun getGuestUserData(): Flow<UserDTO?>
    suspend fun saveGuestUserData(user: UserDTO)
    suspend fun saveSelectionReminders(isActive:Boolean)
    val remindersIsVisibleFlow: Flow<Boolean>
    //Reminders - SendByEmail
    suspend fun saveSelectionSendByEmail(isActive: Boolean)
    val remindersSendIsVisibleFlow: Flow<Boolean>
    suspend fun setRemindersSendVisible(isActive: Boolean)
    //Token-Auth
    suspend fun saveAuthToken(token:String)
    suspend fun getAuthToken(): String

    //Payment List by user
    suspend fun saveEveryPayByBlock(lista : List<StatusTransactionDto>);
    suspend fun getEveryPay() : Flow<List<StatusTransactionDto>>;
    suspend fun setEveryPayByBlock();

    //Create People Invitated
    suspend fun createPeopleInvitates(people: PeopleInvitated) : ValidationResponseDTO
    //IsBloqued - Send Email
    suspend fun saveTimeBloquedSend_Email(time: Long)
    suspend fun getTimeBloquedSend_Email(): Long?
    suspend fun clearTimeBloquedSend_Email()
}

