package com.tramites1cero1.centralizacion.ui.screen.initial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.tramites1cero1.centralizacion.MainActivity
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.data.network.ConnectivityObserver
import com.tramites1cero1.centralizacion.domain.model.Design
import com.tramites1cero1.centralizacion.ui.components.NoConnectionDialog
import com.tramites1cero1.centralizacion.ui.theme.AlcaldiasTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver
    private var connectionJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        val image = findViewById<ImageView>(R.id.loadgif)
        Glide.with(applicationContext).load(R.drawable.loadscreennew).into(image)

        lifecycleScope.launch {
            delay(3500) // Tiempo para mostrar el splash
            checkConnectionAndProceed()
        }
    }

    //NETWORK VALIDATION METHODS
    private fun checkConnectionAndProceed() {
        lifecycleScope.launch {
            val status = connectivityObserver.observe().first()
            if (status == ConnectivityObserver.Status.Available) {
                navigateToMain()
            } else {
                showNoConnectionDialog()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
        finish()
    }

    private fun showNoConnectionDialog() {
        connectionJob?.cancel()

        val dialogView = ComposeView(this)
        val isConnectionRestored = mutableStateOf(false)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.setContent {
            AlcaldiasTheme(
                design = Design(),
                darkTheme = isSystemInDarkTheme()
            ) {
                NoConnectionDialog(
                    isConnectionRestored = isConnectionRestored.value,
                    onDismiss = {
                        dialog.dismiss()
                        checkConnectionAndProceed()
                    }
                )
            }
        }

        connectionJob = lifecycleScope.launch {
            connectivityObserver.observe().collectLatest { status ->
                isConnectionRestored.value = (status == ConnectivityObserver.Status.Available)
            }
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        connectionJob?.cancel()
    }
}