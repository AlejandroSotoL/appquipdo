package com.tramites1cero1.centralizacion.ui.screen.publicservices

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.tramites1cero1.centralizacion.MunicipalityViewModel
import com.tramites1cero1.centralizacion.ui.screen.publicservices.components.CameraPreview
import com.tramites1cero1.centralizacion.ui.screen.publicservices.components.PublicServiceBottomBar
import com.tramites1cero1.centralizacion.ui.screen.publicservices.components.PublicServiceTopBar

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PublicServicesScreen(
    navController: NavController,
    PSSValidationViewModel: PSSValidationViewModel,
    PSFPaymentViewModel: PSFPaymentViewModel,
    munViewModel: MunicipalityViewModel,
    ){

    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            PSSValidationViewModel.limpiarvariableCodigo()
        }
    }

    // cargar pdf de la factura
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                PSSValidationViewModel.scanBarcodesFromPdf(context, it)
            }
        }
    )

    Scaffold(
        bottomBar = {
            PublicServiceBottomBar()
        },
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        innerPadding ->

        Column(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
            PublicServiceTopBar(
                navController = navController
            )
            Text(
                text = "Escanear factura",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 25.dp),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
        //  principal
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center)
        {
            // lector
            CameraPreview(
                context = context,
                navController = navController,
                PSSValidationViewModel = PSSValidationViewModel,
                PSFPaymentViewModel = PSFPaymentViewModel,
                munViewModel = munViewModel
            )

            //subir archivos pdf
//        UploadPdfButton(modifier = Modifier.align(Alignment.BottomCenter),
//            onClick = {
//            launcher.launch(arrayOf("application/pdf"))
//        })

            //logos
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PublicServicesScreenPreview() {
//    MaterialTheme { // Asegúrate de usar el mismo theme que en tu app real
//        PublicServicesScreen(navController = NavController(LocalContext.current))
//    }
//}