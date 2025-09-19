package com.tramites1cero1.centralizacion.ui.screen.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.domain.model.Design
import com.tramites1cero1.centralizacion.ui.theme.RobotoBold
import com.tramites1cero1.centralizacion.ui.theme.Roboto_medium
import com.tramites1cero1.centralizacion.ui.theme.Roboto_regular
import com.tramites1cero1.centralizacion.ui.theme.White

@Composable
fun MainHeader( design : Design, departamento : String? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AsyncImage(
            model = design.escudoUrl,
            contentDescription = "Escudo",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(8.dp)
        )
        Text(
            text = design.NombreAlcaldia,
            color = Color.White,
            fontSize = 28.sp,
            fontFamily = Roboto_medium,
            modifier = Modifier
                .padding(top = 10.dp)
        )
        Text(
            text = "Departamento de ${departamento}",
            color = Color.White,
            fontSize = 14.sp,
            fontFamily = Roboto_regular,
            modifier = Modifier
                .padding(bottom = 10.dp)
        )
    }
}