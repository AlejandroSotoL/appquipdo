package com.tramites1cero1.tramiappquibdo.ui.screen.main.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tramites1cero1.tramiappquibdo.R
import com.tramites1cero1.tramiappquibdo.ui.navigation.AppRoutes
import com.tramites1cero1.tramiappquibdo.ui.theme.Gray300
import android.graphics.Color as AndroidColor


enum class Destination(
    val route: String,
    val label: String,
    val iconResource: Int,
    val contentDescription: String
) {
    MAIN(AppRoutes.MAINSCREEN, "Inicio", R.drawable.icohome, "Menu"),
    NEWS(AppRoutes.NEWS_SCREEN, "Noticias", R.drawable.iconoticias, "Noticias"),
    PORTAL("portalWeb", "Portal", R.drawable.icoportal, "Portal Web"),
//    HISTORIAL(AppRoutes.HISTORY_PAY_SCREEN, "Historial", R.drawable.icohistorial, "Historial")
}

data class BottomNavItem(
    val label: String,
    val iconResource: Int,
    val route: String // Una ruta o clave única para identificar el item
)


data class BottomNavBarState(
    val items: List<BottomNavItem> = emptyList(),
    val selectedRoute: String = "",
)

// Contiene todas las acciones que el usuario puede realizar
data class BottomNavBarActions(
    val onNavItemClicked: (item: BottomNavItem) -> Unit
)

fun darkenColor(color: Color, factor: Float): Color {
    val hsv = FloatArray(3)
    // Convierte el color de Compose a un entero ARGB y luego a HSV
    AndroidColor.colorToHSV(color.toArgb(), hsv)
    // Reduce el componente 'Value' (brillo) por el factor
    hsv[2] *= factor
    // Vuelve a convertir de HSV a un entero ARGB y luego a un color de Compose
    return Color(AndroidColor.HSVToColor(hsv))
}

@Composable
fun BottomNavBar(
                 state: BottomNavBarState,
                 actions: BottomNavBarActions){
    val startDestination = Destination.MAIN
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val darkerPrimaryColor = darkenColor(MaterialTheme.colorScheme.primary, factor = 0.8f)


    Surface(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    ){
        NavigationBar(
           containerColor = MaterialTheme.colorScheme.primary,
            windowInsets = NavigationBarDefaults.windowInsets
        ) {
            Destination.entries.forEachIndexed { index, destination ->
                NavigationBarItem(
                    selected = selectedDestination == index,
                    onClick = {
                        actions.onNavItemClicked(state.items[index])
                    },
                    icon = { Icon(
                        painter = painterResource(destination.iconResource),
                        contentDescription = destination.contentDescription,
                        modifier = Modifier.size(20.dp)) },
                    label = { Text(destination.label) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,  // Color del ícono cuando está seleccionado
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,// Color del texto cuando está seleccionado (un morado oscuro)
                        indicatorColor = darkerPrimaryColor, // Color del fondo "píldora" del ítem seleccionado (un morado claro)
                        unselectedIconColor = Gray300,// Color del ícono cuando NO está seleccionado
                        unselectedTextColor = Gray300  // Color del texto cuando NO está seleccionado
                    )
                )
            }
        }
    }
}
