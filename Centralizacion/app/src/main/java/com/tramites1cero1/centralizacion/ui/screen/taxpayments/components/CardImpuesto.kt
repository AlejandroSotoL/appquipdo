package com.tramites1cero1.centralizacion.ui.screen.taxpayments.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.utils.formatCurrency

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaxCard(
    tax: Tax,
    onPayClick: (Tax) -> Unit,
    onPdfClick: (Tax) -> Unit,
    onShareClick: (Tax) -> Unit,
    onRegiserPayment: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Columna con la información del impuesto
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Nombre: ${tax.name}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Impuesto: ${tax.taxName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Factura: ${tax.invoice}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Fecha límite: ${tax.dueDate}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Total a pagar: ${formatCurrency(tax.value)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                // Columna con los botones de acción (PDF, Compartir)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = CircleShape,
                        color = Gray300,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(60.dp)
                    ) {
                        IconButton(
                            onClick = { onPdfClick(tax) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pdf),
                                contentDescription = "Descargar PDF",
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = CircleShape,
                        color = Gray300,
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(60.dp)
                    ) {
                        IconButton(
                            onClick = { onShareClick(tax) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icocompartir),
                                contentDescription = "Compartir",
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }
            }

            if (tax.isExpired) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Factura Vencida",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de pago principal
            Button(
                onClick = {
                    onPayClick(tax)
                    onRegiserPayment()
                },
                enabled = !tax.isExpired,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004984)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Pagar por PSE",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
        }
    }
}
