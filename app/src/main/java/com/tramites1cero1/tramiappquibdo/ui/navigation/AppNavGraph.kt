package com.tramites1cero1.tramiappquibdo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.PublicServiceNavGraph
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.initialNavGraph
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.mainNavGraph
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.newsNavGraph
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.paymentsNavGraph
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.signUpNavGraph
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tramites1cero1.tramiappquibdo.MunicipalityUiState
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.domain.model.Design
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.pqrdsNavGraph
import com.tramites1cero1.tramiappquibdo.ui.navigation.graphs.psvNavGraph

import com.tramites1cero1.tramiappquibdo.ui.screen.pqrds.PqrdsViewModel
import com.tramites1cero1.tramiappquibdo.ui.theme.AlcaldiasTheme
import com.tramites1cero1.tramiappquibdo.ui.theme.primarycolor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavHost(isDark: Boolean, navController: NavHostController, startDestination : String) {
    val munViewModel: MunicipalityViewModel = hiltViewModel()
    val munState by munViewModel.uiState.collectAsStateWithLifecycle()
    val pqrdsViewModel : PqrdsViewModel = hiltViewModel()

    when (val state = munState) {
        is MunicipalityUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(primarycolor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        is MunicipalityUiState.Success -> {
            val design = state.data.design
            val nombreAlcaldia = state.data.nombreMunicipio
            val escudoUrl = state.data.design.escudoUrl
            val newsUrl = state.data.newsUrl
            val deparment = state.data.departamento

            AlcaldiasTheme(
                design = design,
                darkTheme = isDark
            ) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(300)) },
                    exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(300)) },
                    popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) },
                    popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) }
                ) {

                    mainNavGraph(navController, munViewModel)
                    newsNavGraph(navController, escudoUrl = escudoUrl, nombreAlcaldia = nombreAlcaldia, newsUrl = newsUrl)
                    paymentsNavGraph(navController )
                    PublicServiceNavGraph(navController, munViewModel)
                    signUpNavGraph(navController)
                    pqrdsNavGraph(navController, pqrdsViewModel, codigoEntidad = state.data.codigoEntidad)
                    psvNavGraph(navController)
                }
            }
        }
        is MunicipalityUiState.Error -> {

        }
        else -> {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                mainNavGraph(navController, munViewModel)
                paymentsNavGraph(navController)
                newsNavGraph(navController, escudoUrl = "", nombreAlcaldia = "", newsUrl = "")
                PublicServiceNavGraph(navController, munViewModel)
                initialNavGraph(navController, munViewModel)
                signUpNavGraph(navController)
                pqrdsNavGraph(navController, pqrdsViewModel, "")
                psvNavGraph(navController)
            }
        }
    }


}