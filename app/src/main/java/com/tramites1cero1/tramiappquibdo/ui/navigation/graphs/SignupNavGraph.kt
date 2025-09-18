package com.tramites1cero1.tramiappquibdo.ui.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideInFromLeft
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideInFromRight
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideOutToLeft
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideOutToRight
import com.tramites1cero1.tramiappquibdo.ui.screen.signup.SignUpScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.recoveryByForget.ChangeOnlyPasswordScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.settingsUser.recoveryByForget.RecoveryPasswordScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.signup.SignUpThreeScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.signup.SignUpTwoScreen



fun NavGraphBuilder.signUpNavGraph(navController: NavController) {
    navigation(
        startDestination = AppRoutes.SIGNUP_STEPONE,
        route = AppRoutes.SIGNUP_NAV_GRAPH
    ) {
        composable(AppRoutes.SIGNUP_STEPONE,

            enterTransition = { slideInFromRight() },
            popExitTransition = { slideOutToRight() },

            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            ) {
            SignUpScreen(navController = navController)
        }

        composable(AppRoutes.SIGNUP_STEPTWO,

            enterTransition = { slideInFromRight() },
            popExitTransition = { slideOutToRight() },

            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            ) {
            SignUpTwoScreen(navController = navController)
        }

        composable(AppRoutes.SIGNUP_STEPTHREE,
            enterTransition = { slideInFromRight() },
            popExitTransition = { slideOutToRight() },

            exitTransition = { slideOutToLeft() },
            popEnterTransition = { slideInFromLeft() },
            ) {
            SignUpThreeScreen(navController = navController)
        }

        composable(AppRoutes.RECOVERY_PASSWORD) {
            RecoveryPasswordScreen(navController = navController)
        }

        composable(AppRoutes.CHANGE_ONLY_PASSWORD) {
            ChangeOnlyPasswordScreen(navController = navController)
        }
    }
}