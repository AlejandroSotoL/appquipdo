package com.tramites1cero1.tramiappquibdo.ui.navigation.graphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.screen.psv.PsvScreen
import com.tramites1cero1.tramiappquibdo.utils.abrirURL

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.psvNavGraph(navController: NavHostController) {
    composable(
        route = AppRoutes.PSV_SCREEN,
        arguments = listOf(
            navArgument("taxId") { type = NavType.IntType },
            navArgument("taxName") { type = NavType.StringType }
        )
    ) {

        val context = LocalContext.current
        val primaryColor = MaterialTheme.colorScheme.primary

        PsvScreen(
            onBack = { navController.popBackStack() },
            onOpenUrl = { url ->
                abrirURL(
                    context = context,
                    url = url,
                    toolbarColor = primaryColor.toArgb()
                )
            }
        )
    }
}