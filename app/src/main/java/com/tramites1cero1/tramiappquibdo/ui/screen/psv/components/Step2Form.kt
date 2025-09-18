package com.tramites1cero1.tramiappquibdo.ui.screen.psv.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tramites1cero1.tramiappquibdo.ui.components.PolicyCheckboxes
import com.tramites1cero1.tramiappquibdo.ui.screen.psv.FormButtons
import com.tramites1cero1.tramiappquibdo.ui.screen.psv.PsvPaymentViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.psv.PsvUiState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Form(
    state: PsvUiState, viewModel: PsvPaymentViewModel, onNext: () -> Unit, onCancel: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            text = "Datos de pago",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = state.taxName,
            onValueChange = {},
            readOnly = true,
            label = { Text("*Tipo de Impuesto", style = MaterialTheme.typography.bodySmall) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodySmall,
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            enabled = false
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("*Correo electrónico", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.phone,
            onValueChange = viewModel::onPhoneChange,
            label = { Text("*Teléfono", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.invoiceNumber,
            onValueChange = viewModel::onInvoiceNumberChange,
            label = { Text("*Número de Factura", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.valueToPay,
            onValueChange = viewModel::onValueToPayChange,
            label = { Text("*Valor a pagar", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(10.dp),
            colors = transparentTextFieldColors(),
            singleLine = true,
            prefix = { Text("$ ", style = MaterialTheme.typography.bodyMedium) },
            visualTransformation = CurrencyVisualTransformation()
            )

        PolicyCheckboxes(
            dataPolicyChecked = state.acceptsDataPolicy,
            onDataPolicyChange = viewModel::onAcceptsDataPolicyChange,
            privacyPolicyChecked = state.acceptsTerms,
            onPrivacyPolicyChange = viewModel::onAcceptsTermsChange,
            dataPolicyUrl = state.dataPolicyUrl,
            privacyPolicyUrl = state.privacyPolicyUrl
        )

        Spacer(modifier = Modifier.height(8.dp))
        FormButtons(onNext = onNext, backText = "Atras", onCancel = onCancel)
    }
}

private class CurrencyVisualTransformation : VisualTransformation {

    private val symbols = DecimalFormatSymbols(Locale.GERMANY)
    private val formatter = DecimalFormat("#,###", symbols)

    override fun filter(text: AnnotatedString): TransformedText {

        if (text.text.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val originalText = text.text
        val formattedText = try {
            formatter.format(originalText.toLong())
        } catch (e: NumberFormatException) {
            return TransformedText(
                text,
                OffsetMapping.Identity
            )
        }


        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {

                val separators = (originalText.length - 1) / 3
                var transformedOffset = offset + (offset - 1) / 3
                if (transformedOffset > formattedText.length) {
                    transformedOffset = formattedText.length
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = offset - (offset + 2) / 4
                if (originalOffset < 0) originalOffset = 0
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}

@Composable
private fun transparentTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedBorderColor = MaterialTheme.colorScheme.primary
)

