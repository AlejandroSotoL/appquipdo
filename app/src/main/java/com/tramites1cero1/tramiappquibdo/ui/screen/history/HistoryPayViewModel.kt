package com.tramites1cero1.tramiappquibdo.ui.screen.history

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.StatusTransactionDto
import com.tramites1cero1.tramiappquibdo.data.model.ValidationResponseDTO
import com.tramites1cero1.tramiappquibdo.data.model.historyPaymentDTOs.PaymentHistoryListDTO
import com.tramites1cero1.tramiappquibdo.domain.repository.HistoryPayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

@HiltViewModel
class HistoryPayViewModel @Inject constructor(
    private val _payHistoryPayRepository: HistoryPayRepository
) : ViewModel() {

    // 🔹 Mensajes tipados para UI
    sealed class UiMessage(val text: String) {
        class Success(text: String) : UiMessage(text)
        class Error(text: String) : UiMessage(text)
        class Warning(text: String) : UiMessage(text)
        class Info(text: String) : UiMessage(text)
    }

    private val _uiMessages = MutableStateFlow<UiMessage?>(null)
    val uiMessages: StateFlow<UiMessage?> = _uiMessages

    // Historial de pagos
    private val _historyPaymentsByUser = MutableStateFlow<PaymentHistoryListDTO>(emptyList())
    val historyPaymentsByUser: StateFlow<PaymentHistoryListDTO> = _historyPaymentsByUser

    // Estados disponibles (traídos de la BD/API)
    private val _statusTypes = MutableStateFlow<List<StatusTransactionDto>>(emptyList())
    val statusTypes: StateFlow<List<StatusTransactionDto>> = _statusTypes

    // Mapping dinámico: nombre → id
    private val statusMapping: Map<String, Set<Int>>
        get() = historyPaymentsByUser.value
            .groupBy { it.statusType.uppercase() }
            .mapValues { entry -> entry.value.map { it.idStatusType }.toSet() }

    // Errores
    private val existedError = mutableStateOf<ValidationResponseDTO?>(null)
    val isErrorExisted: State<ValidationResponseDTO?> = existedError

    private val existedErrorStatusPayment = mutableStateOf<ValidationResponseDTO?>(null)
    val isErrorExistedStatus: State<ValidationResponseDTO?> = existedErrorStatusPayment

    private val _informationStatus = MutableStateFlow<Map<Int, StatusTransactionDto>>(emptyMap())
    val informationStatus: StateFlow<Map<Int, StatusTransactionDto>> = _informationStatus

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val semaphore = kotlinx.coroutines.sync.Semaphore(3)

    fun bringInformationByRepository(id: Int) {
        viewModelScope.launch {
            if (id <= 0) {
                postMessage("Id de usuario inválido", UiMessage.Error("Id de usuario inválido"))
                existedError.value = ValidationResponseDTO(
                    sentencesError = "Id de usuario inválido",
                    booleanStatus = false,
                    codeStatus = 400
                )
                return@launch
            }

            _isLoading.value = true
            try {
                val history = _payHistoryPayRepository.getHistoryPayByUser(id)
                if (history.isNullOrEmpty()) {
                    postMessage("Historial vacío para usuario $id", UiMessage.Warning("Historial vacío"))
                    _historyPaymentsByUser.value = emptyList()
                    return@launch
                }

                _historyPaymentsByUser.value = history

                history.forEach { servicio ->
                    postMessage(
                        "Historial id=${servicio.id}, codigoEntidad=${servicio.codigoEntidad}, factura=${servicio.factura}, idimpuesto=${servicio.idimpuesto}, idStatusType=${servicio.idStatusType}",
                        UiMessage.Info("Historial cargado")
                    )

                    if (!servicio.codigoEntidad.isNullOrBlank() &&
                        !servicio.factura.isNullOrBlank() &&
                        !servicio.idimpuesto.isNullOrBlank()
                    ) {
                        launch {
                            semaphore.withPermit {
                                bringStatusAboutPayment(
                                    idHistory = servicio.id,
                                    CodigoEntidad = servicio.codigoEntidad,
                                    Factura = servicio.factura,
                                    IDImpuesto = servicio.idimpuesto
                                )
                            }
                        }
                    } else {
                        postMessage("Historial con campos nulos o vacíos -> id=${servicio.id}",
                            UiMessage.Warning("Campos incompletos"))
                    }
                }
            } catch (e: Exception) {
                postMessage("Error cargando historial: ${e.message}", UiMessage.Error("Error cargando historial"))
                existedError.value = ValidationResponseDTO(
                    sentencesError = "Error cargando historial",
                    booleanStatus = false,
                    codeStatus = 500
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun bringStatusAboutPayment(
        idHistory: Int,
        CodigoEntidad: String?,
        Factura: String?,
        IDImpuesto: String?
    ) {
        viewModelScope.launch {
            val error = validateHistoryParams(idHistory, CodigoEntidad, Factura, IDImpuesto)
            if (error != null) {
                postMessage(error.sentencesError ?: "Error de validación", UiMessage.Error(error.sentencesError ?: "Error"))
                existedErrorStatusPayment.value = error
                return@launch
            }

            try {
                val result = _payHistoryPayRepository.getInformationAboutPayment(
                    CodigoEntidad = CodigoEntidad!!,
                    Factura = Factura!!,
                    IDImpuesto = IDImpuesto!!
                )

                if (result == null) {
                    postMessage("Respuesta nula para idHistory=$idHistory", UiMessage.Error("Respuesta nula"))
                    existedErrorStatusPayment.value = ValidationResponseDTO(
                        booleanStatus = false,
                        sentencesError = "Tenemos problemas"
                    )
                    return@launch
                }

                _informationStatus.value = _informationStatus.value + (idHistory to result)
                val currentPayment = _historyPaymentsByUser.value.firstOrNull { it.id == idHistory }
                if (currentPayment == null) {
                    Log.e("HistoryVM", "No se encontró el historial con id=$idHistory")
                    return@launch
                }
                val estadoApi = result.EstadoTransaccion.trim().uppercase()
                val estadoActual = currentPayment.statusType.trim().uppercase()
                val posiblesIds = statusMapping[estadoApi] ?: emptySet()
                val newStatusId = if (posiblesIds.contains(currentPayment.idStatusType)) {
                    currentPayment.idStatusType
                } else {
                    posiblesIds.firstOrNull() ?: 0
                }

                if (currentPayment.statusType != result.EstadoTransaccion) {
                    postMessage(
                        "El pago ${currentPayment.id} pasa de ${currentPayment.statusType} (id=${currentPayment.idStatusType}) a ${result.EstadoTransaccion} (nuevo id=$newStatusId)",
                        UiMessage.Info("Cambio de estado detectado")
                    )
                } else {
                    postMessage(
                        "El pago ${currentPayment.id} mantiene el mismo estado '${result.EstadoTransaccion}'",
                        UiMessage.Info("Estado sin cambios")
                    )
                }
                if (newStatusId == 0) {
                    postMessage("El estado recibido '$estadoApi' no está mapeado", UiMessage.Warning("Estado no mapeado"))
                    return@launch
                }

                if (estadoApi != estadoActual || newStatusId != currentPayment.idStatusType) {
                    val response = updateHistoryStatusInApi(idHistory, newStatusId)
                    if (!response.booleanStatus) {
                        postMessage("Error al actualizar estado: ${response.sentencesError}", UiMessage.Error("Error actualizando estado"))
                        existedErrorStatusPayment.value = response
                    } else {
                        postMessage("Estado del pago ${currentPayment.id} actualizado correctamente a $estadoApi",
                            UiMessage.Success("Actualización exitosa"))
                    }
                } else {
                    postMessage("Ya está sincronizado", UiMessage.Info("Sincronización completa"))
                }

            } catch (ex: Exception) {
                postMessage("Excepción al traer estado: ${ex.message}", UiMessage.Error("Excepción en consulta"))
                existedErrorStatusPayment.value = ValidationResponseDTO(
                    booleanStatus = false,
                    sentencesError = ex.message ?: "Error desconocido"
                )
            }
        }
    }

    private fun validateHistoryParams(id: Int, entidad: String?, factura: String?, impuesto: String?): ValidationResponseDTO? {
        if (id <= 0) return ValidationResponseDTO(500, false, "Id inválido")
        if (entidad.isNullOrBlank() || factura.isNullOrBlank() || impuesto.isNullOrBlank()) {
            return ValidationResponseDTO(500,false, "Datos incompletos para consultar el estado")
        }
        return null
    }

    private fun postMessage(msg: String, type: UiMessage) {
        Log.d("HistoryVM", "${type::class.simpleName}: $msg")
        _uiMessages.value = type
    }

    private suspend fun updateHistoryStatusInApi(
        idHistory: Int,
        newStatus: Int
    ): ValidationResponseDTO {
        return try {
            _payHistoryPayRepository.updateHistoryStatus(idHistory, newStatus)
        } catch (e: Exception) {
            Log.e("HistoryVM", "Error actualizando estado -> ${e.message}", e)
            ValidationResponseDTO(
                booleanStatus = false,
                sentencesError = e.message ?: "Error actualizando estado"
            )
        }
    }

    suspend fun deleteHistoryByUser(idUser: Int, idHistory: Int): ValidationResponseDTO {
        if (idUser <= 0 || idHistory <= 0) {
            Log.w("HistoryVM", "Parámetros inválidos para eliminar historial -> user=$idUser, history=$idHistory")
            return ValidationResponseDTO(
                sentencesError = "Tenemos problemas para eliminar tu pago, Intenta más tarde.",
                booleanStatus = false,
                codeStatus = 204
            )
        }
        return try {
            _payHistoryPayRepository.deleteHistoryByUser(idUser, idHistory)
        } catch (e: Exception) {
            Log.e("HistoryVM", "Error eliminando historial -> ${e.message}", e)
            ValidationResponseDTO(
                sentencesError = "Tenemos problemas para eliminar tu pago, Intenta más tarde.",
                booleanStatus = false,
                codeStatus = 204
            )
        }
    }
}
