package com.tramites1cero1.tramiappquibdo.ui.screen.psv

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tramites1cero1.tramiappquibdo.ui.components.TopbarNavigation
import com.tramites1cero1.tramiappquibdo.ui.components.StepIndicator
import com.tramites1cero1.tramiappquibdo.ui.components.ValidationErrorDialog
import com.tramites1cero1.tramiappquibdo.ui.screen.psv.components.Step1Form
import com.tramites1cero1.tramiappquibdo.ui.screen.psv.components.Step2Form
import com.tramites1cero1.tramiappquibdo.ui.screen.psv.components.Step3Summary

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PsvScreen(
    onBack: () -> Unit,
    onOpenUrl: (String) -> Unit

) {
    val viewModel: PsvPaymentViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current



    if (state.showValidationErrorDialog) {
        ValidationErrorDialog(onDismiss = viewModel::onValidationDialogDismissed)
    }

    LaunchedEffect(state.paymentUrl) {
        state.paymentUrl?.let { url ->
            onOpenUrl(url)
            viewModel.onNavigationHandled()
        }
    }

    TopbarNavigation(
        onBackPressed = {
            if (state.currentStep == 1) onBack() else viewModel.onPreviousStep()
        },
        icon = state.taxIconResId,
        isMenuEnabled = false,
        title = "Pago Seguros en Línea",
        description = state.taxName,
        scrollContent = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp)
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(horizontal = 18.dp)
                        .verticalScroll(rememberScrollState())
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { focusManager.clearFocus() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    StepIndicator(
                        currentStep = state.currentStep,
                        modifier = Modifier.padding(top = 24.dp, start = 32.dp, end = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedContent(
                        targetState = state.currentStep,
                        transitionSpec = {

                            if (targetState > initialState) {
                                slideInHorizontally { width -> width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width } + fadeOut()
                            } else {

                                slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> width } + fadeOut()
                            }
                        },
                        label = "StepAnimation"
                    ) { targetStep ->

                        key(targetStep) {
                            Column {
                                when (targetStep) {
                                    1 -> Step1Form(
                                        state = state,
                                        viewModel = viewModel,
                                        onNext = viewModel::onNextStep,
                                        onCancel = onBack
                                    )

                                    2 -> Step2Form(
                                        state = state,
                                        viewModel = viewModel,
                                        onNext = viewModel::onNextStep,
                                        onCancel = viewModel::onPreviousStep
                                    )

                                    3 -> Step3Summary(
                                        state = state,
                                        onPay = viewModel::onPayClicked,
                                        isProcessing = state.isProcessingPayment,
                                        onCancel = viewModel::onPreviousStep
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
}


@Composable
fun FormButtons(
    onNext: () -> Unit,
    onCancel: () -> Unit,
    backText: String = "Cancelar",
    nextText: String = "Siguiente",
    isNextEnabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 50.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onCancel, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ), modifier = Modifier
                .weight(1f)
                .height(50.dp)
        ) {
            Text(backText)
        }

        Button(
            onClick = onNext, enabled = isNextEnabled, modifier = Modifier
                .weight(1f)
                .height(50.dp)
        ) {
            Text(nextText)
        }
    }
}








