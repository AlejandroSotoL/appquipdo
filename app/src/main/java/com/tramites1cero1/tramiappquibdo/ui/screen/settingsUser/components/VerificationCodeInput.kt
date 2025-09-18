import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tramites1cero1.tramiappquibdo.ui.theme.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key

@Composable
fun VerificationCodeInput(
    navController: NavController,
    onCodeComplete: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val focusRequesters = List(6) { remember { FocusRequester() } }
    var code by remember { mutableStateOf(List(6) { "" }) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        code.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = {
                    if (it.length <= 1 && it.all { char -> char.isDigit() }) {
                        code = code.toMutableList().also { list -> list[index] = it }
                        if (it.isNotEmpty() && index < 5) {
                            focusRequesters[index + 1].requestFocus()
                        } else if (index == 5 && code.all { it.isNotEmpty() }) {
                            onCodeComplete(code.joinToString(""))
                            focusManager.clearFocus()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequesters[index])
                    .onFocusChanged {
                        if (it.isFocused && code[index].isNotEmpty()) {
                            code = code.toMutableList().also { list -> list[index] = "" }
                        }
                    }
                    .onKeyEvent { event ->
                        // Si la tecla presionada es "Backspace" Y el campo está vacío
                        if (event.key == Key.Backspace && code[index].isEmpty()) {
                            // Y no es el primer campo (index > 0)
                            if (index > 0) {
                                // Mueve el foco al campo anterior
                                focusRequesters[index - 1].requestFocus()
                            }
                            return@onKeyEvent true // Evento consumido
                        }
                        false // Evento no consumido
                    },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = Roboto_bold_condensed,
                    color = ColorTextPrimary,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = ColorTextPrimary,
                    backgroundColor = Color.Transparent,
                    focusedBorderColor = Gray900,
                    unfocusedBorderColor = Gray600,
                    cursorColor = Gray900
                ),
                shape = RoundedCornerShape(12.dp)
            )

        }
    }
}

