package com.tramites1cero1.tramiappquibdo.ui.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.initial.SelectMunScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.initial.SelectMunViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.initial.WelcomeScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel

fun NavGraphBuilder.initialNavGraph(navController: NavController, munViewModel: MunicipalityViewModel ){
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
            SelectMunScreen(navController = navController)
        }

    }
}