package com.tramites1cero1.tramiappquibdo.domain.model


import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Parcelize
data class Tax(
    val entity: String,
    val entityCode: String,
    val document: String,
    val name: String,
    val taxName: String,
    val taxId: Int,
    val value: Int,
    val invoice: String,
    val reference: String,
    val dueDate: String,
    val pdfUrltoApi: String?
) : Parcelable {


    val isExpired: Boolean
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                val dueDateParsed = LocalDate.parse(this.dueDate, formatter)

                dueDateParsed.isBefore(LocalDate.now())
            } catch (e: DateTimeParseException) {
                false
            }
        }
}