package com.tramites1cero1.centralizacion.ui.screen.main.components

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tramites1cero1.centralizacion.domain.model.MunicipalityProcedure
import com.tramites1cero1.centralizacion.ui.screen.main.RemindersViewModel
import com.tramites1cero1.centralizacion.ui.screen.settingsUser.components.ReusableModal
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.Gray400
import com.tramites1cero1.centralizacion.ui.theme.Gray600
import com.tramites1cero1.centralizacion.ui.theme.Gray900
import com.tramites1cero1.centralizacion.ui.theme.Red
import com.tramites1cero1.centralizacion.ui.theme.primarycolor
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowOnlyRemindersIsEmpty(
    onlyProcedure: List<MunicipalityProcedure>,
    remidersVm: RemindersViewModel = hiltViewModel(),
    showModalByCreateReminderFlow: Boolean,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    var expirationDate by remember { mutableStateOf("") }
    var vigenciaDate by remember { mutableStateOf("") }
    var reminderType by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedProcedureId by remember { mutableStateOf<Int?>(null) }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val today = Calendar.getInstance().time
    val context = LocalContext.current

    if (showModalByCreateReminderFlow) {
        ReusableModal(
            title = "CREAR RECORDATORIO",
            onDismiss = { onDismiss() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onlyProcedure.isEmpty()) {
                    Text(text = "No hay procedimientos para crear un recordatorio.")
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = reminderType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de recordatorio") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = reminderType.isBlank()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        when {
                            onlyProcedure.isEmpty() -> {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "No hay tipos de proceso disponibles",
                                            color = Color.Red
                                        )
                                    },
                                    onClick = {},
                                    enabled = false
                                )
                            }

                            else -> {
                                DropdownMenuItem(
                                    text = { Text("Principales", color = Color.Gray) },
                                    onClick = {},
                                    enabled = false
                                )
                                onlyProcedure.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.procedures?.name ?: "Sin nombre") },
                                        onClick = {
                                            reminderType = option.procedures?.name ?: "Sin nombre"
                                            selectedProcedureId = option.id
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                DatePickerField(
                    label = "Fecha Expiración",
                    dateValue = expirationDate,
                    onDateSelected = { expirationDate = it },
                    context = context
                )

                DatePickerField(
                    label = "Fecha Vigencia",
                    dateValue = vigenciaDate,
                    onDateSelected = { vigenciaDate = it },
                    context = context
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        val expDateValid = runCatching { sdf.parse(expirationDate) }
                            .getOrNull()?.after(today) ?: false
                        val vigDateValid = runCatching { sdf.parse(vigenciaDate) }
                            .getOrNull()?.after(today) ?: false

                        when {
                            reminderType.isBlank() || selectedProcedureId == null -> {
                                errorMessage = "Debe seleccionar un tipo de recordatorio"
                            }

                            !expDateValid -> {
                                errorMessage = "La fecha de expiración no es válida"
                            }

                            !vigDateValid -> {
                                errorMessage = "La fecha de vigencia no es válida"
                            }

                            else -> {
                                errorMessage = null
                                remidersVm.createReminders(
                                    expirationDate = expirationDate,
                                    vigenciaDate = vigenciaDate,
                                    procedureId = selectedProcedureId!!
                                )
                                onDismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primarycolor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    dateValue: String,
    onDateSelected: (String) -> Unit,
    context: Context
) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val calendar = Calendar.getInstance()

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Button(
            onClick = {
                val datePicker = android.app.DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(year, month, dayOfMonth)
                        onDateSelected(sdf.format(calendar.time))
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.datePicker.minDate = System.currentTimeMillis()
                datePicker.show()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text(
                text = if (dateValue.isBlank()) "Seleccionar fecha" else dateValue,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun RemindersSection(
    onlyProcedure: List<MunicipalityProcedure>,
    remindersViewModel: RemindersViewModel = hiltViewModel(),
) {
    val uiState by remindersViewModel.uiState.collectAsState()
    var showAddOnlyReminders by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val isActiveSendEmail by remindersViewModel.isActiveSendEmail.collectAsState()

    val remindersOrdered = remember(uiState.reminders, uiState.expiredReminders) {
        uiState.reminders.sortedByDescending { reminder ->
            uiState.expiredReminders.any { it.id == reminder.id }
        }
    }
    val pagerState = rememberPagerState(pageCount = { remindersOrdered.size })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recordatorios",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showAddOnlyReminders = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear recordatorio",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        when {

            uiState.isLoading -> Text(
                "Cargando recordatorios...",
                Modifier.padding(10.dp),
                textAlign = TextAlign.Center,
            )

            uiState.sessionExpired -> Text(
                "Tu sesión ha expirado",
                color = Color.Red,
                modifier = Modifier.padding(10.dp)
            )

            uiState.error != null -> Text(
                uiState.error ?: "Error desconocido",
                color = Color.Red,
                modifier = Modifier.padding(10.dp)
            )

            uiState.reminders.isEmpty() -> Text(
                "No tienes recordatorios aún",
                Modifier.padding(10.dp)
            )
        }
        if (showAddOnlyReminders) {
            ShowOnlyRemindersIsEmpty(
                onlyProcedure = onlyProcedure,
                showModalByCreateReminderFlow = showAddOnlyReminders,
                onDismiss = { showAddOnlyReminders = false }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Gray400,
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
        ) {
            if (remindersOrdered.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    val recordatorio = remindersOrdered[page]
                    val isExpired = uiState.expiredReminders.any { it.id == recordatorio.id }
                    val isActivated = uiState.activatedReminders.contains(recordatorio.id)

                    Card(
                        onClick = {
                            val notificationManager = NotificationManagerCompat.from(context)
                            if (!notificationManager.areNotificationsEnabled()) {
                                val intent =
                                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    }
                                context.startActivity(intent)
                            } else {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                val vibrator =
                                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(
                                        VibrationEffect.createOneShot(
                                            200,
                                            VibrationEffect.DEFAULT_AMPLITUDE
                                        )
                                    )
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator.vibrate(200)
                                }
                                remindersViewModel.onReminderClicked(recordatorio, page)
                            }
                        },
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(25.dp),
                                    verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        if (isExpired) Color.Gray else MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.15f
                                        ),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isExpired -> Icons.Default.NotificationsOff
                                        isActivated -> Icons.Default.NotificationsActive
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = if (isExpired) Color.DarkGray else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = recordatorio.idProcedureMunicipalityNavigation?.procedures?.name
                                        ?: "N/A",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Alcaldía de ${recordatorio.idProcedureMunicipalityNavigation?.municipality?.name.orEmpty()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Vencimiento: ${recordatorio.vigenciaDate ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Vigencia: ${recordatorio.expirationDate ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall
                                )
//                                val titular = listOfNotNull(
//                                    recordatorio.idUserNavigation?.firstName,
//                                    recordatorio.idUserNavigation?.lastName
//                                ).joinToString(" ").ifBlank { "N/A" }
//                                Text("Titular: $titular", style = MaterialTheme.typography.bodySmall)
//                                Spacer(Modifier.height(4.dp))
//                                Text(
//                                    "Correo: ${if (isActiveSendEmail) "Activado" else "Desactivado"}",
//                                    style = MaterialTheme.typography.labelSmall,
//                                    color = if (isActiveSendEmail) MaterialTheme.colorScheme.primary else Color.Gray
//                                )
                            }
                        }
                    }
                }
            }

            if (pagerState.pageCount > 1) {
                if (pagerState.canScrollBackward) ScrollIndicatorLeft()
                if (pagerState.canScrollForward) ScrollIndicatorRight()
            }
        }
    }
}


@Composable
private fun BoxScope.ScrollIndicatorLeft() {
    var atTop by remember { mutableStateOf(true) }
    val yOffset by animateDpAsState(
        targetValue = if (atTop) 5.dp else 0.dp,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "arrowAnimation"
    )

    LaunchedEffect(Unit) {
        while (true) {
            atTop = !atTop
            delay(600)
        }
    }

    Icon(
        imageVector = Icons.Default.KeyboardArrowLeft,
        contentDescription = "Deslizar hacia izquierda",
        tint = Color.White.copy(alpha = 0.7f),
        modifier = Modifier
            .align(Alignment.CenterStart)
            .offset(x = yOffset + 0.dp)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            .size(24.dp)
    )
}

@Composable
private fun BoxScope.ScrollIndicatorRight() {
    var atTop by remember { mutableStateOf(true) }
    val yOffset by animateDpAsState(
        targetValue = if (atTop) 0.dp else 5.dp,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "arrowAnimation"
    )

    LaunchedEffect(Unit) {
        while (true) {
            atTop = !atTop
            delay(600)
        }
    }

    Icon(
        imageVector = Icons.Default.KeyboardArrowRight,
        contentDescription = "Deslizar hacia derecha",
        tint = Color.White.copy(alpha = 0.7f),
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .offset(x = yOffset - 10.dp)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            .size(24.dp)
    )
}
