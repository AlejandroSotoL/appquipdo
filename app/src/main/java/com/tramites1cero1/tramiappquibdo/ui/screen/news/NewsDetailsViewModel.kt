package com.tramites1cero1.tramiappquibdo.ui.screen.news

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tramites1cero1.tramiappquibdo.data.model.NewsDTO
import com.tramites1cero1.tramiappquibdo.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    savedStateHandle: SavedStateHandle)
    : ViewModel() {

    private val _noticiaState = MutableStateFlow<NewsDTO?>(null)
    val noticiaState : StateFlow<NewsDTO?> = _noticiaState
    val encodedUrl: String = savedStateHandle.get<String>("baseUrl") ?: ""
    val baseUrl = URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.toString())
    init {
        val noticiaId : Int = savedStateHandle.get<Int>("noticiaId") ?: 0

        if(noticiaId > 0 && encodedUrl.isNotEmpty()){
            loadNoticia(id = noticiaId, baseUrl = baseUrl)
        }
    }

    private fun loadNoticia(id: Int, baseUrl : String){
        viewModelScope.launch {
            val noticia = newsRepository.getNewById(id, baseUrl)
            _noticiaState.value = noticia
        }
    }
}