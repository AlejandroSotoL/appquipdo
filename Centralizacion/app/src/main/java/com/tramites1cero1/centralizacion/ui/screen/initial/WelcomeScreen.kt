package com.tramites1cero1.centralizacion.ui.screen.initial

import android.R.attr.textColor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.tramites1cero1.centralizacion.ui.components.FooterSponsors
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.initial.components.AdvertisementSection
import com.tramites1cero1.centralizacion.ui.screen.login.AuthViewModel
import com.tramites1cero1.centralizacion.ui.theme.ColorTextPrimary
import com.tramites1cero1.centralizacion.ui.theme.Roboto_medium
import com.tramites1cero1.centralizacion.ui.theme.Roboto_regular
import com.tramites1cero1.centralizacion.ui.theme.Roboto_semiBold
import com.tramites1cero1.centralizacion.ui.theme.primarycolor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun WelcomeScreen(
    navController: NavController,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val filteredDepartments by viewModel.filteredDepartments.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                popUpTo(AppRoutes.SELECT_MUN_SCREEN) { inclusive = true }
            }
        }
    }


    Scaffold(
        containerColor = primarycolor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp)
                .verticalScroll(rememberScrollState())
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusManager.clearFocus() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))
            HeaderWelcome(visible = state.areAnimationsVisible)
            AnimatedVisibility(
                visible = state.areAnimationsVisible,
                enter = fadeIn(tween(800, delayMillis = 600)) + slideInVertically(
                    animationSpec = tween(800, delayMillis = 600),
                    initialOffsetY = { it }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        textStyle = MaterialTheme.typography.bodyMedium,
                        placeholder = { Text("Busca tu departamento...", style = MaterialTheme.typography.titleSmall, color = ColorTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                viewModel.onDropdownFocusChanged(focusState.isFocused)
                            }
                            .height(56.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = ColorTextPrimary,
                            unfocusedTextColor = ColorTextPrimary
                        )
                    )

                    AnimatedVisibility(
                        visible = state.isDropdownVisible && filteredDepartments.isNotEmpty(),
                        enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                        exit = fadeOut(tween(300)) + shrinkVertically(tween(300))
                    ) {
                        val sortedDepartments = filteredDepartments.sortedBy { it.name }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .heightIn(max = 200.dp)
                                .padding(top = 4.dp)
                        ) {
                            items(filteredDepartments.size) { index ->
                                val depto = sortedDepartments[index]
                                DropdownMenuItem(
                                    text = { Text(text = depto.name, style = MaterialTheme.typography.titleSmall, color = ColorTextPrimary) },
                                    onClick = {
                                        scope.launch {
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            delay(160)
                                            viewModel.onDepartmentSelected(depto)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            AnimatedVisibility(
                visible = state.areAnimationsVisible,
                enter = fadeIn(tween(800, delayMillis = 100)) +
                        slideInVertically(
                            animationSpec = tween(800, delayMillis = 800),
                            initialOffsetY = { -it }
                        ) +
                        scaleIn(tween(800, delayMillis = 100))
            ) {
                AdvertisementSection(
                    context = context,
                    images = state.carouselImages
                )
            }

            Spacer(modifier = Modifier.weight(1f).height(10.dp))

            FooterSponsors("", Color.White)

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun HeaderWelcome (
    visible: Boolean,
    authViewModel: AuthViewModel = hiltViewModel(),
){
    //----------------PREFERENCES USER------------------------
    val user by authViewModel.user.collectAsState(initial = null);
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(900)) + slideInVertically(
            animationSpec = tween(900),
            initialOffsetY = { fullHeight -> fullHeight }
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Image(
//                painter = painterResource(R.drawable.newtramiapp),
//                contentDescription = "Logo Software",
//                modifier = Modifier.fillMaxWidth().height(50.dp)
//            )
            Text(
                text = "¡Bienvenido!",
                fontSize = 42.sp,
                fontFamily = Roboto_semiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if(user?.firstName != null){
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Hola ${user?.firstName}",
                    fontSize = 28.sp,
                    fontFamily = Roboto_medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(
            animationSpec = tween(800, delayMillis = 200),
            initialOffsetY = { fullHeight -> fullHeight }
        )
    ) {
        Text(
            text = "Vamos a configurar tu aplicación\nElige tu departamento",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
            fontFamily = Roboto_regular,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }

}

