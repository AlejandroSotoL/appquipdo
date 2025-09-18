package com.tramites1cero1.tramiappquibdo.ui.screen.initial.components

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.tramites1cero1.tramiappquibdo.data.model.CarouselImage
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray300
import com.tramites1cero1.tramiappquibdo.ui.theme.buttoncolorslogin
import com.tramites1cero1.tramiappquibdo.ui.theme.primarycolor
import com.tramites1cero1.tramiappquibdo.utils.abrirURL
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdvertisementSection(
    context: Context,
    images: List<CarouselImage>
) {
    if (images.isEmpty()){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
        return
    }
    val pagerState = rememberPagerState (pageCount = { images.size })

    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(5000L)
            if (pagerState.pageCount > 0) {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }


    Column (
        modifier = Modifier.padding(horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    abrirURL(
                        context = context,
                        url = "https://101fintech.com/SolicitudTarjeta.aspx",
                        toolbarColor = primarycolor.toArgb()
                    )
                }
        ) {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                val imageInfo = images[page]
                ImagendeAnuncio(
                    imageUrl = imageInfo.imageUrl,
                    contentDescription = "Anuncio ${page + 1}",
                    modifier = Modifier.clickable {
                        abrirURL(
                            context = context,
                            url = imageInfo.clickUrl,
                            toolbarColor = primarycolor.toArgb()
                        )
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            Modifier.height(20.dp),
            horizontalArrangement = Arrangement.Center
        ){
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) buttoncolorslogin else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Aplican T&C",
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            style = MaterialTheme.typography.bodySmall,
            color = Gray300)
    }

}

@Composable
fun ImagendeAnuncio(
    imageUrl: String, //  Recibe una URL en lugar de un ID de recurso
    modifier: Modifier = Modifier,
    contentDescription: String
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Ratio 1:1 para la imagen
        contentAlignment = Alignment.Center
    ) {
        Image(
            //  Coil carga la imagen desde la URL
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}