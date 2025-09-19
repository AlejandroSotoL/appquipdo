package com.tramites1cero1.centralizacion.ui.screen.courses

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.screen.courses.components.CourseItem
import com.tramites1cero1.centralizacion.ui.screen.courses.components.RegistrationForm
import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.bottomSheetsColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val context = LocalContext.current
    var showRegistrationSheet by rememberSaveable { mutableStateOf(state.dismiss) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Efecto para mostrar Toast al registrarse
    LaunchedEffect(state.registrationSuccess) {
        if (state.registrationSuccess) {
            Toast.makeText(context, "Inscripción exitosa", Toast.LENGTH_LONG).show()
            viewModel.onDialogDismiss() // Cierra el diálogo y resetea el estado
        }
    }

    // Efecto para mostrar errores
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.circles),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(120.dp)
                .rotate(90f)
                .offset(x = (-25).dp, y = (-30).dp)
                .zIndex(1f),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary.copy(0.7f))
        )

        Scaffold(
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth().background(color = Color.Transparent),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(38.dp).padding(4.dp))
                    }
                    Text("Cursos", modifier = Modifier.fillMaxWidth().padding( horizontal = 16.dp),
                        textAlign = TextAlign.Start, style = MaterialTheme.typography.titleLarge)
                }
                if (state.isLoading && state.courses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.courses) { course ->
                            CourseItem(
                                course = course,
                                onButtonClick = {
                                    showRegistrationSheet = true
                                    viewModel.onRegisterClick(course)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Diálogo de inscripción
        if (state.selectedCourse != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onDialogDismiss() },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background
            ) {
                RegistrationForm(
                    courseTitle = state.selectedCourse?.title?:"",
                    formState = formState,
                    onFormValueChange = viewModel::updateFormField,
                    onDismiss = { viewModel.onDialogDismiss() },
                    onSubmit = viewModel::submitRegistration,
                    isLoading = state.isLoading
                )
            }
        }
    }
}