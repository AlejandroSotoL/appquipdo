package com.tramites1cero1.centralizacion.ui.screen.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class TopBarActions(
    val onMenuClicked: () -> Unit,
    val onBackClicked: () -> Unit
)


@Composable
fun MainTopBar(
    actions: TopBarActions,
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.primary)
        .padding(top = 43.dp, start = 10.dp, end = 15.dp)
        .height(60.dp)){
        IconButton(
            onClick = actions.onBackClicked,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(5.dp)
                .background(White, shape = RoundedCornerShape(40.dp))
                .size(35.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Menú",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(23.dp)
            )
        }
        IconButton(
            onClick = actions.onMenuClicked,
            modifier = Modifier
                .size(38.dp)
                .align(Alignment.TopEnd)
                .background((MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)), shape = RoundedCornerShape(8.dp)),
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menú",
                tint = Color.White,
                modifier = Modifier
                    .size(30.dp)
            )
        }
    }
}