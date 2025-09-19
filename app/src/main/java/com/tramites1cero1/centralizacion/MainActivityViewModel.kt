package com.tramites1cero1.centralizacion

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.data.network.ConnectivityObserver
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainActivityState(
    val isLoading: Boolean = true,
    val startDestination: String = "",
    val municipalityId: Int? = 0,
    val isDarkTheme: Boolean = false,
)



@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private var networkDebounceJob: Job? = null
    private val _uiState = MutableStateFlow(MainActivityState())
    val uiState = _uiState.asStateFlow()

    val networkStatus: StateFlow<ConnectivityObserver.Status> = connectivityObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConnectivityObserver.Status.Available
        )

    private val _showNoConnectionDialog = MutableStateFlow(false)
    val showNoConnectionDialog = _showNoConnectionDialog.asStateFlow()

    init {

        // Empezamos a observar la red en tiempo real con el temporizador
        viewModelScope.launch {
            networkStatus.collect { status ->
                networkDebounceJob?.cancel()

                if (status == ConnectivityObserver.Status.Available) {
                    // Si la conexión está disponible, el diálogo debe estar oculto
                    _showNoConnectionDialog.value = false
                } else {
                    // Si la conexión se pierde, iniciamos un temporizador
                    networkDebounceJob = launch {
                        delay(4000) // Esperamos 4 segundos
                        _showNoConnectionDialog.value = true
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            combine(
                userPreferencesRepository.getTheme(),
                userPreferencesRepository.getSavedUbication()
            ) { isDark, ubicacionPref ->
                var destination: String? = ""

                if (ubicacionPref.guardado) {
                    destination = AppRoutes.MAIN_NAV_GRAPH
                } else {
                    destination = AppRoutes.INITIAL_NAV_GRAPH
                }

                // 4. Actualizamos el estado con toda la información.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        startDestination = destination,
                        municipalityId = ubicacionPref.municipalityId,
                        isDarkTheme = isDark
                    )
                }

            }.collect()
        }
    }

    fun dismissNoConnectionDialog() {
        _showNoConnectionDialog.value = false
    }
}

