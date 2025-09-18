package com.tramites1cero1.tramiappquibdo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tramites1cero1.tramiappquibdo.ui.theme.Blue
import com.tramites1cero1.tramiappquibdo.utils.abrirURL

@Composable
fun PolicyCheckboxes(
    dataPolicyChecked: Boolean,
    onDataPolicyChange: (Boolean) -> Unit,
    privacyPolicyChecked: Boolean,
    onPrivacyPolicyChange: (Boolean) -> Unit,
    dataPolicyUrl: String,
    privacyPolicyUrl: String
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PolicyCheckboxRow(
            text = "Acepto y autorizo la política de tratamiento de datos personales",
            linkText = "tratamiento de datos personales",
            url = dataPolicyUrl,
            checked = dataPolicyChecked,
            onCheckedChange = onDataPolicyChange
        )
        PolicyCheckboxRow(
            text = "Acepto las condiciones de uso y las políticas de privacidad",
            linkText = "políticas de privacidad",
            url = privacyPolicyUrl,
            checked = privacyPolicyChecked,
            onCheckedChange = onPrivacyPolicyChange
        )
    }
}

@Composable
fun PolicyCheckboxRow(
    text: String,
    linkText: String,
    url: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary

    val annotatedString = buildAnnotatedString {
        append(text.substringBefore(linkText))
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(
            style = SpanStyle(
                color = Blue,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(linkText)
        }
        pop()
        append(text.substringAfter(linkText))
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange,)
        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium.copy(color = LocalContentColor.current),
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        if (annotation.item.isNotBlank()) {
                            abrirURL(
                                context = context,
                                url = annotation.item,
                                toolbarColor = primaryColor.toArgb()
                            )
                        }
                    }
            }
        )
    }
}