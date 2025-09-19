package com.tramites1cero1.centralizacion.ui.screen.settingsUser.components

import VerificationCodeInput
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.ui.screen.settingsUser.recoveryByForget.RecoveryPasswordViewModel
import com.tramites1cero1.centralizacion.ui.theme.Black
import com.tramites1cero1.centralizacion.ui.theme.ColorTextPrimary
import com.tramites1cero1.centralizacion.ui.theme.Gray300
import com.tramites1cero1.centralizacion.ui.theme.Gray600
import com.tramites1cero1.centralizacion.ui.theme.Red
import com.tramites1cero1.centralizacion.ui.theme.White
import com.tramites1cero1.centralizacion.ui.theme.primarycolor

@Composable
fun ShowModalVerificationCode(
    navController: NavController,
    isValida: Boolean,
    recovryVm: RecoveryPasswordViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Introduce el código de verificación.",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        VerificationCodeInput(navController) { code ->
            recovryVm.validateCode(code)
        }

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(2.dp, Gray600),
        ) {
            Text("Cancelar", style = MaterialTheme.typography.headlineSmall, color = ColorTextPrimary)
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}
