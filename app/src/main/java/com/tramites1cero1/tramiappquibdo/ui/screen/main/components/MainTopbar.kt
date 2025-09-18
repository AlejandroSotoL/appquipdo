package com.tramites1cero1.tramiappquibdo.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tramites1cero1.tramiappquibdo.ui.theme.White

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