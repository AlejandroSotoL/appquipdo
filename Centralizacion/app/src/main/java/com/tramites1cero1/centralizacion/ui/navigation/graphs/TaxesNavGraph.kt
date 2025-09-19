package com.tramites1cero1.centralizacion.ui.navigation.graphs

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.tramites1cero1.centralizacion.domain.model.Tax
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import com.tramites1cero1.centralizacion.ui.screen.taxpayments.TaxQueryScreen
import com.tramites1cero1.centralizacion.ui.screen.taxpayments.TaxResultsScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.tramites1cero1.centralizacion.ui.navigation.slideInFromLeft
import com.tramites1cero1.centralizacion.ui.navigation.slideInFromRight
import com.tramites1cero1.centralizacion.ui.navigation.slideOutToLeft
import com.tramites1cero1.centralizacion.ui.navigation.slideOutToRight
import com.tramites1cero1.centralizacion.ui.screen.taxpayments.TaxQueryViewModel
import com.tramites1cero1.centralizacion.ui.screen.taxpayments.TaxResultsViewModel
import com.tramites1cero1.centralizacion.utils.abrirURL


fun NavGraphBuilder.paymentsNavGraph(navController: NavController) {
    navigation(
        startDestination = AppRoutes.TAX_QUERY_SCREEN,
        route = AppRoutes.PAYMENTS_NAV_GRAPH
    ) {

        composable(
            route = AppRoutes.TAX_QUERY_SCREEN,

            enterTransition = { slideInFromRight() },
            popExitTransition = { slideOutToRight() },

            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },

            arguments = listOf(
                navArgument("entityCode") { type = NavType.StringType },
                navArgument("queryFieldsJson") { type = NavType.StringType },
                navArgument("taxId") { type = NavType.IntType },
                navArgument("dataPolicyUrl") { type = NavType.StringType },
                navArgument("privacyPolicyUrl") { type = NavType.StringType }
            )
        ) {
            val viewModel: TaxQueryViewModel = hiltViewModel()

            TaxQueryScreen(
                onCancel = { navController.popBackStack() },
                onQuerySuccess = { taxes, email ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("taxes", ArrayList(taxes))
                    navController.currentBackStackEntry?.savedStateHandle?.set("email", email)
                    navController.navigate(AppRoutes.TAX_RESULTS_SCREEN)
                }
            )
        }

        // Pantalla 2: Resultados y opción de pago
        composable(
            route = AppRoutes.TAX_RESULTS_SCREEN,
            enterTransition = { slideInFromRight() },
            popExitTransition = { slideOutToRight() }

            ) {
            //  Recuperamos AMBOS datos desde la pantalla anterior.
            val taxes = navController.previousBackStackEntry?.savedStateHandle?.get<ArrayList<Tax>>("taxes") ?: arrayListOf()
            val email = navController.previousBackStackEntry?.savedStateHandle?.get<String>("email") ?: ""

            val context = LocalContext.current
            val viewModel: TaxResultsViewModel = hiltViewModel()
            val primaryColor = MaterialTheme.colorScheme.primary


            TaxResultsScreen(
                viewModel = viewModel,
                taxes = taxes,
                userEmail = email,
                onBack = { navController.popBackStack() },
                onGoToMain = {
                    navController.navigate(AppRoutes.MAINSCREEN) {
                        popUpTo(AppRoutes.PAYMENTS_NAV_GRAPH) { inclusive = true }
                    }
                },
                openUrl = { url ->
                    abrirURL(
                        context = context,
                        url = url,
                        toolbarColor = primaryColor.toArgb()
                    )
                }
            )
        }
    }
}