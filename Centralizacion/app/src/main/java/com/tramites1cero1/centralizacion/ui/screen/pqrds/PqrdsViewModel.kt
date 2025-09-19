package com.tramites1cero1.centralizacion.ui.screen.pqrds

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.data.model.pqrddto.ActividadEconomicaPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.AsuntoInteres
import com.tramites1cero1.centralizacion.data.model.pqrddto.AtencionPreferencial
import com.tramites1cero1.centralizacion.data.model.pqrddto.Ciudad
import com.tramites1cero1.centralizacion.data.model.pqrddto.Ciudadano
import com.tramites1cero1.centralizacion.data.model.pqrddto.ClasificacionSolicitud
import com.tramites1cero1.centralizacion.data.model.pqrddto.Departamento
import com.tramites1cero1.centralizacion.data.model.pqrddto.DiscapacidadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.Documentos
import com.tramites1cero1.centralizacion.data.model.pqrddto.EscolaridadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GeneroPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GrupoEtnicoPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.GrupoInteresPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.MedioRespuesta
import com.tramites1cero1.centralizacion.data.model.pqrddto.NivelEstratoPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.NivelSisbenPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.PqrdAnonimaPost
import com.tramites1cero1.centralizacion.data.model.pqrddto.PqrdIdentificacionPost
import com.tramites1cero1.centralizacion.data.model.pqrddto.RangoEdadPQRD
import com.tramites1cero1.centralizacion.data.model.pqrddto.Secretaria
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoDocumento
import com.tramites1cero1.centralizacion.data.model.pqrddto.TipoSolicitante
import com.tramites1cero1.centralizacion.data.model.pqrddto.VulnerabilidadPQRD
import com.tramites1cero1.centralizacion.domain.repository.GeneralesRepository
import com.tramites1cero1.centralizacion.domain.repository.PqrdRepository
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import javax.inject.Inject


data class PqrdsUiState(
    val isLoading: Boolean = false,
    val currentStep: Int = 1,
    val isNextButtonEnabled: Boolean = true,
    val showConfirmationSheet: Boolean = false
)
sealed class PqrdsResponseState {
    object LoadingPqrd : PqrdsResponseState()
    data class Success(val token: String) : PqrdsResponseState()
    data class ErrorPqrd(val message: String) : PqrdsResponseState()
    object Empty : PqrdsResponseState()
}

sealed interface PqrdsEvent {
    data class NavigateNextStep(val route: String) : PqrdsEvent
    data object OnBackStep : PqrdsEvent
}

private const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024 // 10 MB

@HiltViewModel
class PqrdsViewModel @Inject constructor(
    private val pqrdRepository: PqrdRepository,
    private val generalesRepository: GeneralesRepository,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _uiState = MutableStateFlow(PqrdsUiState())
    val uiState = _uiState.asStateFlow()

    private val _pqrdState = MutableStateFlow<PqrdsResponseState>(PqrdsResponseState.Empty)
    val pqrdState: StateFlow<PqrdsResponseState> = _pqrdState

    private val _formState = MutableStateFlow(PqrdFormState())
    val formState = _formState.asStateFlow()

    private val _dropDownOptionsState = MutableStateFlow(PqrdDropdownOptionsState())
    val dropDownOptionsState = _dropDownOptionsState.asStateFlow()

    private val _errorState = MutableStateFlow(FormErrorState())
    val errorState: StateFlow<FormErrorState> = _errorState.asStateFlow()

    private val _event = Channel<PqrdsEvent>()
    val event = _event.receiveAsFlow()


    private val allowedMimeTypes = setOf(
        "application/vnd.ms-excel", "application/xls",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/xlsx",
        "application/msword", "application/doc",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/docx",
        "image/jpeg", "image/jpg",
        "image/png",
        "application/pdf",
        "application/rar", "application/zip", "application/x-zip-compressed" // Agregado por compatibilidad
    )



    fun onFileSelected(uri: Uri?) {
        if (uri == null) {
            // Limpia el estado si el usuario cancela la selección
            _formState.update { it.copy(nombreArchivo = null, tipoArchivo = null, contenidoArchivoBase64 = null) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                var fileName: String? = null
                var fileType: String? = null
                var fileSize: Long = 0

                // 2. Obtenemos nombre, tipo y TAMAÑO del archivo sin leerlo por completo
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        fileName = cursor.getString(nameIndex)
                        fileSize = cursor.getLong(sizeIndex)
                    }
                }
                fileType = contentResolver.getType(uri)

                // 3. Validación de TIPO de archivo (MIME Type)
                if (fileType !in allowedMimeTypes) {
                    _errorState.update { it.copy(tipoArchivoError = "Tipo de archivo no permitido.") }
                    return@launch // Detiene la ejecución si el tipo no es válido
                }

                // 4. Validación de TAMAÑO de archivo
                if (fileSize > MAX_FILE_SIZE_BYTES) {
                    _errorState.update { it.copy(tipoArchivoError = "El archivo excede el tamaño máximo de 10 MB.") }
                    return@launch // Detiene la ejecución si el archivo es muy grande
                }

                // 5. Si es válido, limpiamos errores previos y procesamos el archivo
                _errorState.update { it.copy(tipoArchivoError = null) }

                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                val base64String = bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
                inputStream?.close()

                // 6. Actualizamos el estado del formulario en el hilo principal
                withContext(Dispatchers.Main) {
                    _formState.update {
                        it.copy(
                            nombreArchivo = fileName,
                            tipoArchivo = fileType,
                            contenidoArchivoBase64 = base64String
                        )
                    }
                }

            } catch (e: Exception) {
                _errorState.update { it.copy(tipoArchivoError = "Error al procesar el archivo.") }
                e.printStackTrace()
            }
        }
    }

    fun clearFileTypeError() {
        _errorState.update { it.copy(tipoArchivoError = null) }
    }
    fun loadDropdownData(codigoEntidad : String) {
        viewModelScope.launch {
            _dropDownOptionsState.update { it.copy(isLoading = true) }

            try {
                val secretarias = pqrdRepository.listSecretariaEntidad(codigoEntidad)
                val asuntos = pqrdRepository.listAsuntoInteres(codigoEntidad)
                val clasificaciones = pqrdRepository.listClasificacionSolicitud(codigoEntidad)
                val tiposSolicitante = pqrdRepository.listTipoSolicitante(codigoEntidad)
                val atencionPreferencial = pqrdRepository.listAtencionPreferencial(codigoEntidad)
                val medioRespuesta = pqrdRepository.listMedioRespuesta(codigoEntidad)
                val tipoDocumento = pqrdRepository.listTipoDocumento(codigoEntidad)
                val grupoInteresPqrd = pqrdRepository.getListGrupoInteresPQRD(codigoEntidad)
                val discapacidadPQRD = pqrdRepository.listDiscapacidadPQRD(codigoEntidad)
                val grupoEtnicoPQRD = pqrdRepository.getListGrupoEtnicoPQRD(codigoEntidad)
                val generoPQRD = pqrdRepository.listGeneroPQRD(codigoEntidad)
                val rangoEdadPQRD = pqrdRepository.listRangoEdadPQRD(codigoEntidad)
                val actividadEconomicaPQRD = pqrdRepository.listActividadEconomicaPQRD(codigoEntidad)
                val nivelEstractoPQRD = pqrdRepository.listNivelEstractoPQRD(codigoEntidad)
                val nivelSisbenPQRD = pqrdRepository.listNivelSisbenPQRD(codigoEntidad)
                val escolaridadPQRD = pqrdRepository.listEscolaridadPQRD(codigoEntidad)
                val vulnerabilidadPQRD = pqrdRepository.listVulnerabilidadPQRD(codigoEntidad)
                val departamentosResult = generalesRepository.getDepartamentos()

                _dropDownOptionsState.update {
                    it.copy(
                        isLoading = false,
                        secretarias = secretarias,
                        asuntosInteres = asuntos,
                        clasificacionesSolicitud = clasificaciones,
                        tiposSolicitante = tiposSolicitante,
                        atencionesPreferenciales = atencionPreferencial,
                        mediosRespuesta = medioRespuesta,
                        tiposDocumento = tipoDocumento,
                        gruposInteres = grupoInteresPqrd,
                        discapacidades = discapacidadPQRD,
                        gruposEtnicos = grupoEtnicoPQRD,
                        generos = generoPQRD,
                        rangosEdad = rangoEdadPQRD,
                        actividadesEconomicas = actividadEconomicaPQRD,
                        nivelesEstrato = nivelEstractoPQRD,
                        nivelesSisben = nivelSisbenPQRD,
                        escolaridades = escolaridadPQRD,
                        vulnerabilidades = vulnerabilidadPQRD,
                        departamentos = departamentosResult
                    )
                }
            }catch (e: Exception) {
                // Manejar el error (mostrar un mensaje, etc.)
                _dropDownOptionsState.update { it.copy(isLoading = false) }
            }
        }
    }

    // FUNCIONES PASO 1 //

    fun onSecretariaChange(secretaria: Secretaria) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(secretaria = secretaria) }
        _errorState.update { it.copy(secretariaError = false) }
    }
    fun onAsuntoInteresChange(asuntoInteres: AsuntoInteres) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(asuntoInteres = asuntoInteres) }
        _errorState.update { it.copy(asuntoInteresError = false) }
    }
    fun onClasificacionSolicitudChange(clasificacionSolicitud: ClasificacionSolicitud) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(clasificacionSolicitud = clasificacionSolicitud) }
        _errorState.update { it.copy(clasificacionSolicitudError = false) }
    }
    fun onTipoSolicitanteChange(tipoSolicitante: TipoSolicitante) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(tipoSolicitante = tipoSolicitante) }
        _errorState.update { it.copy(tipoSolicitanteError = false) }
    }
    fun onAtencionPreferencialChange(atencionPreferencial: AtencionPreferencial) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(atencionPreferencial = atencionPreferencial) }
        _errorState.update { it.copy(atencionPreferencialError = false) }
    }
    fun onMedioRespuestaChange(medioRespuesta: MedioRespuesta) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(medioRespuesta = medioRespuesta) }
        _errorState.update { it.copy(medioRespuestaError = false) }
    }

    // FUNCIONES PASO 2 //

    fun onTipoDocumentoChange(tipoDocumento: TipoDocumento) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(tipoDocumento = tipoDocumento) }
        _errorState.update { it.copy(tipoDocumentoError = false) }
    }
    fun onIdentificacionChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(identificacion = value) }
        _errorState.update { it.copy(identificacionError = false) }
    }

    fun onPrimerNombreChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(primerNombre = value) }
        _errorState.update { it.copy(primerNombreError = false) }
    }

    fun onSegundoNombreChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(segundoNombre = value) }

    }

    fun onPrimerApellidoChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(primerApellido = value) }
        _errorState.update { it.copy(primerApellidoError = false) }
    }

    fun onSegundoApellidoChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(segundoApellido = value) }
    }

    fun onGrupoInteresChange(grupoInteres: GrupoInteresPQRD) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(grupoInteres = grupoInteres) }
        _errorState.update { it.copy(grupoInteresError = false) }
    }
    fun onDiscapacidadChange(discapacidad: DiscapacidadPQRD) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(discapacidad = discapacidad) }
        _errorState.update { it.copy(discapacidadError = false) }
    }
    fun onGrupoEtnicoChange(grupoEtnico: GrupoEtnicoPQRD) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(grupoEtnico = grupoEtnico) }
        _errorState.update { it.copy(grupoEtnicoError = false) }
    }
    fun onGeneroChange(genero: GeneroPQRD) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(genero = genero) }
        _errorState.update { it.copy(generoError = false) }
    }
    fun onRangoEdadChange(rangoEdad: RangoEdadPQRD) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(rangoEdad = rangoEdad) }
        _errorState.update { it.copy(rangoEdadError = false) }
    }
    fun onActividadEconomicaChange(actividadEconomica: ActividadEconomicaPQRD) {
        _formState.update { it.copy(actividadEconomica = actividadEconomica) }
        _errorState.update { it.copy(actividadEconomicaError = false) }
    }
    fun onNivelEstratoChange(nivelEstrato: NivelEstratoPQRD) {
        _formState.update { it.copy(nivelEstrato = nivelEstrato) }
        _errorState.update { it.copy(nivelEstratoError = false) }
    }
    fun onNivelSisbenChange(nivelSisben: NivelSisbenPQRD) {
        _formState.update { it.copy(nivelSisben = nivelSisben) }
        _errorState.update { it.copy(nivelSisbenError = false) }
    }
    fun onEscolaridadChange(escolaridad: EscolaridadPQRD) {
        _formState.update { it.copy(escolaridad = escolaridad) }
        _errorState.update { it.copy(escolaridadError = false) }
    }
    fun onVulnerabilidadChange(vulnerabilidad: VulnerabilidadPQRD) {
        _formState.update { it.copy(vulnerabilidad = vulnerabilidad) }
        _errorState.update { it.copy(vulnerabilidadError = false) }
    }

    // FUNCIONES PASO 3 //
    fun setPaisName(value: String = "Colombia") {
        _formState.update { it.copy(pais = value) }
    }

    fun onDepartamentoSelected(departamento: Departamento) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        viewModelScope.launch {
            // Actualiza el depto seleccionado y limpia la ciudad anterior
            _formState.update { it.copy(departamento = departamento, ciudad = null) }

            // Muestra el spinner de carga para las ciudades
            _dropDownOptionsState.update { it.copy(isLoadingCiudades = true, ciudades = emptyList()) }

            try {
                val ciudadesResult = generalesRepository.getCiudadesPorDepartamento(departamento.Id)
                _dropDownOptionsState.update { it.copy(ciudades = ciudadesResult) }
            } catch (e: Exception) {
                // Manejar error
            } finally {
                // Oculta el spinner de carga
                _dropDownOptionsState.update { it.copy(isLoadingCiudades = false) }
            }
        }
        _errorState.update { it.copy(tipoSolicitanteError = false) }
    }

    fun onCiudadSelected(ciudad: Ciudad) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(ciudad = ciudad) }
        _errorState.update { it.copy(tipoSolicitanteError = false) }
    }
    fun onRazonSocialChange(value: String) {
        _formState.update { it.copy(razonSocial = value) }
        _errorState.update { it.copy(razonSocialError = false) }
    }
    fun correoElectronicoChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(correoElectronico = value) }
        _errorState.update { it.copy(correoElectronicoError = false) }
    }
    fun onDireccionChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(direccion = value) }
        _errorState.update { it.copy(direccionError = false) }
    }
    fun onTelefonoCelularChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(telefonoCelular = value) }
        _errorState.update { it.copy(telefonoCelularError = false) }
    }
    fun onTelefonoFijoChange(value: String) {
        _formState.update { it.copy(telefonoFijo = value) }
    }
    fun onDescripcionChange(value: String) {
        _uiState.update { it.copy(isNextButtonEnabled = true) }
        _formState.update { it.copy(descripcion = value) }
        if (value.isNotBlank()) {
            _errorState.update { it.copy(descripcionError = false) }
        }
    }

    // FUNCIONES DE EVENTOS //

    fun onTratamientoDatosAcepted(value: Boolean) {
        _formState.update { it.copy(aceptaTratamientoDatos = value) }
    }
    fun onCondicionesUsoAcepted(value: Boolean) {
        _formState.update { it.copy(aceptaCondicionesUso = value) }
    }


    fun onNextStep() {
        when (_uiState.value.currentStep) {
            1 -> {
                if (validateStep1()) {
                    _uiState.update { it.copy(currentStep = 2) }
                }
            }
            2 -> {
                if (validateStep2()) {
                    _uiState.update { it.copy(currentStep = 3) }
                }
            }
        }
    }

    fun onPreviousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update {
                it.copy(
                    currentStep = it.currentStep - 1,
                    isNextButtonEnabled = true
                )
            }
        }
    }

    //pqrds identification validation steps

    private fun validateStep1(): Boolean {
        val form = _formState.value
        val errors = FormErrorState(
            secretariaError = form.secretaria == null,
            asuntoInteresError = form.asuntoInteres == null,
            clasificacionSolicitudError = form.clasificacionSolicitud == null,
            tipoSolicitanteError = form.tipoSolicitante == null,
            atencionPreferencialError = form.atencionPreferencial == null,
            medioRespuestaError = form.medioRespuesta == null
        )
        _errorState.value = errors
        // Devuelve true si ninguna de las propiedades de error es true
        return !errors.secretariaError && !errors.asuntoInteresError &&
                !errors.clasificacionSolicitudError && !errors.tipoSolicitanteError &&
                !errors.atencionPreferencialError && !errors.medioRespuestaError
    }

    private fun validateStep2(): Boolean {
        val form = _formState.value
        // Limpiamos los errores anteriores y seteamos los nuevos
        val errors = _errorState.value.copy(
            tipoDocumentoError = form.tipoDocumento == null,
            identificacionError = form.identificacion.isBlank(),
            primerNombreError = form.primerNombre.isBlank(),
            primerApellidoError = form.primerApellido.isBlank()

        )
        _errorState.value = errors

        return !errors.tipoDocumentoError && !errors.identificacionError &&
                !errors.primerNombreError && !errors.primerApellidoError
    }

    private fun validateStep3(): Boolean {
        val form = _formState.value
        val isEmailValid =
            Patterns.EMAIL_ADDRESS.matcher(form.correoElectronico).matches()

        val errors = _errorState.value.copy(
            departamentoError = form.departamento == null,
            ciudadError = form.ciudad == null,
            razonSocialError = form.razonSocial.isBlank(),
            correoElectronicoError = form.correoElectronico.isBlank() || !isEmailValid,
            descripcionError = form.descripcion.isBlank()
        )
        _errorState.value = errors

        return !errors.departamentoError && !errors.ciudadError && !errors.razonSocialError &&
                !errors.correoElectronicoError && !errors.descripcionError
    }

    fun onFinalizeClicked() {
        if (validateStep3()) {
            _uiState.update { it.copy(showConfirmationSheet = true) }
        }
    }

    fun hideConfirmationSheet() {
        _uiState.update { it.copy(showConfirmationSheet = false) }
    }

    // PQRD IDENTIFICACION SEND //
    fun submitPqrdIdentificacion(codigoEntidad : String) {
        viewModelScope.launch {
            val currentState = _formState.value

            // Construir el objeto Documentos si hay un archivo adjunto
            val documentos = if (currentState.nombreArchivo != null && currentState.contenidoArchivoBase64 != null) {
                Documentos(
                    ContentType = currentState.tipoArchivo ?: "application/octet-stream",
                    Documentos = currentState.contenidoArchivoBase64,
                    NombreArchivo = currentState.nombreArchivo
                )
            } else {
                // Importante: Debes verificar si la API acepta un objeto nulo o vacío
                Documentos(ContentType = "", Documentos = "", NombreArchivo = "")
            }

            // Construir el cuerpo de la petición
            val body = PqrdIdentificacionPost(
                AtencionEspecial = false, // Reemplaza con el valor real // Reemplaza con el valor real
                Ciudad = currentState.ciudad?.NombreCiudad ?: "",
                Ciudadano = Ciudadano(
                    Direccion = currentState.direccion,
                    Email = currentState.correoElectronico,
                    Identificacion = currentState.identificacion,
                    PrimerApellido = currentState.primerApellido,
                    PrimerNombre = currentState.primerNombre,
                    SegundoApellido = currentState.segundoApellido,
                    SegundoNombre = currentState.segundoNombre,
                    Telefono = currentState.telefonoCelular,
                    TipoDocumento = currentState.tipoDocumento?.ID ?: 0
                ),
                CodigoEntidad = codigoEntidad,
                Departamento = currentState.departamento?.NombreDepartamento ?: "",
                Descripcion = currentState.descripcion,
                Documentos = documentos,
                IDActividadEconomica = currentState.actividadEconomica?.ID ?: 0,
                IDAsunto = currentState.asuntoInteres?.ID ?: 0, // Ajusta a tu modelo de datos
                IDAtencionEspecial = currentState.atencionPreferencial?.ID ?: 0,
                IDAtencionPreferencial = currentState.atencionPreferencial?.ID ?: 0,
                IDClasificacion = currentState.clasificacionSolicitud?.ID ?: 0,
                IDDiscapacidad = currentState.discapacidad?.ID ?: 0,
                IDEscolaridad = currentState.escolaridad?.ID ?: 0,
                IDGenero = currentState.genero?.ID ?: 0,
                IDGrupoEtnico = currentState.grupoEtnico?.ID ?: 0,
                IDGrupoInteres = currentState.grupoInteres?.ID ?: 0,
                IDMedioRespuesta = currentState.medioRespuesta?.ID ?: 0,
                IDNivelExtrato = currentState.nivelEstrato?.ID ?: 0,
                IDNivelSisben = currentState.nivelSisben?.ID ?: 0,
                IDRangoEdad = currentState.rangoEdad?.ID ?: 0,
                IDSecretaria = currentState.secretaria?.IDSecretaria ?: 0,
                IDTipoSolicitante = currentState.tipoSolicitante?.ID?: 0,
                IDVulnerabilidad = currentState.vulnerabilidad?.ID ?: 0,
                Pais = currentState.pais,
                RazonSocial = currentState.razonSocial,
                Recepcion = "Móvil", // O el valor que corresponda
            )
            _pqrdState.value = PqrdsResponseState.LoadingPqrd
            try {
                val response = pqrdRepository.insertPQRDIdentificacion(body)
                if (response.Ticket != "") {
                    _pqrdState.value = PqrdsResponseState.Success(
                        response.Ticket
                    )
                }else {
                    _pqrdState.value = PqrdsResponseState.ErrorPqrd(
                        "Error al enviar la solicitud"
                    )
                }
                // Manejar la respuesta (navegar a pantalla de éxito, mostrar mensaje, etc.)
            } catch(e: Exception) {
                // Manejar error de red
                e.printStackTrace()
            }
        }
    }

    private fun validateFormAnonimas(): Boolean {
        val currentState = _formState.value
        val errors = FormErrorState(
            secretariaError = currentState.secretaria == null,
            asuntoInteresError = currentState.asuntoInteres == null,
            clasificacionSolicitudError = currentState.clasificacionSolicitud == null,
            descripcionError = currentState.descripcion.isBlank()
        )

        _errorState.value = errors

        // Devuelve 'true' si no hay errores, 'false' si hay al menos uno.
        return !errors.secretariaError && !errors.asuntoInteresError &&
                !errors.clasificacionSolicitudError && !errors.descripcionError
    }

    // PQRD ANONIMA SEND //
    fun submitPqrdAnonima(codigoEntidad: String) {

        if (!validateFormAnonimas()) {
            return
        }

        viewModelScope.launch {
            val currentState = _formState.value

            // Construir el objeto Documentos si hay un archivo adjunto
            val documentos = if (currentState.nombreArchivo != null && currentState.contenidoArchivoBase64 != null) {
                Documentos(
                    ContentType = currentState.tipoArchivo ?: "application/octet-stream",
                    Documentos = currentState.contenidoArchivoBase64,
                    NombreArchivo = currentState.nombreArchivo
                )
            } else {
                // Importante: Debes verificar si la API acepta un objeto nulo o vacío
                Documentos(ContentType = "", Documentos = "", NombreArchivo = "")
            }

            // Construir el cuerpo de la petición
            val body = PqrdAnonimaPost(
                CodigoEntidad = codigoEntidad, // Reemplaza con el valor real
                Descripcion = currentState.descripcion,
                IDAsuntoInteres = currentState.asuntoInteres?.ID ?: 0, // Ajusta a tu modelo de datos
                IDCLasificacion = currentState.clasificacionSolicitud?.ID ?: 0,
                IDSecretaria = currentState.secretaria?.IDSecretaria ?: 0,
                //Recepcion = "Móvil", // O el valor que corresponda
                Documentos = documentos
            )

            _pqrdState.value = PqrdsResponseState.LoadingPqrd
            try {
               val response = pqrdRepository.insertPQRDAnonima(body)
                if (response.Ticket != "") {
                    _pqrdState.value = PqrdsResponseState.Success(
                        response.Ticket
                    )
                }else {
                    _pqrdState.value = PqrdsResponseState.ErrorPqrd(
                        "Error al enviar la solicitud"
                    )
                }
                // Manejar la respuesta (navegar a pantalla de éxito, mostrar mensaje, etc.)
            } catch(e: Exception) {
                // Manejar error de red
                e.printStackTrace()
            }
        }
    }

    fun resetSubmissionState() {
        _pqrdState.value = PqrdsResponseState.Empty
    }

}