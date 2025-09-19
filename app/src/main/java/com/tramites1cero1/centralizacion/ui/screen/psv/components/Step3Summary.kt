package com.tramites1cero1.centralizacion.ui.screen.psv.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tramites1cero1.centralizacion.ui.screen.psv.FormButtons
import com.tramites1cero1.centralizacion.ui.screen.psv.PsvUiState
import com.tramites1cero1.centralizacion.utils.formatCurrency

@Composable
fun Step3Summary(
    state: PsvUiState,
    onPay: () -> Unit,
    isProcessing: Boolean,
    onCancel: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Resumen de pago",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // Tarjeta gris con resumen
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryRow(label = "Trámite:", value = state.taxName)
                SummaryRow(label = "Usuario:", value = "${state.firstName} ${state.firstLastName}".trim())
                SummaryRow(label = "Fecha y hora:", value = state.paymentDateTime)
                SummaryRow(label = "No. de factura:", value = state.invoiceNumber)
                SummaryRow(
                    label = "Valor a pagar:",
                    value = formatCurrency(state.valueToPay.toDoubleOrNull() ?: 0.0),
                    isBold = true
                )
            }
        }


        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEF5350)),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Importante",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = "Al seleccionar \"Pagar\", será llevado a la pasarela de pagos donde podrá finalizar la transacción de forma segura. Asegúrese de revisar que los datos y el valor a pagar sean correctos antes de continuar.",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (isProcessing) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            FormButtons(onNext = onPay, backText = "Atrás", onCancel = onCancel, nextText = "Pagar")
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            ),
            color = Color.Black
        )
    }
}
