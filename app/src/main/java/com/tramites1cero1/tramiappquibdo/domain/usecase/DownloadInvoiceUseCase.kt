package com.tramites1cero1.tramiappquibdo.domain.usecase


import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.tramites1cero1.tramiappquibdo.data.network.TaxApiService
import com.tramites1cero1.tramiappquibdo.domain.model.Tax
import com.tramites1cero1.tramiappquibdo.domain.repository.TaxRepository
import kotlinx.coroutines.Dispatchers
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.withContext



class DownloadInvoiceUseCase(
    private val taxRepository: TaxRepository,
    private val context: Context,
    private val taxApiService: TaxApiService,

) {

    suspend operator fun invoke(tax: Tax): Result<Uri> {

        if (!tax.pdfUrltoApi.isNullOrBlank()) {
            Log.d("DownloadUseCase", "URL directa encontrada. Descargando desde: ${tax.pdfUrltoApi}")
            return downloadAndSaveFile(tax.pdfUrltoApi, "factura_${tax.reference}.pdf")
        } else {
            Log.d("DownloadUseCase", "No hay URL directa. Solicitando URL al servidor...")
            val urlResult = taxRepository.getInvoicePdfUrl(tax)
            return urlResult.fold(
                onSuccess = { pdfUrl ->
                    Log.d("DownloadUseCase", "URL obtenida del servidor. Descargando desde: $pdfUrl")
                    downloadAndSaveFile(pdfUrl, "factura_${tax.reference}.pdf")
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        }
    }

    private suspend fun downloadAndSaveFile(url: String, fileName: String): Result<Uri> = withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val response = taxApiService.downloadFile(url)

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Error en la descarga del servidor: ${response.code()}"))
            }
            val body = response.body() ?: return@withContext Result.failure(Exception("El cuerpo de la respuesta está vacío."))

            val contentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
            }

            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: return@withContext Result.failure(Exception("No se pudo crear el archivo en MediaStore."))

            inputStream = body.byteStream()
            outputStream = contentResolver.openOutputStream(uri)
                ?: return@withContext Result.failure(Exception("No se pudo abrir el OutputStream."))

            inputStream.copyTo(outputStream)

            Log.d("DownloadUseCase", "PDF guardado exitosamente en: $uri")
            Result.success(uri)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        } finally {

            inputStream?.close()
            outputStream?.close()
        }
    }
}