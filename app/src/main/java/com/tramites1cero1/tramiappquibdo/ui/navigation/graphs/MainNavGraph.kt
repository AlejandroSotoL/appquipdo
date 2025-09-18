package com.tramites1cero1.tramiappquibdo.ui.navigation.graphs

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tramites1cero1.tramiappquibdo.MunicipalityUiState
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideInFromLeft
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideOutToLeft
import com.tramites1cero1.tramiappquibdo.ui.screen.VenuesScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.courses.CoursesScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.history.HistoryPayScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.initial.SelectMunViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.UserSettingsScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.venues.VenuesViewModel


fun NavGraphBuilder.mainNavGraph(navController: NavController, munViewModel: MunicipalityViewModel) {
    navigation(
        startDestination = AppRoutes.MAINSCREEN,
        route = AppRoutes.MAIN_NAV_GRAPH
    ) {
        composable(
            route = AppRoutes.MAINSCREEN,
            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() }
        ) {
            MainScreen(navController = navController, munViewModel = munViewModel)
        }

        // Pantalla de Cursos
        composable(AppRoutes.COURSES_SCREEN) {
            CoursesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.HISTORY_PAY_SCREEN) {
            val state by munViewModel.uiState.collectAsStateWithLifecycle()
            when (val newState = state) {
                MunicipalityUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center){
                        CircularProgressIndicator()
                    }
                }
                is MunicipalityUiState.Success -> {
                    val bank = newState.data.bank
                    HistoryPayScreen(navController = navController, bank)
                }
                is MunicipalityUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center){
                        Text(text = "Ocurrió un error al cargar los datos", modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    HistoryPayScreen(navController = navController, "")
                }
            }

        }

        composable(AppRoutes.CONFIGURATIONS_USER_SCREEN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                UserSettingsScreen()
            }
        }

        // Pantalla de Escenarios Deportivos
        composable(AppRoutes.VENUES_SCREEN) {
            val venuesViewModel: VenuesViewModel = hiltViewModel()
            VenuesScreen(
                viewModel = venuesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }


    }
}