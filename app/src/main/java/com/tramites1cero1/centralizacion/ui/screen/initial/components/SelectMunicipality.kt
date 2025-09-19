// SelectMunicipality.kt
package com.tramites1cero1.centralizacion.ui.screen.initial.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tramites1cero1.centralizacion.domain.model.Municipality
import com.tramites1cero1.centralizacion.ui.screen.initial.SelectMunUiState
import com.tramites1cero1.centralizacion.ui.theme.ColorTextPrimary
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty

@Composable
fun SelectMunicipio(
    state: SelectMunUiState,
    municipiosFiltrados: List<Municipality>,
    onQueryChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onMunicipioSelected: (Municipality) -> Unit,
    visible: Boolean
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { -30 }),
            exit = fadeOut() + slideOutVertically()
        ) {
            TextField(
                value = state.query,
                onValueChange = onQueryChanged,
                textStyle = TextStyle(ColorTextPrimary, fontSize = 16.sp),
                placeholder = {
                    Text("Buscar municipio...", style = MaterialTheme.typography.titleSmall, color = ColorTextPrimary)
                },
                leadingIcon = {
                    Icon(Icons.Default.Search,  contentDescription = "Buscar")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    }
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        AnimatedVisibility(
            visible = state.showDropdown && municipiosFiltrados.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val sortedMunicipality = municipiosFiltrados.sortedBy { it.name }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .heightIn(max = 180.dp)
                    .padding(top = 4.dp)
            ) {
                items(
                    municipiosFiltrados.size
                ) { municipio ->
                    val muncp = sortedMunicipality[municipio]
                    DropdownMenuItem(
                        text = { Text(muncp.name, style = MaterialTheme.typography.titleSmall, color = ColorTextPrimary) },
                        onClick = {

                            keyboardController?.hide()
                            focusManager.clearFocus()

                            onMunicipioSelected(muncp)
                        }
                    )
                }
            }
        }
    }
}
