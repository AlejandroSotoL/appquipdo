package com.tramites1cero1.tramiappquibdo.ui.screen.publicservices

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.tramites1cero1.tramiappquibdo.MunicipalityUiState
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.initial.SelectMunViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.login.AuthViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel
import com.tramites1cero1.tramiappquibdo.ui.theme.Roboto_semiBold
import com.tramites1cero1.tramiappquibdo.utils.abrirURL

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun PublicServicesForm (
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    PSSValidationViewModel: PSSValidationViewModel,
    PSFPaymentViewModel: PSFPaymentViewModel,
    munViewModel: MunicipalityViewModel,
){
    val context = LocalContext.current
    val state = PSFPaymentViewModel.uiState.value
    // cargar datos del municipio
    val munState by munViewModel.uiState.collectAsStateWithLifecycle()
    //  Extraemos el id del municipio
    val municipalityId: Int? = (munState as? MunicipalityUiState.Success)?.data?.idMunicipio
    // Datos personales
    val user by authViewModel.user.collectAsState(initial = null)
    var isEditing by remember { mutableStateOf(true) }

    // terminos y condiciones
    var aceptaPoliticaDatos by remember { mutableStateOf(false) }
    var aceptaCondicionesUso by remember { mutableStateOf(false) }

    //respuesta transaccion
    val respuestaTransaccion by PSFPaymentViewModel.transactionResponse.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            PSFPaymentViewModel.setUserData(user!!)
        }else {
            isEditing = false
        }
    }

    BackHandler {
        PSSValidationViewModel.limpiarvariableCodigo()
        navController.popBackStack()
    }

    DisposableEffect(Unit) {
        onDispose {
            PSFPaymentViewModel.clearReponseTransaction()
            PSSValidationViewModel.limpiarvariableCodigo()
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(start = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(38.dp)
                        .padding(4.dp))
            }
        }

        Text(
            "Detalles de facturación",
            fontFamily = Roboto_semiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Editar",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(end = 3.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.BorderColor,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(10.dp)
                            .clickable { isEditing = !isEditing }
                    )
                }
                Spacer(Modifier.height(5.dp))
                // --- Datos del pagador ---
                OutlinedTextField(
                    value = state.valorPagar,
                    onValueChange = { state.valorPagar },
                    label = { Text("Valor a Pagar") },
                    readOnly = true,
                    singleLine = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = state.factura,
                    onValueChange = { state.valorPagar },
                    label = { Text("Factura") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    readOnly = true,
                    enabled = false,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = state.documento,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(documento = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Documento*") },
                    enabled = isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = state.primerNombre,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(primerNombre = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Primer Nombre*") },
                    enabled = isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )

                OutlinedTextField(
                    value = state.segundoNombre,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(segundoNombre = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Segundo Nombre") },
                    enabled = isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    value = state.primerApellido,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(primerApellido = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Primer Apellido*") },
                    enabled = isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    value = state.segundoApellido,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(segundoApellido = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Segundo Apellido") },
                    enabled =isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    value = state.direccion,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(direccion = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Dirreción") },
                    enabled = isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                )
                OutlinedTextField(
                    value = state.telefono,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(telefono = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Teléfono") },
                    enabled = isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { nuevoTexto ->
                        PSFPaymentViewModel.updateField { it.copy(email = nuevoTexto) }
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = { Text("Email*") },
                    enabled = isEditing,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
                // terminos y condiciones
                Spacer(Modifier.height(8.dp))
                val Color = MaterialTheme.colorScheme.primary.toArgb()
                val yaSeAbrio = remember { mutableStateOf(false) }

                LaunchedEffect(respuestaTransaccion?.result?.url) {
                    val url = respuestaTransaccion?.result?.url

                    if (!url.isNullOrEmpty() && !yaSeAbrio.value) {
                        yaSeAbrio.value = true
                        abrirURL(context, url, Color)
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = aceptaPoliticaDatos,
                    onCheckedChange = { aceptaPoliticaDatos = it },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.onBackground,
                        uncheckedColor = MaterialTheme.colorScheme.onBackground,
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    "Acepto la política de tratamiento de datos personales.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = aceptaCondicionesUso,
                    onCheckedChange = { aceptaCondicionesUso = it },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = MaterialTheme.colorScheme.onBackground,
                        uncheckedColor = MaterialTheme.colorScheme.onBackground,
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                Text(
                    "Acepto las Condiciones de Uso y las políticas de privacidad.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Button(
            onClick = {
                PSFPaymentViewModel.enviarDatos(municipalityId)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(22.dp)
        ) {
            Text("Ir a pagar", style = MaterialTheme.typography.titleMedium)
        }
    }
}