package com.tramites1cero1.centralizacion.ui.screen.publicservices

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PSSValidationViewModel @Inject constructor(): ViewModel() {


    private val _codigoDetectado = mutableStateOf<String?>(null)
    val codigoDetectado: State<String?> = _codigoDetectado

    private val _codigoValidado = mutableStateOf(false)
    val codigoValidado: State<Boolean> = _codigoValidado

    private val _resultFactura = mutableStateOf<String?>(null)
    val resultFactura: State<String?> = _resultFactura

    private val _resultValorAPagar = mutableStateOf<String?>(null)
    val resultValorAPagar: State<String?> = _resultValorAPagar

    private val _codigoEANServicio = mutableStateOf<String?>(null)

    private val _fechaVencimiento = mutableStateOf<String?>(null)

    private val _fechaValida = mutableStateOf(false)
    val fechaValida: State<Boolean> = _fechaValida

    var resultText by mutableStateOf<String?>(null)
        private set

    // Actualizar el código detectado
    fun actualizarCodigo(codigo: String) {
        val codigoLimpio = codigo.digitsOnly()

        _codigoDetectado.value = null
        _codigoDetectado.value = codigoLimpio

        Log.d("codigo detectado", codigoLimpio)

        // Actualizar validaciones
        _codigoValidado.value = validarCodigo(codigoLimpio)
        _resultFactura.value = extraerFactura(codigoLimpio)
        _resultValorAPagar.value = extraerValorAPagar(codigoLimpio)
        _codigoEANServicio.value = extraerEANServicio(codigoLimpio)
        _fechaVencimiento.value = extraerFechaVencimiento(codigoLimpio)
        _fechaValida.value = validarFechaVencimiento(_fechaVencimiento.value)

    }


    //funcion quitar carcateres no numericos
    private fun String.digitsOnly(): String {
        return this.filter { it.isDigit() }
    }

    private fun validarCodigo(codigo: String): Boolean {
        val valoresValidos = listOf("415", "8020", "3900", "96")
        return valoresValidos.all { codigo.contains(it) }
    }

    private fun extraerFactura(codigo: String): String? {
        return codigo.substringAfter("8020").substringBefore("3900").dropLast(1)
    }

    private fun extraerValorAPagar(codigo: String): String? {
        return codigo.substringAfter("3900")
            .substringBefore("96")
            .substringBefore("96")
            .substringBefore("96")
            .trimStart('0')
    }

    private fun extraerEANServicio(codigo: String): String? {
        return codigo.substringAfter("415").substringBefore("8020")
    }

    private fun extraerFechaVencimiento(codigo: String): String? {
        return if (codigo.length >= 8) codigo.takeLast(8) else null
    }

    private fun validarFechaVencimiento(fechaStr: String?): Boolean {
        return try {
            if (fechaStr != null && fechaStr.length == 8) {
                val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                val fechaActualStr = formatter.format(Date())

                val fechaVencimientoDate = formatter.parse(fechaStr)
                val fechaActualDate = formatter.parse(fechaActualStr)

                fechaVencimientoDate != null && fechaActualDate != null &&
                        (fechaVencimientoDate.after(fechaActualDate) || fechaVencimientoDate == fechaActualDate)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }




    fun scanBarcodesFromPdf(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver = context.contentResolver
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: return@launch
            val pdfRenderer = PdfRenderer(fileDescriptor)

            val barcodeScanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
            )

            var found = false

            for (i in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(i)
                val fullBitmap = Bitmap.createBitmap(
                    page.width * 2,
                    page.height * 2,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(fullBitmap)
                canvas.drawColor(Color.WHITE)
                page.render(fullBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                found = scanBitmapByRows(fullBitmap, 3, barcodeScanner)
                if (found) break

                found = scanBitmapByRows(fullBitmap, 4, barcodeScanner)
                if (found){
                    break
                }
            }

            if (!found) {
                resultText = " No se encontró ningún código."
            }
            pdfRenderer.close()
            fileDescriptor.close()
        }
    }

    // Función reutilizable para dividir en N filas y escanear cada una con zoom
    suspend fun scanBitmapByRows(bitmap: Bitmap, rows: Int, scanner: BarcodeScanner): Boolean {
        val rowHeight = bitmap.height / rows

        for (row in 0 until rows) {
            val y = row * rowHeight
            val height = if (row == rows - 1) bitmap.height - y else rowHeight

            val subBitmap = Bitmap.createBitmap(bitmap, 0, y, bitmap.width, height)


            val zoomFactor = 1.5f
            val zoomedBitmap = Bitmap.createBitmap(
                (subBitmap.width * zoomFactor).toInt(),
                (subBitmap.height * zoomFactor).toInt(),
                Bitmap.Config.ARGB_8888
            )
            val zoomCanvas = Canvas(zoomedBitmap)
            zoomCanvas.drawColor(Color.WHITE)
            val matrix = Matrix().apply { setScale(zoomFactor, zoomFactor) }
            zoomCanvas.drawBitmap(subBitmap, matrix, Paint())

            val inputImage = InputImage.fromBitmap(zoomedBitmap, 0)
            val barcodes = try {
                scanner.process(inputImage).await()
            } catch (e: Exception) {
                continue
            }

            for (barcode in barcodes) {
                val rawValue = barcode.rawValue
                if (!rawValue.isNullOrBlank()) {
                    _codigoDetectado.value = rawValue
                    return true
                }
            }
        }

        return false
    }

    // fin detectar codigo de barras

    fun limpiarvariableCodigo() {
        _codigoDetectado.value = null
        _codigoValidado.value = false
        _resultFactura.value = null
        _resultValorAPagar.value = null
        _codigoEANServicio.value = null
        _fechaVencimiento.value = null
        _fechaValida.value = false

    }










}