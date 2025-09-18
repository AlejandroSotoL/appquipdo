package com.tramites1cero1.tramiappquibdo

import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.tramites1cero1.tramiappquibdo.data.network.ConnectivityObserver
import com.tramites1cero1.tramiappquibdo.ui.components.NoConnectionDialog
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppNavHost
import com.tramites1cero1.tramiappquibdo.ui.theme.InicialTheme
import com.tramites1cero1.tramiappquibdo.ui.theme.primarycolor
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    val RESULT_CODE_UPDATE = 101
    private lateinit var appUpdateManager: AppUpdateManager
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        // Manejamos el resultado de la actualización
        if (result.resultCode != Activity.RESULT_OK) {
            Log.d("IN_APP_UPDATE", "La actualización fue cancelada o falló. Código: ${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //IN APP UPDATE MANAGER PARA ACTUALIZACIONES IN APP
        appUpdateManager = AppUpdateManagerFactory.create(this)
        // 4. Llamamos a la función para buscar actualizaciones
        activityResultLauncher
        checkForAppUpdate()

        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
            val showDialog by viewModel.showNoConnectionDialog.collectAsStateWithLifecycle()


            InicialTheme(
                darkTheme = state.isDarkTheme
            ) {
                if (showDialog) {
                    NoConnectionDialog(
                        isConnectionRestored = networkStatus == ConnectivityObserver.Status.Available,
                        onDismiss = viewModel::dismissNoConnectionDialog
                    )
                }
                else if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(primarycolor),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                else {
                    val navController = rememberNavController()

                    AppNavHost(
                        isDark = state.isDarkTheme,
                        navController = navController,
                        startDestination = state.startDestination
                    )
                }
            }
        }
    }



    private fun checkForAppUpdate() {
        // Obtenemos la información de la actualización
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // Verificamos si hay una actualización disponible y si es del tipo INMEDIATA
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this, RESULT_CODE_UPDATE
                    )
                } catch (e: SendIntentException) {
                    e.printStackTrace()
                }
            }else{
                Log.d("INAPP_UPDATE", "No hay Actualizaciones")
            }
        }.addOnFailureListener { e ->
            // Manejo de errores en caso de que no se pueda verificar la actualización
            Log.e("IN_APP_UPDATE", "Error al buscar la actualización.", e)
        }
    }


}




