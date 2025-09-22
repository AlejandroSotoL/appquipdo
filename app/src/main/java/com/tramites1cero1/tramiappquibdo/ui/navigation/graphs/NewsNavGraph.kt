package com.tramites1cero1.tramiappquibdo.ui.navigation.graphs

import android.os.Build
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideInFromLeft
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideInFromRight
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideOutToLeft
import com.tramites1cero1.tramiappquibdo.ui.navigation.slideOutToRight
import com.tramites1cero1.tramiappquibdo.ui.screen.news.NewsDetailsScreen
import com.tramites1cero1.tramiappquibdo.ui.screen.news.NewsScreen
import com.tramites1cero1.tramiappquibdo.utils.webview


fun NavGraphBuilder.newsNavGraph(
    navController: NavController,
    escudoUrl: String,
    nombreAlcaldia : String,
    newsUrl: String) {
    navigation(
        startDestination = AppRoutes.NEWS_SCREEN,
        route = AppRoutes.NEWS_NAV_GRAPH
    ) {

        composable(AppRoutes.NEWS_SCREEN){
            webview(
                navController = navController,
                url = newsUrl,
                titulo = "Noticias $nombreAlcaldia "
            )
        }
//        composable(AppRoutes.NEWS_SCREEN,
//            enterTransition = { slideInFromRight() },
//            popExitTransition = { slideOutToRight() },
//
//            exitTransition = { slideOutToLeft() },
//            popEnterTransition = { slideInFromLeft() },
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NewsScreen(navController = navController,
//                    escudoUrl = escudoUrl,
//                    nombreAlcaldia = nombreAlcaldia,
//                    newsUrl = newsUrl)
//            }
//        }
//
//        // Pantalla de Cursos
//        composable(
//            route = "${AppRoutes.NEWS_DETAILS_SCREEN}" + "/{noticiaId}?baseUrl={baseUrl}",
//            arguments = listOf(
//                navArgument("noticiaId") { type = NavType.IntType },
//                navArgument("baseUrl") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            NewsDetailsScreen(navController = navController)
//        }

    }
}