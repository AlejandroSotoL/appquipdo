package com.tramites1cero1.centralizacion.ui.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tramites1cero1.centralizacion.MunicipalityViewModel
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.history.HistoryPayScreen
import com.tramites1cero1.centralizacion.ui.screen.initial.SelectMunScreen
import com.tramites1cero1.centralizacion.ui.screen.initial.WelcomeScreen
import com.tramites1cero1.centralizacion.ui.screen.login.LoginOptionsScreen

fun NavGraphBuilder.initialNavGraph(navController: NavController, munViewModel: MunicipalityViewModel){
    navigation(
        startDestination = AppRoutes.WELCOMESCREEN,
        route = AppRoutes.INITIAL_NAV_GRAPH
    ){
        composable(AppRoutes.WELCOMESCREEN) { backStackEntry ->
            WelcomeScreen(navController = navController)
        }

        composable(
            route = "${AppRoutes.SELECT_MUN_SCREEN}/{departmentId}",
            arguments = listOf(navArgument("departmentId") { type = NavType.IntType })
        ){
            SelectMunScreen(navController = navController, municipalityViewModel = munViewModel)
        }

    }
}