package com.tramites1cero1.centralizacion.ui.screen.initial

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.centralizacion.data.model.CarouselImage
import com.tramites1cero1.centralizacion.domain.model.Department
import com.tramites1cero1.centralizacion.domain.repository.DepartmentRepository
import com.tramites1cero1.centralizacion.domain.repository.RemoteConfigRepository
import com.tramites1cero1.centralizacion.domain.repository.UserPreferencesRepository
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WelcomeScreenState(
    val areAnimationsVisible: Boolean = false,
    val searchQuery: String = "",
    val selectedDepartment: String? = "",
    val departments: List<Department> = emptyList(),
    val isDropdownVisible: Boolean = false,
    val carouselImages: List<CarouselImage> = emptyList()

)

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val departmentRepository: DepartmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeScreenState())

    val filteredDepartments: StateFlow<List<Department>> =
        _uiState.map { it.searchQuery }
            .combine(_uiState.map { it.departments }) { query, departments ->
                if (query.isBlank() || departments.any { it.name == query }) {
                    departments
                } else {
                    departments.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                }
            }.stateIn( // Convierte el Flow resultante en un StateFlow
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<String>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadDepartments()
        triggerAnimations()
        loadCarouselImages()
    }

    private fun loadCarouselImages() {
        viewModelScope.launch {
            val images = remoteConfigRepository.getCarouselImages()
            _uiState.update { it.copy(carouselImages = images) }
        }
    }

    fun loadDepartments() {
        viewModelScope.launch {
            try {
                val result = departmentRepository.getDepartments()
                _uiState.update { it.copy(departments = result) }
                Log.e("Departments", "Departments: ${result}")
            } catch (e: Exception) {
                Log.e("Departments", "Error: ${e.message}")
            }
        }
    }


    fun onQueryChanged(newQuery: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = newQuery,
                selectedDepartment = if (currentState.selectedDepartment != newQuery) null else currentState.selectedDepartment,
                isDropdownVisible = true
            )
        }
    }

    fun onDepartmentSelected(department: Department) {
        // Actualizamos el estado de la UI
        _uiState.update {
            it.copy(
                searchQuery = department.name,
                selectedDepartment = department.name,
                isDropdownVisible = false
            )
        }
        // Lanzamos el evento de navegación
        viewModelScope.launch {
            userPreferencesRepository.saveDepartmentSelected(department.id)
            _navigationEvent.send("${AppRoutes.SELECT_MUN_SCREEN}/${department.id}")
        }
    }


    fun onDropdownFocusChanged(isFocused: Boolean) {
        _uiState.update {
            it.copy(isDropdownVisible = isFocused)
        }
    }


    private fun triggerAnimations() {
        viewModelScope.launch {
            delay(500) // Un delay más corto para que la app se sienta más rápida
            _uiState.update {
                it.copy(areAnimationsVisible = true)
            }
        }
    }


}