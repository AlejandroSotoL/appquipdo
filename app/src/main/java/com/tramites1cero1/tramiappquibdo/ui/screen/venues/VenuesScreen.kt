package com.tramites1cero1.tramiappquibdo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.tramites1cero1.tramiappquibdo.ui.components.VenueItem
import com.tramites1cero1.tramiappquibdo.ui.screen.venues.VenuesViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.venues.components.ReservationForm
import com.tramites1cero1.tramiappquibdo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenuesScreen(
    viewModel: VenuesViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    LaunchedEffect(uiState.reservationSuccess) {
        if (uiState.reservationSuccess) {
            Toast.makeText(context, "¡Reserva exitosa!", Toast.LENGTH_LONG).show()
            viewModel.onDismissBottomSheet()
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.circles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
                .rotate(90f)
                .offset(x = (-25).dp, y = (-30).dp)
                .zIndex(1f),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary.copy(0.7f))
        )
        Scaffold(
        ) { padding ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {

                Row (
                    modifier = Modifier.fillMaxWidth().background(color = Color.Transparent),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(38.dp).padding(4.dp))
                    }
                    Text("Espacios deportivos", modifier = Modifier.fillMaxWidth().padding( horizontal = 16.dp),
                        textAlign = TextAlign.Start, style = MaterialTheme.typography.titleLarge)
                }


                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading && uiState.venues.isEmpty()) {
                        CircularProgressIndicator()
                    } else if (uiState.error != null) {
                        Text("Error: ${uiState.error}")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.venues,
                                key = { venue -> venue.id }
                            ) { venue ->
                                VenueItem(
                                    venue = venue,
                                    onButtonClick = { viewModel.onReserveClick(venue) }
                                )
                            }
                        }
                    }

                    if (uiState.selectedVenue != null) {
                        ModalBottomSheet(
                            onDismissRequest = viewModel::onDismissBottomSheet,
                            sheetState = sheetState
                        ) {

                            ReservationForm(
                                venueTitle = uiState.selectedVenue.title,
                                isLoading = uiState.isLoading,
                                venuesViewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}