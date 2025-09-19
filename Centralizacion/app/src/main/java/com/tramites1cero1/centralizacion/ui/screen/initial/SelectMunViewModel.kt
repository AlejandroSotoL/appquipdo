package com.tramites1cero1.centralizacion.ui.screen.initial

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.domain.model.Municipality
import com.tramites1cero1.centralizacion.domain.repository.MunicipalityRepository
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectMunUiState(
    val query: String = "",
    val departmentID: Int = 0,
    val allMunicipalities: List<Municipality> = emptyList(),
    val selectedMunicipality: Municipality? = null,
    val showDropdown: Boolean = false,
    val guardarUbicacion: Boolean = false,
)

sealed interface SelectMunEvent {
    data object NavigateToMain : SelectMunEvent
    data object OnBackClicked : SelectMunEvent
}

@HiltViewModel
class SelectMunViewModel @Inject constructor(
    private val municipalityRepository: MunicipalityRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectMunUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<SelectMunEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    val filteredMunicipalities: StateFlow<List<Municipality>> =
        _uiState.map { it.query }
            .combine(_uiState.map { it.allMunicipalities }) { query, municipalities ->
                if (query.isBlank() || municipalities.any { it.name == query }) {
                    municipalities
                } else {
                    municipalities.filter { it.name.contains(query, ignoreCase = true) }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    init {
        viewModelScope.launch {
            val departmentId: Int? = savedStateHandle.get("departmentId")
            if (departmentId != null) {
                _uiState.update { it.copy(departmentID = departmentId) }
                loadMunicipalities(departmentId)
            }
        }
    }

    private fun loadMunicipalities(departmentID: Int) {
        viewModelScope.launch {
            val municipalities = municipalityRepository.getMunicipalitiesByDepartment(departmentID)
            _uiState.update { it.copy(allMunicipalities = municipalities) }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { currentState ->
            currentState.copy(
                query = newQuery,
                selectedMunicipality = if (currentState.selectedMunicipality?.name != newQuery) null else currentState.selectedMunicipality,
                showDropdown = true
            )
        }
    }

    fun onMunicipalitySelected(municipio: Municipality) {
        _uiState.update {
            it.copy(
                selectedMunicipality = municipio,
                query = municipio.name,
                showDropdown = false
            )
        }
    }

    fun onFocusChanged(isFocused: Boolean) {
        _uiState.update { it.copy(showDropdown = isFocused) }
    }

    fun onSaveUbicationChange(guardar: Boolean) {
        _uiState.update { it.copy(guardarUbicacion = guardar) }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            _uiState.value.selectedMunicipality?.let { municipio ->
                userPreferencesRepository.saveUbicationPreferences(
                    departmentId = _uiState.value.departmentID,
                    municipalityId = municipio.id,
                    municipio = municipio.name,
                    guardar = _uiState.value.guardarUbicacion
                )
                _navigationEvent.send(SelectMunEvent.NavigateToMain)
            }
        }
    }
}