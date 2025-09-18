package com.tramites1cero1.tramiappquibdo.ui.navigation.graphs

import android.annotation.SuppressLint
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tramites1cero1.tramiappquibdo.MunicipalityViewModel
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideInFromLeft
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideInFromRight
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideOutToLeft
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideOutToRight
import com.tramites1cero1.tramiappquibdo.ui.screen.initial.SelectMunViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.main.MainViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.publicservices.PublicServicesScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.publicservices.PSSValidationViewModel
import com.tramites1cero1.tramiappquibdo.ui.screen.publicservices.PSFPaymentViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
fun NavGraphBuilder.PublicServiceNavGraph(
    navController: NavController,
    munViewModel: MunicipalityViewModel
) {

    navigation(
        startDestination = AppRoutes.PUBLIC_SERVICE_SCREEN, // Pantalla de inicio de este flujo
        route = AppRoutes.PUBLIC_SERVICE_NAV_GRAPH
    ) {
    }
        composable(AppRoutes.PUBLIC_SERVICE_SCREEN,
            enterTransition = { slideInFromRight() },
            popExitTransition = { slideOutToRight() },

            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
        ) {
            val parentEntry = remember { navController.getBackStackEntry(AppRoutes.PUBLIC_SERVICE_SCREEN) }
            val PSSValidationViewModel: PSSValidationViewModel = hiltViewModel(parentEntry)
            val PSFPaymentViewModel: PSFPaymentViewModel = hiltViewModel(parentEntry)
            PublicServicesScreen(navController = navController,
                PSSValidationViewModel = PSSValidationViewModel,
                PSFPaymentViewModel = PSFPaymentViewModel,
                munViewModel = munViewModel)
        }


//    //SE PUEDE QUITAR
//        composable(AppRoutes.PUBLIC_SERVICE_FORM,
//            enterTransition = { slideInFromRight() },
//            popExitTransition = { slideOutToRight() }
//        ) {
//            val parentEntry = remember { navController.getBackStackEntry(AppRoutes.PUBLIC_SERVICE_SCREEN) }
//            val PSSValidationViewModel: PSSValidationViewModel = hiltViewModel(parentEntry)
//            val PSFPaymentViewModel: PSFPaymentViewModel = hiltViewModel(parentEntry)
//
//            PublicServicesForm(
//                navController = navController,
//                PSSValidationViewModel = PSSValidationViewModel,
//                PSFPaymentViewModel = PSFPaymentViewModel,
//            )
//        }
}