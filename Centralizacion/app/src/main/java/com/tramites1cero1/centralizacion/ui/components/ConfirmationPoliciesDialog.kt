package com.tramites1cero1.centralizacion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tramites1cero1.centralizacion.ui.theme.Gray600

@Composable
fun ConfirmationPoliciesDialog(
    aceptaTratamientoDatos: Boolean,
    onAceptaTratamientoDatosChange: (Boolean) -> Unit,
    aceptaCondicionesUso: Boolean,
    onAceptaCondicionesUsoChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){

    val isConfirmEnabled = aceptaTratamientoDatos && aceptaCondicionesUso

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Políticas y Condiciones",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            "El usuario acepta expresamente que la notificación de la decisión se hará " +
                    "vía electrónica de conformidad a la ley 1437 de 2011, la cual se realizará al correo " +
                    "electrónico suministrado por el solicitante.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )

        PolicyCheckboxRow(
            text = "Acepto y autorizo la política de tratamiento de datos personales",
            linkText = "tratamiento de datos personales",
            url = "", // Deberías poner una URL real aquí
            checked = aceptaTratamientoDatos,
            onCheckedChange = onAceptaTratamientoDatosChange
        )

        Text(
            "Ley de Protección de Datos Personales: La autorización suministrada en el presente formulario faculta al Municipio para que dé a sus datos aquí recopilados el tratamiento señalado " +
                    "en la “Política de Privacidad para el Tratamiento de Datos Personales”...",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )

        PolicyCheckboxRow(
            text = "Acepto las condiciones de uso y las políticas de privacidad",
            linkText = "políticas de privacidad",
            url = "", // Deberías poner una URL real aquí
            checked = aceptaCondicionesUso,
            onCheckedChange = onAceptaCondicionesUsoChange
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                modifier = Modifier.width(150.dp).padding(end = 10.dp),
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Gray600)
            ) {
                Text("Cancelar", style = MaterialTheme.typography.titleSmall)
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onConfirm,
                enabled = isConfirmEnabled // El botón solo se activa si ambos checkboxes están marcados
            ) {
                Text("Finalizar y Radicar", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}