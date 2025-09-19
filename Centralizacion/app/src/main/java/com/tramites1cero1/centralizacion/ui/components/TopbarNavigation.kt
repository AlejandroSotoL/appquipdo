package com.tramites1cero1.centralizacion.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tramites1cero1.centralizacion.R
import com.tramites1cero1.centralizacion.ui.theme.Gray900
import com.tramites1cero1.centralizacion.ui.theme.InicialTheme
import com.tramites1cero1.centralizacion.ui.theme.White


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopbarNavigation(
    onBackPressed:() -> Unit,
    icon: Int? = 0,
    isMenuEnabled: Boolean = false,
    title: String? = "",
    description: String? = "",
    scrollContent: @Composable (innerPadding: PaddingValues) -> Unit = {null}
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    val showDetails = scrollBehavior.state.collapsedFraction < 0.5f
                    Row(){
                        if(showDetails){
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(65.dp)
                                    .clip(CircleShape)
                                    .background(color = White, shape = CircleShape)
                            ) {
                                Icon(
                                    painter = painterResource(id = icon?: 0),
                                    contentDescription = "ICONO",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(45.dp)
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(id = icon?: 0),
                                contentDescription = "ICONO",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(36.dp).padding(3.dp)
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth().padding(5.dp),
                            horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center){
                            Text(
                                title?: "",
                                maxLines = 1,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                                textAlign = TextAlign.Start,
                            )
                            if(showDetails){
                                Text(
                                    description?: "",
                                    maxLines =2,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed as () -> Unit,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .background(White, shape = RoundedCornerShape(40.dp))
                            .size(35.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Menú",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }
                },
                actions = {
                    if(isMenuEnabled){
                        IconButton(onClick = { /* do something */ }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Localized description",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        scrollContent(innerPadding)
    }
}

@Preview(showBackground = true, name = "Topbar with Very Long Title")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopbarNavigationLongTitlePreview() {
    InicialTheme {
        TopbarNavigation(
            onBackPressed = { },
            icon = R.drawable.icopredial, // Placeholder
            isMenuEnabled = true,
            title = "Pago sin validacion",
            description = "Este es un título extremadamente largo diseñado para probar cómo se maneja el desbordamiento de texto y las capacidades de elipsis del componente TopAppBar.",
            scrollContent = { innerPadding ->
                LazyColumn(
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(20) { index ->
                        Text(
                            text = "Item de prueba ${index + 1}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        )
    }
}