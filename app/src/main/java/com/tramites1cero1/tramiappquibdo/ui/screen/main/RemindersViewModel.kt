package com.tramites1cero1.tramiappquibdo.ui.screen.main

import CreateReminderDto
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.EmailsDtos.EmailDto
import com.tramites1cero1.tramiappquibdo.data.model.RemindersByUserDto
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.RemindersRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.SendEmailRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.UserPreferencesRepository
import com.tramites1cero1.tramiappquibdo.utils.NotificationReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

// --------------------- EVENTOS ------------------------
sealed interface ReminderEvent {
    data class ShowToast(val message: String) : ReminderEvent
    data object RequestExactAlarmPermission : ReminderEvent
}

// --------------------- UI STATE ------------------------
data class RemindersUiState(
    val isLoading: Boolean = false,
    val reminders: List<RemindersByUserDto> = emptyList(),
    val error: String? = null,
    val expiredReminders: List<RemindersByUserDto> = emptyList(),
    val activeReminders: List<RemindersByUserDto> = emptyList(),
    val activatedReminders: Set<Int> = emptySet(),
    val sessionExpired: Boolean = false,
)

// --------------------- VIEWMODEL ------------------------
@HiltViewModel
class RemindersViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remindersRepo: RemindersRepository,
    private val authRepo: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sendEmailRepository: SendEmailRepository
) : ViewModel() {

    // Preferencias (flujo si enviar emails o no)
    val isActiveSendEmail: StateFlow<Boolean> =
        userPreferencesRepository.remindersSendIsVisibleFlow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Estado de UI
    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState

    // Eventos de un solo uso (Toast, permisos, etc.)
    private val _event = Channel<ReminderEvent>()
    val event: Flow<ReminderEvent> = _event.receiveAsFlow()

    // Usuario logueado
    private val _user = MutableStateFlow<UserDTO?>(null)
    val user: StateFlow<UserDTO?> = _user

    init {
        viewModelScope.launch {
            authRepo.user.collect { currentUser ->
                _user.value = currentUser
                if (currentUser == null || !currentUser.loginStatus) {
                    _uiState.value = _uiState.value.copy(
                        sessionExpired = true,
                        error = "Tu sesión ha expirado. - Cerrando cuenta..",
                        reminders = emptyList(),
                        isLoading = false
                    )
//                    authRepo.clearUserSession()
                } else {
                    loadRemindersByUser(currentUser)
                }
            }
        }
    }

    // --------------------- CREAR RECORDATORIO ------------------------
    fun createReminders(
        expirationDate: String,
        vigenciaDate: String,
        procedureId: Int,
    ) {
        viewModelScope.launch {
            try {
                val id = _user.value?.id ?: run {
                    _uiState.value = _uiState.value.copy(error = "Tenemos problemas.")
                    return@launch
                }

                val structur = CreateReminderDto(
                    expirationDate = expirationDate,
                    vigenciaDate = vigenciaDate,
                    idUser = id,
                    idProcedureMunicipality = procedureId,
                    reminderType = "EMAIL"
                )

                val response = remindersRepo.createReminders(structur)
                if (!response.booleanStatus) return@launch

                _user.value?.let { loadRemindersByUser(it) }
                _uiState.value = _uiState.value.copy(isLoading = false)

            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Tenemos problemas al crear tu recordatorio",
                    isLoading = false
                )
            }
        }
    }

    // --------------------- CLICK EN RECORDATORIO ------------------------
    fun onReminderClicked(recordatorio: RemindersByUserDto, recordatorioId: Int) {
        viewModelScope.launch {
            val title = "Recordatorio: ${recordatorio.idProcedureMunicipalityNavigation?.procedures?.name ?: "PROCESO"}"
            val content = "Recuerda pagar tu factura antes del ${recordatorio.vigenciaDate ?: "fecha límite"}"

            val isScheduled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                scheduleNotification(
                    recordatorioId = recordatorioId,
                    reminderDate = recordatorio.vigenciaDate ?: return@launch,
                    title = title,
                    content = content
                )
            } else false

            // Enviar correo si aplica
            if (isActiveSendEmail.value) sendReminderByEmail(title, content)

            // Actualizar lista de activados
            toggleActivatedReminder(recordatorio.id)

            // Avisar al usuario
            if (isScheduled) {
                _event.send(ReminderEvent.ShowToast("Notificación programada"))
            } else {
                _event.send(ReminderEvent.ShowToast("Necesitas permisos para programar notificaciones exactas"))
                _event.send(ReminderEvent.RequestExactAlarmPermission)
            }
        }
    }

    // --------------------- SCHEDULE NOTIFICATION ------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleNotification(
        recordatorioId: Int,
        reminderDate: String,
        title: String,
        content: String,
        hour: Int = 9,
        minute: Int = 0
    ): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            return false
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val localDate = LocalDate.parse(reminderDate, formatter)
        val triggerAtMillis = localDate
            .atTime(hour, minute)
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EXTRA_TITLE", title)
            putExtra("EXTRA_CONTENT", content)
            putExtra("EXTRA_NOTIFICATION_ID", recordatorioId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            recordatorioId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        return true
    }

    // --------------------- CARGAR RECORDATORIOS ------------------------
    private suspend fun loadRemindersByUser(user: UserDTO) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            val reminders = remindersRepo.getRemindersByUser(user.id)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                checkRemindersExpired(reminders)
            } else {
                _uiState.value = _uiState.value.copy(
                    reminders = reminders,
                    expiredReminders = emptyList(),
                    activeReminders = reminders,
                    isLoading = false,
                    sessionExpired = false
                )
            }
        } catch (e: Exception) {
            Log.e("RemindersViewModel", "Error cargando recordatorios", e)
            _uiState.value = _uiState.value.copy(
                error = "Tenemos problemas con tus recordatorios",
                reminders = emptyList(),
                expiredReminders = emptyList(),
                isLoading = false
            )
        }
    }

    // --------------------- VERIFICAR VENCIDOS ------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkRemindersExpired(reminders: List<RemindersByUserDto>) {
        if (reminders.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                error = "No hay recordatorios disponibles",
                reminders = emptyList(),
                expiredReminders = emptyList(),
                activeReminders = emptyList()
            )
            return
        }

        val currentDate: LocalDate = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        val expiredReminders = mutableListOf<RemindersByUserDto>()
        val activeReminders = mutableListOf<RemindersByUserDto>()

        reminders.forEach { reminder ->
            reminder.vigenciaDate?.let { expirationStr ->
                try {
                    val expirationDate = LocalDate.parse(expirationStr, formatter)
                    if (currentDate.isAfter(expirationDate)) {
                        expiredReminders.add(reminder)
                    } else {
                        activeReminders.add(reminder)
                    }
                } catch (e: Exception) {
                    Log.e("DATE_PARSE", "Error parseando fecha ${reminder.expirationDate}", e)
                }
            } ?: activeReminders.add(reminder)
        }

        _uiState.value = _uiState.value.copy(
            error = null,
            reminders = reminders,
            expiredReminders = expiredReminders,
            activeReminders = activeReminders,
            isLoading = false,
            sessionExpired = false
        )
    }

    // --------------------- HELPERS ------------------------
    private suspend fun sendReminderByEmail(title: String, content: String) {
        try {
            val email = _user.value?.email
            if (!email.isNullOrBlank()) {
                val structur = EmailDto(subject = title, body = content, to = email)
                val result = sendEmailRepository.sendEmail(structur)
                if (!result.booleanStatus) {
                    _uiState.value = _uiState.value.copy(
                        error = "Tenemos problemas con tu recordatorio por correo",
                        isLoading = false
                    )
                }
            } else {
                Log.w("RemindersViewModel", "No se puede enviar correo: usuario sin email")
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Error al enviar el correo.",
                isLoading = false
            )
        }
    }

    private fun toggleActivatedReminder(reminderId: Int?) {
        val updatedActivated = _uiState.value.activatedReminders.toMutableSet()
        val id = reminderId ?: return
        if (updatedActivated.contains(id)) {
            updatedActivated.remove(id)
        } else {
            updatedActivated.add(id)
        }
        _uiState.value = _uiState.value.copy(activatedReminders = updatedActivated)
    }
}
