package com.tramites1cero1.centralizacion.ui.screen.news

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tramites1cero1.centralizacion.MunicipalityUiState
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.data.model.NewsDTO
import com.tramites1cero1.centralizacion.domain.model.Design
import com.tramites1cero1.centralizacion.ui.components.TopbarNavigation
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.main.MainEvent.Navigate
import com.tramites1cero1.centralizacion.ui.screen.main.MainEvent.OpenUrl
import com.tramites1cero1.centralizacion.ui.screen.main.MainViewModel
import com.tramites1cero1.centralizacion.ui.screen.main.components.BottomNavBar
import com.tramites1cero1.centralizacion.ui.screen.main.components.BottomNavBarActions
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.White
import com.tramites1cero1.centralizacion.utils.abrirURL
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsScreen(
    viewModel : NewsViewModel = hiltViewModel(),
    navController: NavController,
    escudoUrl : String,
    nombreAlcaldia : String,
    newsUrl: String) {

    var expanded by remember { mutableStateOf(false) }
    var selectedMonthText by remember { mutableStateOf("Todos los meses") }

    val query by viewModel.query.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val availableMonths by viewModel.availableMonths.collectAsState()

    val noticiasFiltradas by viewModel.noticiasFiltradas.collectAsState()

    val screenState by viewModel.screenState.collectAsState()

    var isSearchBarVisible by remember { mutableStateOf(false) }
    var isFilterMenuExpanded by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = newsUrl) {
        viewModel.loadNewsForMunicipality(newsUrl)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "", textAlign = TextAlign.Center, modifier = Modifier.padding(start = 15.dp)) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(40.dp))
                            .size(35.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Menú",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
    ) {
        innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Column(modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "Noticias de $nombreAlcaldia", style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(15.dp))
                    Box {
                        OutlinedButton(
                            onClick = { isFilterMenuExpanded = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues.Absolute(left = 10.dp, right = 10.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                                ) {
                                Text(text = "Filtrar", color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(start = 10.dp), style = MaterialTheme.typography.bodyMedium)
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(35.dp)
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 10.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            modifier = Modifier.fillMaxWidth(),
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // Opción para limpiar el filtro
                            DropdownMenuItem(
                                text = { Text("Todos los meses") },
                                onClick = {
                                    viewModel.onMonthSelected(null)
                                    selectedMonthText = "Todos los meses"
                                    expanded = false
                                }
                            )
                            // Meses disponibles
                            availableMonths.forEach { (monthNumber, monthName) ->
                                DropdownMenuItem(
                                    text = { Text(monthName) },
                                    onClick = {
                                        viewModel.onMonthSelected(monthNumber)
                                        selectedMonthText = monthName
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { isSearchBarVisible = !isSearchBarVisible },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues.Absolute(left = 10.dp, right = 10.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(text = "Buscar", color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 10.dp),
                                style = MaterialTheme.typography.bodyMedium)
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(35.dp)
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 10.dp)
                            )
                        }

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ----- CAMBIO 4: Campo de búsqueda condicional -----
                AnimatedVisibility(visible = isSearchBarVisible) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        label = { Text("Buscar noticias", style = MaterialTheme.typography.bodyMedium) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                            focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                when(val state = screenState){
                    is NewsScreenState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is NewsScreenState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                    is NewsScreenState.Success -> {
                        // AQUÍ ESTÁ LA LÓGICA CLAVE
                        if (state.news.isEmpty()) {
                            Text(
                                text = "No se han encontrado noticias del año actual.",
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // Muestra tu LazyColumn con las noticias
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(noticiasFiltradas) {
                                    NewsCard(noticia = it, navController = navController, escudoUrl = escudoUrl, newsUrl = newsUrl)
                                }
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "No se han encontrado noticias.",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun NewsCard(noticia: NewsDTO, navController: NavController, escudoUrl : String, newsUrl: String) {

    val sharepointBaseUrl = newsUrl
    // Función que busca la URL de la imagen dentro del HTML
    fun findImageUrlInContent(htmlContent: String?): String? {
        if (htmlContent.isNullOrBlank()) return null
        return try {
            // Jsoup parsea el HTML y busca la primera etiqueta <img>
            val firstImage = Jsoup.parse(htmlContent).select("img").first()
            // Extrae el atributo 'src' (la URL)
            firstImage?.attr("src")
        } catch (e: Exception) {
            // Si no encuentra ninguna etiqueta <img>, Jsoup lanza una excepción o devuelve null.
            null
        }
    }

    val imageUrl = findImageUrlInContent(noticia.publishingPageContent)

    fun findImageUrlInHtml(html: String?): String? {
        if (html.isNullOrBlank()) return null
        return try {
            Jsoup.parse(html).select("img").first()?.attr("src")
        } catch (e: Exception) { null }
    }

    // 1. Obtenemos el HTML del campo de imagen dedicado
    val imageHtml = noticia.fieldValuesAsHtml?.publishingPageImage

    // 2. Extraemos la URL de ese HTML
    val finalImageUrl = findImageUrlInHtml(imageHtml)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                val encodedUrl = URLEncoder.encode(newsUrl, StandardCharsets.UTF_8.toString())
                navController.navigate("${AppRoutes.NEWS_DETAILS_SCREEN}/${noticia.id}?baseUrl=${encodedUrl}")  }
    ) {
        Column {
            // Usamos el campo corregido y la librería Coil para cargar la imagen
            if (imageUrl != null) {
                AsyncImage(
                    model = "$sharepointBaseUrl$imageUrl", // Construimos la URL completa
                    contentDescription = noticia.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else if(finalImageUrl != null) {
                AsyncImage(
                    model = "$sharepointBaseUrl$finalImageUrl",
                    contentDescription = "new",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Gray300),
                    contentScale = ContentScale.Inside,
                )
            } else {
                AsyncImage(
                    model = escudoUrl, // O usa painterResource(R.drawable.tu_placeholder)
                    contentDescription = "No hay imagen disponible",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Gray300),
                    contentScale = ContentScale.Inside,
                )
            }

            val fechaRecortada = noticia.fechaHoraNoticia?.substring(0, 10)

            Column(modifier = Modifier.padding(12.dp)) {
                Text(noticia.title, style = MaterialTheme.typography.titleMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(Jsoup.parse(noticia!!.publishingPageContent ?: "Sin contenido").text(), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(fechaRecortada?:"", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}