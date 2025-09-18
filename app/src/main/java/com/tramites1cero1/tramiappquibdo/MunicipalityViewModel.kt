package com.tramites1cero1.tramiappquibdo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.domain.model.InfoTramite
import com.tramites1cero1.tramiappquibdo.domain.model.MunicipalityModel
import com.tramites1cero1.tramiappquibdo.domain.model.MunicipalityProcedure
import com.tramites1cero1.tramiappquibdo.domain.repository.MunicipalityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MunicipalityUiState {
    object Loading : MunicipalityUiState()
    data class Success(
        val data: MunicipalityModel,
        val tramitesPrincipales: List<InfoTramite> = emptyList(),
        val otrosTramites: List<InfoTramite> = emptyList(),
        val onlyProcedures : List<MunicipalityProcedure> = emptyList()
    ) : MunicipalityUiState()
    data class Error(val message: String) : MunicipalityUiState()
    object Empty : MunicipalityUiState()
}

@HiltViewModel
class MunicipalityViewModel @Inject constructor(
    private val repository: MunicipalityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MunicipalityUiState>(MunicipalityUiState.Empty)
    val uiState : StateFlow<MunicipalityUiState> = _uiState

    fun loadMunicipalityData(municipalityId: Int) {
        viewModelScope.launch {
            _uiState.value = MunicipalityUiState.Loading
            val result = repository.getMunicipalityData(municipalityId)
            val procedimientos = result.municipalityProcedures
            procedimientos.forEach { proc ->
                println("EJEMPLO ID:${proc.id}, Nombre trámite: ${proc.procedures?.id}")
            }

            // Validamos si la carga fue exitosa basándonos en tu lógica de error
            if (result.idMunicipio != 0) {
                _uiState.value = MunicipalityUiState.Success(
                    data = result,
                    onlyProcedures = result.municipalityProcedures,
                    tramitesPrincipales = result.tramitesPrincipales,
                    otrosTramites = result.otrosTramites)
            } else {
                _uiState.value = MunicipalityUiState.Error("No se pudieron cargar los datos del municipio.")
            }
        }
    }
}