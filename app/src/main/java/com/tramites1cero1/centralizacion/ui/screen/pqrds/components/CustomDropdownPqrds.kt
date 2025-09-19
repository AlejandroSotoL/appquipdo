package com.tramites1cero1.centralizacion.ui.screen.pqrds.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.Gray400
import com.tramites1cero1.centralizacion.ui.theme.Gray600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownPqrds(
    placeHolder: String,
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueSelected: (Int) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = { Text(placeHolder, style = MaterialTheme.typography.bodyMedium) },
            label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = {
                if(!expanded){
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Arrow Drop Down")
                }else {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Arrow Drop Up")
                }
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Gray400,
                disabledBorderColor = Gray400,
                disabledTextColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(10.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text, style = MaterialTheme.typography.bodyMedium) },
                    enabled = enabled,
                    onClick = {
                        onValueSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}