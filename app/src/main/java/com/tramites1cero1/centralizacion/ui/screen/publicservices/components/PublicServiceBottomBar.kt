package com.tramites1cero1.centralizacion.ui.screen.publicservices.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tramites1cero1.centralizacion.ui.components.FooterSponsors

@Composable
fun PublicServiceBottomBar(){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)
        .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically) {

        FooterSponsors("",MaterialTheme.colorScheme.onPrimary)

    }
}