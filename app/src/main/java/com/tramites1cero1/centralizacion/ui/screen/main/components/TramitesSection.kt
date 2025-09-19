package com.tramites1cero1.centralizacion.ui.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tramites1cero1.centralizacion.domain.model.InfoTramite
import com.tramites1cero1.centralizacion.domain.model.TramiteAccion
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.theme.RobotoBold
import com.tramites1cero1.centralizacion.ui.theme.Roboto_regular

data class TramitesState(
    val titulo: String,
    val tramites: List<InfoTramite>,
    val searchText: String,
    val isSearchActive: Boolean,
    val isSearchable: Boolean = true
)

data class TramitesActions(
    val onSearchTextChanged: (String) -> Unit,
    val onSearchToggled: () -> Unit,
    val onTramiteClick: (InfoTramite) -> Unit
)

@Composable
fun TramitesSection(
    state: TramitesState,
    actions: TramitesActions
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val tramitesFiltrados = state.tramites.filter {
        it.nombre.contains(state.searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background,
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.surfaceContainer,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 15.dp, vertical = 5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!state.isSearchActive) {
                Text(text = state.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp))
            } else {
                BasicTextField(
                    value = state.searchText,
                    onValueChange = actions.onSearchTextChanged,
                    textStyle = TextStyle(color = Color.White, fontFamily = RobotoBold, fontSize = 18.sp),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (state.searchText.isEmpty()) {
                                Text(
                                    "Buscar trámites...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                )
                            }
                            innerTextField()
                        }
                    })
            }
            if (state.isSearchable) {
                IconButton(
                    onClick = actions.onSearchToggled,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (state.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Buscador",
                        tint = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(8.dp)
            )
        ) {

            tramitesFiltrados.chunked(3).forEach { filaDeTramites ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    filaDeTramites.forEach { tramite ->
                        Box(modifier = Modifier.weight(1f)) {
                            TramiteCard(
                                cardinfo = tramite,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { actions.onTramiteClick(tramite) }
                            )
                        }
                    }

                    val espaciosVacios = 3 - filaDeTramites.size
                    repeat(espaciosVacios) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}