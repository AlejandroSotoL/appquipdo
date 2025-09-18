package com.tramites1cero1.tramiappquibdo.ui.screen.news

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.NewsDTO
import com.tramites1cero1.tramiappquibdo.domain.repository.NewsRepository
import com.tramites1cero1.tramiappquibdo.ui.screen.main.components.BottomNavBarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

// 1. (Opcional pero recomendado) Renombramos NewsUiState a algo más interno
data class NewsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val navBarState: BottomNavBarState = BottomNavBarState(),
)

// 2. CREAMOS UN ESTADO SELLADO PARA LA PANTALLA
// Esto representa todos los posibles estados que el contenido principal de tu UI puede tener.
sealed interface NewsScreenState {
    object Loading : NewsScreenState
    data class Success(val news: List<NewsDTO>) : NewsScreenState
    data class Error(val message: String) : NewsScreenState
}


@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    // _uiState ahora es privado y se enfoca solo en la carga y el error

    private val _loadingState = MutableStateFlow(NewsUiState(isLoading = true))
    val uiState: StateFlow<NewsUiState> = _loadingState.asStateFlow()
    private val _todasLasNoticias = MutableStateFlow<List<NewsDTO>>(emptyList())
    private val _selectedMonth = MutableStateFlow<Int?>(null)
    private val _query = MutableStateFlow("")

    // Mantenemos estos públicos para que la UI controle los filtros
    val query: StateFlow<String> = _query.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val availableMonths: StateFlow<List<Pair<Int, String>>> = _todasLasNoticias.map { noticias ->
        // ... (lógica sin cambios)
        val currentYear = LocalDateTime.now().year
        val spanishLocale = Locale("es", "ES")

        noticias
            .mapNotNull { it.fechaHoraNoticia?.let { dateStr ->
                try {
                    LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                } catch (e: Exception) { null }
            }}
            .filter { it.year == currentYear }
            .map { it.monthValue }
            .distinct()
            .sorted()
            .map { monthNumber ->
                val monthName = Month.of(monthNumber).getDisplayName(TextStyle.FULL_STANDALONE, spanishLocale)
                monthNumber to monthName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(spanishLocale) else it.toString() }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    val noticiasFiltradas: StateFlow<List<NewsDTO>> =
        combine(_todasLasNoticias, _selectedMonth, _query) { noticias, month, texto ->
            // ... (lógica sin cambios)
            val currentYear = LocalDateTime.now().year
            val noticiasDelAnioActual = noticias.filter {
                it.fechaHoraNoticia?.let { dateStr ->
                    try {
                        LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME).year == currentYear
                    } catch (e: Exception) { false }
                } ?: false
            }
            val noticiasPorMes = if (month == null) {
                noticiasDelAnioActual
            } else {
                noticiasDelAnioActual.filter {
                    it.fechaHoraNoticia?.let { dateStr ->
                        try {
                            LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME).monthValue == month
                        } catch (e: Exception) { false }
                    } ?: false
                }
            }
            if (texto.isBlank()) {
                noticiasPorMes
            } else {
                noticiasPorMes.filter {
                    it.title.contains(texto, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // 3. NUEVO STATEFLOW PÚBLICO PARA LA UI
    // Combina el estado de carga/error con la lista filtrada para darle a la UI un único estado que observar.
    @RequiresApi(Build.VERSION_CODES.O)
    val screenState: StateFlow<NewsScreenState> =
        combine(_loadingState, noticiasFiltradas) { loadingState, filteredNews ->
            when {
                loadingState.isLoading -> NewsScreenState.Loading
                loadingState.error != null -> NewsScreenState.Error(loadingState.error)
                else -> NewsScreenState.Success(filteredNews)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NewsScreenState.Loading)




    fun onMonthSelected(month: Int?) {
        _selectedMonth.value = month
    }

    fun onQueryChange(nuevoQuery: String) {
        _query.value = nuevoQuery
    }

    fun loadNewsForMunicipality(baseUrl: String) {
        if (_todasLasNoticias.value.isNotEmpty()) {
            _loadingState.value = NewsUiState(isLoading = false)
            return
        }

        viewModelScope.launch {
            _loadingState.value = NewsUiState(isLoading = true)
            try {
                val newsList = newsRepository.getNews(baseUrl)
                _todasLasNoticias.value = newsList
                _selectedMonth.value = null
                _loadingState.value = NewsUiState(isLoading = false)
            } catch (e: Exception) {
                _loadingState.value = NewsUiState(isLoading = false, error = "Error al cargar noticias: ${e.message}")
            }
        }
    }
}