package com.tramites1cero1.tramiappquibdo.ui.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrds.PqrdsChoiceScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrds.PqrdsViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrdsanonimas.PqrdsAnonimaScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.pqrdsidentificacion.PqrdsIdentificationNavScreen


fun NavGraphBuilder.pqrdsNavGraph(
    navController: NavController,
    pqrdsViewModel: PqrdsViewModel,
    codigoEntidad: String
){
    navigation(
        startDestination = AppRoutes.PQRDS_CHOICE_SCREEN,
        route = AppRoutes.PQRDS_NAV_GRAPH
    ){
        composable(AppRoutes.PQRDS_CHOICE_SCREEN){
            PqrdsChoiceScreen(codigoEntidad = codigoEntidad)
        }

        composable(AppRoutes.PQRDS_ANONIMAS){
            PqrdsAnonimaScreen(navController = navController, viewModel = pqrdsViewModel, codigoEntidad = codigoEntidad)
        }
        composable(AppRoutes.PQRDS_IDENTIFICACION) {
            PqrdsIdentificationNavScreen(navController = navController, viewModel = pqrdsViewModel, codigoEntidad = codigoEntidad)
        }
    }
}