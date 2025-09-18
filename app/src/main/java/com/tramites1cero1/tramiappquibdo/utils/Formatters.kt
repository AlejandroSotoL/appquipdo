package com.tramites1cero1.tramiappquibdo.utils

import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(value: Number): String {
    val colombianLocale = Locale("es", "CO")
    val format = NumberFormat.getCurrencyInstance(colombianLocale)
    format.maximumFractionDigits = 0
    return format.format(value)
}