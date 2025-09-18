package com.tramites1cero1.tramiappquibdo.ui.navigation


import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

const val ANIMATION_DURATION = 500

// Animación para entrar desde la derecha (navegación hacia adelante)
fun slideInFromRight(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { it }, // 'it' es el ancho completo de la pantalla
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
}

// Animación para salir hacia la izquierda (navegación hacia adelante)
fun slideOutToLeft(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { -it / 3 }, // Se desliza solo un tercio
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
}

// Animación para entrar desde la izquierda (al volver atrás)
fun slideInFromLeft(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeIn(animationSpec = tween(ANIMATION_DURATION))
}

// Animación para salir hacia la derecha (al volver atrás)
fun slideOutToRight(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(
            durationMillis = ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        )
    ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
}