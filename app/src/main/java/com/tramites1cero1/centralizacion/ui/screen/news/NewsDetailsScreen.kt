package com.tramites1cero1.centralizacion.ui.screen.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import org.jsoup.Jsoup
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NewsDetailsScreen(
    navController: NavController,
    detalleViewModel: NewsDetailsViewModel = hiltViewModel(),
) {

    val noticia by detalleViewModel.noticiaState.collectAsState()
    val baseUrl = detalleViewModel.baseUrl

    if (noticia != null) {

        fun findImageUrlInContent(htmlContent: String?): String? {
            if (htmlContent.isNullOrBlank()) return null
            return try {
                Jsoup.parse(htmlContent).select("img").first()?.attr("src")
            } catch (e: Exception) {
                null
            }
        }

        val imageUrl = findImageUrlInContent(noticia!!.publishingPageContent)

        Scaffold(
            topBar = {
                Box{
                    Image(
                        painter = painterResource(id = R.drawable.circlestop),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(100.dp)
                            .zIndex(2f),
                        colorFilter = ColorFilter.tint(Gray300.copy(alpha = 0.9f))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.background).padding(start = 10.dp, top = 35.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = navController::popBackStack,
                            modifier = Modifier.background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape))
                        {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp).padding(2.dp))
                        }
                        Text(text= "Detalles de la noticia", modifier = Modifier.fillMaxWidth().padding( horizontal = 16.dp),
                            textAlign = TextAlign.Start, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        ) {
            innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                if (imageUrl != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        AsyncImage(
                            model = "$baseUrl/$imageUrl",
                            contentDescription = noticia!!.title,
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                } else {
                    // Placeholder si no hay imagen
                    Image(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = "No hay imagen",
                        modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.LightGray),
                        contentScale = ContentScale.Inside
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(noticia!!.title, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Publicado: ${noticia!!.fechaHoraNoticia}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        Jsoup.parse(noticia!!.publishingPageContent ?: "Sin contenido").text(),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.weight(1f).height(10.dp))

                    // Empuja el botón hacia abajo
                    OutlinedButton(
                        onClick = navController::popBackStack,
                        modifier = Modifier.fillMaxWidth(),

                        ) {
                        Text("VOLVER")
                    }
                }

            }
        }
    } else {
        // Opcional: Muestra un indicador de carga mientras se busca la noticia
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}