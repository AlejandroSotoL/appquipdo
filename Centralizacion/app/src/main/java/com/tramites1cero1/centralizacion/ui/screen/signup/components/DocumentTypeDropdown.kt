package com.tramites1cero1.centralizacion.ui.screen.signup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DocumentTypeDropdown(
    query: String,
    onQueryChanged: (String) -> Unit,
    expanded: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    documentTypes: List<String>,
    onDocumentTypeSelected: (String) -> Unit,
    errorMessage: String?
) {
    val filteredTipos = documentTypes.filter { it.contains(query, ignoreCase = true) }
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(800, delayMillis = 300)) + slideInVertically(
            animationSpec = tween(800, delayMillis = 300),
            initialOffsetY = { fullHeight -> fullHeight }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3D3D4E), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(20.dp))
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChanged,
                isError = errorMessage != null,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp
                ),
                placeholder = { Text("Selecciona tipo de documento", color = Color.White, fontSize = 14.sp) },

                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> onFocusChanged(focusState.isFocused) }
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFF3D3D4E),
                    focusedContainerColor = Color(0xFF3D3D4E),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                )
            )
            if(errorMessage != null){
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded && filteredTipos.isNotEmpty(),
                enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3D3D4E))
                        .heightIn(max = 180.dp)
                        .padding(top = 4.dp)
                ) {
                    filteredTipos.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo, color = Color.White) },
                            onClick = { onDocumentTypeSelected(tipo) }
                        )
                    }
                }
            }
        }
    }

}