package com.tramites1cero1.centralizacion.ui.screen.publicservices.components

import android.Manifest
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.tramites1cero1.centralizacion.MunicipalityViewModel
import com.tramites1cero1.centralizacion.utils.BarcodeAnalyzer
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.screen.publicservices.PSSValidationViewModel
import com.tramites1cero1.centralizacion.ui.screen.publicservices.PSFPaymentViewModel
import com.tramites1cero1.centralizacion.ui.screen.publicservices.PublicServicesForm
import com.tramites1cero1.centralizacion.ui.theme.White
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CameraPreview(
    context: Context,
    navController: NavController,
    PSSValidationViewModel: PSSValidationViewModel,
    PSFPaymentViewModel: PSFPaymentViewModel,
    munViewModel: MunicipalityViewModel,
    ){

    val codigoDetectado = PSSValidationViewModel.codigoDetectado.value
    val codigovalido = PSSValidationViewModel.codigoValidado.value
    val fechavalida = PSSValidationViewModel.fechaValida.value

    var advertenciaFechaVencimiento by remember { mutableStateOf(false) }
    var advertenciaCodigoErrado by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    var isExpanded by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }
    val limitedHeight = (400.dp * scale.coerceIn(1f, 2.5f)).coerceAtMost(800.dp)

    var visible by remember { mutableStateOf(true) }
    val scalea by animateFloatAsState(
        targetValue = if (isExpanded) 2f else 1f,
        animationSpec = tween(durationMillis = 2000)
    )

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFormDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit,codigovalido) {
        if (codigoDetectado != null ) {
                if (codigovalido){
                    if (fechavalida) {
                        showFormDialog = true
                    }else{
                        advertenciaFechaVencimiento = true
                    }
                }else{
                    advertenciaCodigoErrado = true
                }
        }
        // imagen animada
        repeat(3) { // 2 veces (2 segundos en total)
            isExpanded = !isExpanded
            delay(1000)
            // espera 1 segundo entre cada animación
        }
        visible = false
        //permiso para poder implementar la camara
        cameraPermissionState.launchPermissionRequest()
    }

    if(showFormDialog){
        ModalBottomSheet(
            onDismissRequest = { showFormDialog = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background
        ){
            PublicServicesForm(
                navController = navController,
                PSSValidationViewModel = PSSValidationViewModel,
                PSFPaymentViewModel = PSFPaymentViewModel,
                munViewModel = munViewModel
            )
        }
    }

    LaunchedEffect(advertenciaFechaVencimiento, advertenciaCodigoErrado) {
        if (advertenciaFechaVencimiento) {
            delay(4000)
            advertenciaFechaVencimiento = false
        }

        if (advertenciaCodigoErrado) {
            delay(4000)
            advertenciaCodigoErrado = false
        }
    }
    // camaerax y detetctar codigo de barras
    if (cameraPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            //toma el valor de codigo de barras y lo envia a la funcion de actualizar codigo
            val analyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        BarcodeAnalyzer { codigoDetectado ->
                            PSSValidationViewModel.actualizarCodigo(codigoDetectado)
                            PSFPaymentViewModel.setFacturaYValorpagar(PSSValidationViewModel.resultFactura.value, PSSValidationViewModel.resultValorAPagar.value)
                        }
                    )
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analyzer
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(limitedHeight)
                .clip(RoundedCornerShape(2.dp))
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        scale *= zoom
                    }
                }
        ) {
            if (cameraPermissionState.status.isGranted && showFormDialog == false) {
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .matchParentSize()
                        .graphicsLayer(
                            scaleX = 1f,
                            scaleY = 1f
                        )
                )
                if(visible){
                    Box(modifier = Modifier.fillMaxSize().zIndex(1f), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.expandir),
                            contentDescription = "Ícono pulsante",
                            modifier = Modifier
                                .size(100.dp)
                                .graphicsLayer {
                                    scaleX = scalea
                                    scaleY = scalea
                                }
                        )
                        Text(
                            "Expandir",
                            style = MaterialTheme.typography.titleLarge,
                            color = White
                        )
                    }
                }
                if(advertenciaFechaVencimiento  || advertenciaCodigoErrado){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f))
                            .zIndex(1f)// Fondo oscuro semitransparente
                            .clickable { }, // Toca para cerrar
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if(advertenciaFechaVencimiento) "Factura vencida: La fecha de vencimiento ha expirado. No se puede procesar el pago." else "El código de barras escaneado no es válido.",
                            color = Color.White,
                            fontSize = 20.sp,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            } else if (showFormDialog) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator(color = White)
                }
            } else {
                Text(
                    "Se requiere permiso de cámara para escanear",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

