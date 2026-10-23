package com.example.dnd_quedamos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dnd_quedamos.di.AppContainer
import com.example.dnd_quedamos.di.DefaultAppContainer
import com.example.dnd_quedamos.features.evento.domain.usecase.ManagerEventoUseCase
import com.example.dnd_quedamos.features.evento.presentation.EventoViewModel
import com.example.dnd_quedamos.features.evento.ui.HomeEventViewRoot

data class Objeto(
    val id: Int,
    val nombre: String,
    val descripcion: String
)

// Lista fija de ejemplo, con pocos elementos
val objetosDeEjemplo = (1..30).map { i ->
    Objeto(id = i, nombre = "Objeto $i", descripcion = "Descripción de ejemplo $i")
}

@Composable
fun BottomNavButton(
    emoji: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            //.clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(label, fontSize = 12.sp, color = color)
    }
}
@Composable
fun App( appContainer: AppContainer = DefaultAppContainer() ) {
    val navController = rememberNavController();

    NavHost(navController = navController, startDestination = Route.Home) {
        composable<Route.Home> {
            val viewModel = remember {
                EventoViewModel(appContainer.useManagerEventoUseCase)
            }
            HomeEventViewRoot(/* viewModel = ... , onButtonClick = { navController.navigate(Route...) }   */)
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme() // DnD = dark fantasy vibe
    ) {
        Scaffold(
            modifier =Modifier
                .fillMaxWidth(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "DnD Quedamos",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            },

            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    BottomNavButton(emoji = "🏠", label = "Inicio", selected = true) { }
                    BottomNavButton(emoji = "🧙", label = "Personajes", selected = false) { }
                    BottomNavButton(emoji = "📜", label = "Partidas", selected = false) { }
                    BottomNavButton(emoji = "⚙️", label = "Ajustes", selected = false) { }
                }
            }



        ) { innerPadding ->
            EventoScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

// Use the 'viewModel()' function from the lifecycle-viewmodel-compose artifact
@Composable
fun EventoScreen(
    modifier: Modifier = Modifier,
    viewModel: EventoViewModel = EventoViewModel(ManagerEventoUseCase())
) {
    // 1. Inicializa el ViewModel de forma segura una sola vez
    LaunchedEffect(Unit) {
        viewModel.inicializar()
    }

    // 2. Observa el estado de la UI de manera reactiva
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 3. Pintamos la lista directamente aquí dentro de la función Composable
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(uiState.dataEventos) { evento ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Ajusta estos campos según las propiedades reales de tu clase 'DataEvento'
                    Text(evento.nombre, fontWeight = FontWeight.Bold)
                    Text(evento.descripcion, fontSize = 14.sp)
                }
            }
        }
    }
}