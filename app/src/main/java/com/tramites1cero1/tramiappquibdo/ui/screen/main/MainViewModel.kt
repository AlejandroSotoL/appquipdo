package com.tramites1cero1.tramiappquibdo.ui.screen.main

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.Gson
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.data.model.DocumentTypeDTO
import com.tramites1cero1.tramiappquibdo.data.model.UserDTO
import com.tramites1cero1.tramiappquibdo.data.network.LocationProvider
import com.tramites1cero1.tramiappquibdo.domain.model.InfoTramite
import com.tramites1cero1.tramiappquibdo.domain.model.MunicipalityModel
import com.tramites1cero1.tramiappquibdo.domain.model.MunicipalityProcedure
import com.tramites1cero1.tramiappquibdo.domain.model.QueryField
import com.tramites1cero1.tramiappquibdo.domain.model.TramiteAccion
import com.tramites1cero1.tramiappquibdo.domain.repository.AuthRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.MunicipalityRepository
import com.tramites1cero1.tramiappquibdo.domain.repository.UserPreferencesRepository
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainEvent.*
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.BottomNavBarState
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.BottomNavItem
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.ModalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject



data class MainUiState(
    // Datos principales
    val isLoading: Boolean = true,
    val currentUser: UserDTO? = null,
    val currentIdMunicipality: Int? = 0,
    val currentMunicipalityName: String? = "",

    // Estado de la lista de trámites
    val tramitesPrincipales: List<InfoTramite> = emptyList(),
    val otrosTramites: List<InfoTramite> = emptyList(),
    val isSearchActive: Boolean = false,
    val searchText: String = "",

    val showInDevelopmentDialog: Boolean = false,

    val pendingPanicAction: TramiteAccion.AbrirBotonPanico? = null,
    val modalMode: ModalFormMode? = null,

    val urlToOpen: String? = null,
    val showExitDialog: Boolean = false,

    // darktheme y cambio de ubicacion
    val isDarkTheme : Boolean = false,
    val isSaved : Boolean = false,
    val showChangeLocationDialog : Boolean = false,
    //BottomBar
    val navBarState: BottomNavBarState = BottomNavBarState(),
    val isVisibleReminders : Boolean = false,
    //PQRDS
    val isPqrdVisible : Boolean = false,
)



sealed interface MainEvent {
    data class Navigate(val route: String) : MainEvent
    data class OpenUrl(val url: String) : MainEvent
    data object NavigateToWelcome : MainEvent
    data object FinishApp : MainEvent
    data object OpenDrawer : MainEvent
    data class NavToPqrdAnonima(val route: String) : MainEvent
    data class NavToPqrdIdentificacion(val route: String) : MainEvent
}

enum class ModalFormMode {
    GENERIC,
    PANIC_BUTTON
}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val locationProvider: LocationProvider,
    private val municipalityRepository: MunicipalityRepository,

) : ViewModel(){
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    val isActiveReminders = userPreferencesRepository.remindersIsVisibleFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    fun onSaveSelectionRemiders(isActive: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveSelectionReminders(isActive)
        }
    }

    private val _event = Channel<MainEvent>()
    val event = _event.receiveAsFlow()

    fun getUserConfig(){
        viewModelScope.launch {
            // Ponemos el estado en 'cargando'
            _uiState.update { it.copy(isLoading = true) }

            val user = authRepository.user.first()
            val isDarkTheme = userPreferencesRepository.getTheme().first()
            val userPref = userPreferencesRepository.getSavedUbication().first()

            // 3. Hacemos UNA SOLA actualización al estado con todos los datos.
            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentUser = user,
                    currentMunicipalityName = userPref.municipio,
                    isDarkTheme = isDarkTheme,
                )
            }
        }
    }

    fun goToSettingUser(){
        viewModelScope.launch {
           _event.send(MainEvent.Navigate(AppRoutes.CONFIGURATIONS_USER_SCREEN));
        }
    }


     fun loadSavedMunicipality() {
        viewModelScope.launch {
            val ubicacionPref = userPreferencesRepository.getSavedUbication().first()

            if(ubicacionPref.guardado){
                _uiState.update {
                    it.copy(
                        currentIdMunicipality = ubicacionPref.municipalityId,
                        isSaved = true
                    )
                }
            }
        }
    }
    fun loadBottomBarItems(){
        val navItems = listOf(
            BottomNavItem("Inicio", R.drawable.icohome, AppRoutes.MAINSCREEN),
            BottomNavItem("Noticias", R.drawable.iconoticias, AppRoutes.NEWS_NAV_GRAPH),
            BottomNavItem("Portal", R.drawable.icoportal, "portal_url"),
            BottomNavItem("Historial", R.drawable.icohistorial, AppRoutes.HISTORY_PAY_SCREEN)
        )

        _uiState.update {
            it.copy(
                navBarState = it.navBarState.copy(items = navItems)
            )
        }
    }

    fun onTramiteClicked(tramite: InfoTramite) {

        if (!tramite.isActive) {
            _uiState.update { it.copy(showInDevelopmentDialog = true) }
            return
        }
        viewModelScope.launch {
            when (val accion = tramite.accion) {
                is TramiteAccion.ShowPqrds -> {
                    _uiState.update { it.copy(isPqrdVisible = !it.isPqrdVisible) }
                }
                is TramiteAccion.AbrirUrl -> {
                    val formCompleted = userPreferencesRepository.getModalFormCompleted().first()
                    if (formCompleted) {
                        _event.send(OpenUrl(accion.url))
                    } else {
                        _uiState.update { it.copy(
                            modalMode = ModalFormMode.GENERIC,
                            urlToOpen = accion.url
                        ) }
                    }
                }

                is TramiteAccion.AbrirUrlDirecto -> {
                    _event.send(OpenUrl(accion.url))
                }

                is TramiteAccion.AbrirBotonPanico -> {
                    val savedPanicData = userPreferencesRepository.getGuestUserData().first()
                    if (savedPanicData != null) {
                        sendPanicAlert(
                            ModalData(
                                identificacion = savedPanicData.nationalId,
                                nombresApellidos = savedPanicData.firstName,
                                telefono = savedPanicData.phoneNumber
                            ),
                            accion.baseWhatsappUrl
                        )
                    } else {
                        _uiState.update { it.copy(
                            modalMode = ModalFormMode.PANIC_BUTTON,
                            pendingPanicAction = accion
                        ) }
                    }
                }

                is TramiteAccion.NavegarANativo -> {
                    _event.send(Navigate(accion.ruta))
                }

                is TramiteAccion.NavegarAConsultaImpuesto -> {
                    val route = NavtoTaxes(
                        entityCode = accion.entityCode,
                        queryFields = accion.queryFields,
                        taxId = tramite.idtramite,
                        dataPolicyUrl = accion.dataPolicyUrl,
                        title = tramite.nombre,
                        privacyPolicyUrl = accion.privacyPolicyUrl
                    )
                    _event.send(Navigate(route))
                }

                is TramiteAccion.NavegarAPagoSinValidacion -> {
                    val route = NavtoPSV(
                        taxId = accion.taxId,
                        taxName = accion.taxName
                    )
                    _event.send(MainEvent.Navigate(route))
                }
                is TramiteAccion.NavegarACursos -> {
                    val route = NavToCourses(
                        getUrl = accion.getUrl,
                        postUrl = accion.postUrl
                    )
                    _event.send(Navigate(route))
                }
                is TramiteAccion.NavegarAvenues -> {
                    val route = NavToVenues(
                        getUrl = accion.getUrl,
                        postUrlReservation = accion.postUrlReservation,
                        postUrlCalendar = accion.postUrlCalendar
                    )
                    _event.send(Navigate(route))
                }
            }
        }
    }

    fun onMenuClicked(){
        viewModelScope.launch {
            _event.send(OpenDrawer)
        }
    }

    fun onInDevelopmentDialogDismiss() {
        _uiState.update { it.copy(showInDevelopmentDialog = false) }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            val currentPqrdsState = _uiState.value.isPqrdVisible
            if(currentPqrdsState){
                _uiState.update { it.copy(isPqrdVisible = false) }
            }else {
                _uiState.update { it.copy(showExitDialog = true) }
            }
        }
    }

    // Función de ayuda para construir la ruta de consulta de impuestos
    private fun NavtoTaxes(entityCode: String, queryFields: List<QueryField>, taxId: Int, title: String, dataPolicyUrl: String, privacyPolicyUrl: String
    ): String {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        val encodedDataUrl = URLEncoder.encode(dataPolicyUrl, "UTF-8")
        val fieldsAsJson = Gson().toJson(queryFields)
        val encodedPrivacyUrl = URLEncoder.encode(privacyPolicyUrl, "UTF-8")
        val encodedFields = URLEncoder.encode(fieldsAsJson, "UTF-8")

        return "TaxQueryScreen/$entityCode/$encodedFields/$taxId/$encodedTitle/$encodedDataUrl/$encodedPrivacyUrl"
    }

    fun NavtoPSV(taxId: Int, taxName: String): String {
        val encodedTaxName = URLEncoder.encode(taxName, "UTF-8")
        return "psv_screen/$taxId/$encodedTaxName"
    }

    //  Función de ayuda para construir la ruta
    fun NavToCourses(getUrl: String, postUrl: String): String {

        val encodedGet = URLEncoder.encode(getUrl, "UTF-8")
        val encodedPost = URLEncoder.encode(postUrl, "UTF-8")
        return "CoursesScreen/$encodedGet/$encodedPost"
    }
    //  Función de ayuda para construir la ruta
    fun NavToVenues(getUrl: String, postUrlReservation: String,postUrlCalendar: String  ): String {

        val encodedGet = URLEncoder.encode(getUrl, "UTF-8")
        val encodedPostReservation = URLEncoder.encode(postUrlReservation, "UTF-8")
        val encodedPostCalendar = URLEncoder.encode(postUrlCalendar, "UTF-8")
        return "VenuesScreen/$encodedGet/$encodedPostReservation/$encodedPostCalendar"
    }

    fun onPqrdsClicked(){
        _uiState.update {
            it.copy(
                isPqrdVisible = !it.isPqrdVisible
            )
        }
    }

    fun onPqrdsCancel() {
        _uiState.update {
            it.copy(
                isPqrdVisible = false
            )
        }
    }

    fun navToPqrdsA(){
        val currentPqrdValue = _uiState.value.isPqrdVisible
        if(currentPqrdValue){
            viewModelScope.launch {
                _event.send(NavToPqrdAnonima(AppRoutes.PQRDS_ANONIMAS))
            }
        }

    }


    fun navToPqrdsI(){
        val currentPqrdValue = _uiState.value.isPqrdVisible
        if(currentPqrdValue){
            viewModelScope.launch {
                _event.send(NavToPqrdAnonima(AppRoutes.PQRDS_IDENTIFICACION))
            }
        }
    }


    fun onConfirmExit(){
        viewModelScope.launch {
            val currentStateSave = _uiState.value.isSaved
            if(!currentStateSave){
                _event.send(NavigateToWelcome)
            }else {
                _event.send(FinishApp)
            }
            _uiState.update { it.copy(showExitDialog = false) }
        }
    }

    fun onExitDialogDismissed() {
        _uiState.update { it.copy(showExitDialog = false) }
    }

    fun onSearchTextChanged(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

    fun onSearchToggled() {
        _uiState.update {
            val isCurrentlyActive = it.isSearchActive
            it.copy(isSearchActive = !isCurrentlyActive, searchText = "")
        }
    }

    fun onModalConfirmed(formData: ModalData) {
        viewModelScope.launch {
            val user = authRepository.user.first()

            if (user == null) {
                val guestUserDto = UserDTO(
                    firstName = formData.nombresApellidos.split(" ").firstOrNull() ?: "",
                    lastName = formData.nombresApellidos.split(" ").drop(1).joinToString(" "),
                    nationalId = formData.identificacion,
                    phoneNumber = formData.telefono,
                    email = formData.correo,
                    id = 0,
                    loginStatus = false,
                    address = "",
                    documentType = DocumentTypeDTO(id = 0, name = ""),
                    documentTypeId = 0,
                    password = "",
                    birthDate = ""
                )

                userPreferencesRepository.saveGuestUserData(guestUserDto)
            }

            val currentMode = _uiState.value.modalMode

            when (currentMode) {
                ModalFormMode.PANIC_BUTTON -> {

                    val panicAction = _uiState.value.pendingPanicAction ?: return@launch
                    sendPanicAlert(formData, panicAction.baseWhatsappUrl)
                }
                ModalFormMode.GENERIC -> {
                    userPreferencesRepository.saveModalFormCompleted(true)
                    _uiState.value.urlToOpen?.let { url ->
                        _event.send(MainEvent.OpenUrl(url))
                    }
                }
                null -> {}
            }

            _uiState.update { it.copy(modalMode = null, urlToOpen = null, pendingPanicAction = null) }
        }
    }

    fun onModalDismissed() {
        _uiState.update { it.copy(modalMode = null, urlToOpen = null, pendingPanicAction = null) }
    }

    fun onThemeToggled() {
        viewModelScope.launch {
            userPreferencesRepository.toggleTheme()
            _uiState.update { it.copy(isDarkTheme = !it.isDarkTheme) }
        }
    }


    fun onChangeLocationClicked() {
        _uiState.update { it.copy(showChangeLocationDialog = true) }
    }

    fun onDismissChangeLocationDialog() {
        _uiState.update { it.copy(showChangeLocationDialog = false) }
    }

    fun onConfirmChangeLocation() {
        viewModelScope.launch {
            userPreferencesRepository.clearCurrentMunicipality()
            _event.send(NavigateToWelcome)
            _uiState.update { it.copy(showChangeLocationDialog = false) }
        }
    }

    fun onRouteChanged(currentRoute: String?) {
        _uiState.update {
            it.copy(navBarState = it.navBarState.copy(selectedRoute = currentRoute ?: ""))
        }
    }

    fun onNavItemClicked(item: BottomNavItem, municipalityModel: MunicipalityModel?) {
        viewModelScope.launch {
            when (item.route) {
                AppRoutes.MAINSCREEN -> {
                    _event.send(Navigate(AppRoutes.MAINSCREEN))
                }
                AppRoutes.NEWS_NAV_GRAPH -> {
                    _event.send(Navigate(AppRoutes.NEWS_NAV_GRAPH))
                }
                "portal_url" -> {
                    municipalityModel?.let {
                        _event.send(OpenUrl(it.domain))
                    }
                }
                AppRoutes.HISTORY_PAY_SCREEN -> {
                    _event.send(Navigate(AppRoutes.HISTORY_PAY_SCREEN))
                }
            }
        }
    }

    private suspend fun sendPanicAlert(formData: ModalData, baseWhatsappUrl: String) {
        val location = locationProvider.getCurrentLocation()
        val locationText = if (location != null) {
            "Mi ubicación es: http://googleusercontent.com/maps.google.com/${location.latitude},${location.longitude}"
        } else { "No se pudo obtener la ubicación." }

        val message = """
            ¡ALERTA DE PÁNICO!
            Necesito ayuda urgente.
            Mis datos:
            - Nombre: ${formData.nombresApellidos}
            - Cédula: ${formData.identificacion}
            - Teléfono: ${formData.telefono}
            
            $locationText
        """.trimIndent()

        val whatsappUrl = "$baseWhatsappUrl?text=${URLEncoder.encode(message, "UTF-8")}"
        _event.send(OpenUrl(whatsappUrl))
    }

    fun launchInAppReview(activity: Activity) {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    // Aquí termina el flujo, Google no da feedback del resultado
                }
            } else {
                // Manejo de error: requestReviewFlow falló
            }
        }
    }

}