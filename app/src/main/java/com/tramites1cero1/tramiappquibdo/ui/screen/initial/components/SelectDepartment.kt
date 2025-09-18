package com.tramites1cero1.tramiappquibdo.ui.screen.initial.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tramites1cero1.tramiappquibdo.domain.model.Department

@Composable
fun SelectDepartment(
    visible: Boolean,
    query: String,
    onQueryChanged: (String) -> Unit,
    departamentos: List<Department>,
    showDropdown: Boolean,
    onDepartamentoSelected: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black
) {

}