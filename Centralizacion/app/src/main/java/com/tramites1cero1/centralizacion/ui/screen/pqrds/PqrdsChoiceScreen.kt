package com.tramites1cero1.centralizacion.ui.screen.pqrds

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close // Importa el icono de cerrar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
// Importa painterResource si aún lo necesitas para otras cosas, pero no para este icono.
// import androidx.compose.ui.res.painterResource
import com.tramites1cero1.centralizacion.R // Asegúrate de que R esté importado si usas tus propios Drawables en otras partes
import com.tramites1cero1.centralizacion.ui.navigation.AppRoutes
import com.tramites1cero1.centralizacion.ui.screen.main.AnimatedSection
import com.tramites1cero1.centralizacion.ui.screen.main.MainViewModel
import com.tramites1cero1.centralizacion.ui.theme.Gray600
import com.tramites1cero1.centralizacion.ui.theme.White

@Preview
@Composable
fun PqrdsChoiceScreen(
    mainViewModel: MainViewModel? = null,// Hago que navController sea nullable para el Preview
    codigoEntidad: String = ""
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 180.dp)
            .background(color = MaterialTheme.colorScheme.background, RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "PQRSDF",
            color =  MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Seleccione el tipo de PQRSDF",
            color =  MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(14.dp))
        ChoiceCard(
            text = "P.Q.R.S.D Identificación",
            description = "Podrá recibir un radicado y hacer seguimiento",
            icon = Icons.Default.Person,
            backgroundImageRes = R.drawable.fondopqrdidentifi,
            iconPqrd = R.drawable.ico_pqrdidentificacion,
            onClick = { mainViewModel?.navToPqrdsI()  }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Tarjeta para PQRSDF Anónima
        ChoiceCard(
            text = "P.Q.R.S.D Anónima",
            description = "No se guardan sus datos ni podrá consultar la respuesta",
            icon = Icons.Default.LockPerson,
            backgroundImageRes = R.drawable.fondopqrds,
            iconPqrd = R.drawable.ico_pqrdanonima,
            onClick = { mainViewModel?.navToPqrdsA() }
        )

        Spacer(modifier = Modifier.height(10.dp))

       Button(
            onClick = { mainViewModel?.onPqrdsCancel() }, // Ahora lo uso para mostrar el diálogo en el preview
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gray600,
                contentColor = White
            ),
            shape = RoundedCornerShape(26.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 12.dp),
        ){
            Text("Cancelar", style = MaterialTheme.typography.titleMedium, color = White)
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
fun InformationDialog(
    onDismissRequest: () -> Unit,
    onAnonymousClick: () -> Unit,
    onIdentificationClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        // Ícono de 'X' para cerrar
        icon = {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }
        },
        // Título del diálogo
        title = {
            Text(
                text = "Mensaje del sistema de información",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )
        },
        // Contenido del diálogo
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ciudadano/a tenga en cuenta que la información a registrar en el siguiente formulario se encuentra protegida bajo la ley del Habeas data - Ley 1581 de 2012, Decreto1377 de 2013- si quiere conocer más sobre esta ley de click:",
                    textAlign = TextAlign.Justify,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "http://www.mintic.gov.co/portal/604/articles-4274_documento.pdf",
                    color = Color.Blue,
                    fontSize = 12.sp
                )
            }
        },
        // Acciones del diálogo (los botones de abajo)
        confirmButton = {}, // Lo dejamos vacío porque manejaremos los botones manualmente
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onAnonymousClick) {
                    Text("PQRSDF Anónima")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onIdentificationClick) {
                    Text("PQRSDF Identificación")
                }
            }
        }
    )
}

@Composable
fun ChoiceCard(
    text: String,
    description : String,
    icon: ImageVector,
    backgroundImageRes: Int,
    iconPqrd: Int,
    onClick: () -> Unit
) {
    AnimatedSection(200) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .height(180.dp)
                .clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {

            Box(modifier = Modifier.fillMaxSize()) {
                // Imagen de fondo
                Image(
                    painter = painterResource(id = iconPqrd),
                    contentDescription = null,
                    contentScale = ContentScale.Inside, // Esto hace que la imagen cubra todo el espacio
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 20.dp)
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .zIndex(1f),
                )
                Image(
                    painter = painterResource(id = backgroundImageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // Esto hace que la imagen cubra todo el espacio
                    modifier = Modifier.fillMaxSize()
                )
                // Contenido sobre la imagen
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp)) // Recorta el área del desenfoque con esquinas redondeadas
                    ){
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary) // Un fondo semitransparente ayuda al efecto
                                .blur(radius = 20.dp)
                                .padding(horizontal = 32.dp, vertical = 16.dp)
                        ){
                            Text(
                                text = "",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White // O el color que se vea mejor sobre tus imágenes
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = text,
                                    color = Color.White, // Usamos blanco para que contraste bien
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp)
                                )
                                Text(
                                    text = description,
                                    color = Color.White, // Usamos blanco para que contraste bien
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}