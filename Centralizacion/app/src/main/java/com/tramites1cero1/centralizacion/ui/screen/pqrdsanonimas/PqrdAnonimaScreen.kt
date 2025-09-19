package com.tramites1cero1.centralizacion.ui.screen.pqrdsanonimas

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.components.TopbarNavigation
import com.tramites1cero1.centralizacion.ui.screen.pqrds.PqrdsResponseState
import com.tramites1cero1.centralizacion.ui.screen.pqrds.PqrdsViewModel
import com.tramites1cero1.centralizacion.ui.screen.pqrds.components.CustomDropdownPqrds
import com.tramites1cero1.centralizacion.ui.screen.pqrds.components.ErrorDialog
import com.tramites1cero1.centralizacion.ui.screen.pqrds.components.SuccessDialog
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.Gray400
import com.tramites1cero1.centralizacion.ui.theme.Gray600
import com.tramites1cero1.centralizacion.ui.theme.Green

@Composable
fun PqrdsAnonimaScreen(
    navController: NavController,
    viewModel: PqrdsViewModel,
    codigoEntidad: String
){

    val pqrdState by viewModel.pqrdState.collectAsState()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val errorState by viewModel.errorState.collectAsState()
    val dropDownOptionsState by viewModel.dropDownOptionsState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current


    LaunchedEffect(errorState.tipoArchivoError) {
        errorState.tipoArchivoError?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearFileTypeError() // Necesitarías crear esta función en el ViewModel
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadDropdownData(codigoEntidad)
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        viewModel.onFileSelected(uri)
    }


    BackHandler(enabled = true) {
        navController.popBackStack() // Dispara el evento para volver
    }


    TopbarNavigation(
        onBackPressed = { navController.popBackStack() },
        icon = R.drawable.ico_pqrdanonima,
        title = "PQRSD ANÓNIMA",
        description = "Peticiones, quejas, reclamos y sugerencias",
        scrollContent = {
            innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(innerPadding)){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(top = 20.dp)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { focusManager.clearFocus() },
                ) {
                    Text("Información General",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondary)

                    Spacer(modifier = Modifier.height(16.dp))

                    if(dropDownOptionsState.isLoading){
                        CircularProgressIndicator()
                    } else {
                        CustomDropdownPqrds(
                            placeHolder = "Seleccione una secretaría",
                            label = "*Secretaría",
                            options = dropDownOptionsState.secretarias.map { it.Secretaria }, // Suponiendo que el modelo tiene 'nombre'
                            selectedValue = formState.secretaria?.Secretaria ?: "",
                            onValueSelected = { index ->
                                viewModel.onSecretariaChange(dropDownOptionsState.secretarias[index])
                            }
                        )
                        if (errorState.secretariaError) {
                            Text(
                                text = "Debe seleccionar una secretaría",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        CustomDropdownPqrds(
                            placeHolder = "Seleccione un asunto de interés",
                            label = "*Asunto de interés",
                            options = dropDownOptionsState.asuntosInteres.map { it.Descripcion },
                            selectedValue = formState.asuntoInteres?.Descripcion ?: "",
                            onValueSelected = { index ->
                                viewModel.onAsuntoInteresChange(dropDownOptionsState.asuntosInteres[index])
                            }
                        )
                        if (errorState.asuntoInteresError) {
                            Text(
                                text = "Debe seleccionar un asunto de interés",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        CustomDropdownPqrds(
                            placeHolder = "Seleccione una clasificación",
                            label = "*Clasificación solicitud",
                            options = dropDownOptionsState.clasificacionesSolicitud.map { it.Descripcion },
                            selectedValue = formState.clasificacionSolicitud?.Descripcion ?: "",
                            onValueSelected = { index ->
                                viewModel.onClasificacionSolicitudChange(dropDownOptionsState.clasificacionesSolicitud[index])
                            }
                        )
                        if (errorState.clasificacionSolicitudError) {
                            Text(
                                text = "Debe seleccionar una clasificación",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = formState.descripcion,
                        onValueChange = { viewModel.onDescripcionChange(it) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        label = { Text("Descripción *", style = MaterialTheme.typography.bodyMedium) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = TextFieldDefaults.colors(
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = Gray400,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                        ),
                        shape = RoundedCornerShape(10.dp),
                        isError = errorState.descripcionError,
                        supportingText = {
                            if (errorState.descripcionError) {
                                Text("La descripción no puede estar vacía.")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if(formState.nombreArchivo == null || formState.nombreArchivo == ""){
                        Column() {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Button(onClick = { filePickerLauncher.launch("*/*") }) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null)
                                }
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Adjuntar documentos", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .clickable { filePickerLauncher.launch("*/*") }
                            .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween){
                            Text("Seleccionar documentos", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Gray300, RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Archivo adjuntado",
                                tint = Green
                            )
                            Text(
                                text = formState.nombreArchivo ?: "",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            // Opcional: Botón para quitar el archivo
                            IconButton(onClick = { viewModel.onFileSelected(null) }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Quitar archivo")
                            }
                        }
                    }

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)) {
                        Button(
                            // El botón se deshabilita mientras está cargando
                            enabled = pqrdState != PqrdsResponseState.LoadingPqrd,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gray600,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .width(150.dp)
                                .align(Alignment.BottomStart),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 18.dp)
                        ) {
                            Text("Cancelar", style = MaterialTheme.typography.headlineMedium)
                        }
                        Button(
                            // El botón se deshabilita mientras está cargando
                            enabled = pqrdState != PqrdsResponseState.LoadingPqrd,
                            onClick = { viewModel.submitPqrdAnonima(codigoEntidad) },
                            modifier = Modifier
                                .width(150.dp)
                                .align(Alignment.BottomEnd),
                            contentPadding = PaddingValues(vertical = 12.dp, horizontal = 18.dp)
                        ) {
                            Text("Finalizar", style = MaterialTheme.typography.headlineMedium)
                        }
                    }

                }
                when (val state = pqrdState) {
                    is PqrdsResponseState.LoadingPqrd -> {
                        // Muestra un indicador de carga en el centro de la pantalla
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(enabled = false, onClick = {}) // Bloquea clics detrás
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is PqrdsResponseState.Success -> {
                        // Muestra un diálogo de éxito
                        SuccessDialog(
                            ticket = state.token,
                            onDismiss = {
                                viewModel.resetSubmissionState()
                                // Navega a la pantalla de inicio o a donde necesites
                                navController.popBackStack("pqrd_choice", inclusive = false)
                            }
                        )
                    }
                    is PqrdsResponseState.ErrorPqrd -> {
                        // Muestra un diálogo de error
                        ErrorDialog(
                            message = state.message,
                            onDismiss = {
                                viewModel.resetSubmissionState()
                            }
                        )
                    }
                    is PqrdsResponseState.Empty -> {
                        // No hace nada, la UI está en su estado normal
                    }
                }
            }
        })

}




