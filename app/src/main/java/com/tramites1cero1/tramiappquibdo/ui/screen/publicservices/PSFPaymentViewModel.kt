package com.tramites1cero1.tramiappquibdo.ui.screen.publicservices
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.FormPublicServiceStateDto
import com.tramites1cero1.tramiappquibdo.data.model.PayerDto
import com.tramites1cero1.tramiappquibdo.data.model.TransactionFintech
import com.tramites1cero1.tramiappquibdo.data.model.TransactionResponse
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.network.FintechPayments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PSFPaymentViewModel @Inject constructor(
    private val fintechPayments: FintechPayments
)  : ViewModel() {

    private val _transactionData = mutableStateOf<TransactionFintech?>(null)

    private val _transactionResponse = MutableStateFlow<TransactionResponse?>(null)
    // Externo: solo lectura (inmutable)
    val transactionResponse: StateFlow<TransactionResponse?> = _transactionResponse

    private val _uiState = mutableStateOf(FormPublicServiceStateDto())
    val uiState: State<FormPublicServiceStateDto> = _uiState

    // funcion para cargar pagador aa pai transaccion
    fun cargarPagador(transactionData: TransactionFintech, id: Int) {
        _transactionData.value = transactionData
        viewModelScope.launch {
            try {
                val response = fintechPayments.transactionFintech(transactionData, id)
                _transactionResponse.value = response
                // Puedes mostrar un mensaje de éxito o navegar a otra pantalla
            } catch (e: Exception) {
                // Maneja errores (por ejemplo, mostrar un mensaje con Snackbar)
                Log.e("ViewModel", "Error al realizar transacción: ${e.message}")
            }

        }
    }

    fun clearReponseTransaction(){
        _transactionResponse.value = null
        _transactionData.value = null

    }

    fun updateField(field: (FormPublicServiceStateDto) -> FormPublicServiceStateDto) {
        _uiState.value = field(_uiState.value)
    }

    fun setFacturaYValorpagar(factura: String?, valorpagar: String?) {
        _uiState.value = _uiState.value.copy(
            factura = factura ?: "",
            valorPagar = valorpagar ?: ""
        )
    }

    fun enviarDatos(municipalityId: Int?) {
        val state = _uiState.value

        val payer = PayerDto(
            direccion = state.direccion,
            documento = state.documento,
            dv = 0,
            email = state.email,
            nombre_Completo = "String",
            primerapellido = state.primerApellido,
            primernombre = state.primerNombre,
            segundoapellido = state.segundoApellido,
            segundonombre = state.segundoNombre,
            telefono = state.telefono,
            tipoDocumento = state.tipoDocumento
        )

        val datos = TransactionFintech(
            idTramite = state.idTramite,
            pagador = payer,
            fuentePago = state.fuentePago,
            tipoImplementacion = state.tipoImplementacion,
            estado_Url = state.estadoUrl,
            url = state.url,
            valorPagar = state.valorPagar.toIntOrNull() ?: 0,
            factura = state.factura ?: "",
            referencia = state.referencia,
            descripcion = state.descripcion
        )
        // cargar pagador
        cargarPagador(
            datos,
           id = municipalityId ?: 0
        )
    }

    fun setUserData(user: UserDTO) {
        _uiState.value = _uiState.value.copy(
            tipoDocumento = user.documentTypeId ?: 3,
            documento = user.documentTypeId.toString() ?: "",
            primerNombre = user.firstName ?: "",
            segundoNombre = user.middleName ?: "",
            primerApellido = user.lastName ?: "",
            segundoApellido = user.secondLastName ?: "",
            direccion = user.address ?: "",
            telefono = user.phoneNumber ?: "",
            email = user.email ?: ""

        )
    }
}