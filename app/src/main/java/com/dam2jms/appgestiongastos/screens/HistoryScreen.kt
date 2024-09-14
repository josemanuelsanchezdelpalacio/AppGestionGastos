package com.dam2jms.appgestiongastos.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.DatePickerComponents.showDatePicker
import com.dam2jms.appgestiongastos.components.ItemComponents.categoriaItem
import com.dam2jms.appgestiongastos.components.ScreenComponents.menu
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.data.CategoriaAPI.obtenerCategorias
import com.dam2jms.appgestiongastos.models.EditTransactionViewModel
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, mvvm: EditTransactionViewModel){

    val uiState by mvvm.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //para el menu lateral y la barra superior
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            menu(navController = navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Historial de transacciones", color = Blanco) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if(isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "icono menu", tint = Blanco)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "atras", tint = Blanco)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaOscuro)
                )
            },
            content = { paddingValues ->
                HistoryScreenBody(paddingValues = paddingValues, uiState = uiState, mvvm = mvvm)
            }
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreenBody(paddingValues: PaddingValues, uiState: UiState, mvvm: EditTransactionViewModel){

    var busquedaCategoria by remember { mutableStateOf("") }
    var busquedaFecha by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }
    var transaccionesFiltradas by remember { mutableStateOf(uiState.ingresos + uiState.gastos) }

    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    LaunchedEffect(tipoSeleccionado) {
        if(!tipoSeleccionado.isNullOrEmpty()){
            categorias = obtenerCategorias(tipoSeleccionado!!)
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = busquedaCategoria,
            onValueChange = { busquedaCategoria = it },
            label = { Text("Buscar por categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = uiState.fecha,
            onValueChange = {},
            label = { Text("Buscar por fecha (dd/MM/yyyy)") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker(context, LocalDate.parse(uiState.fecha)) { nuevaFecha ->
                        mvvm.actualizarDatosTransaccion(fecha = nuevaFecha.format(DateTimeFormatter.ISO_DATE))
                    }
                },
            enabled = false,
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Icono calendario") }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tipoSeleccionado == "ingreso",
                    onClick = { tipoSeleccionado == "ingreso" },
                    colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
                )
                Text(text = "Ingreso")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tipoSeleccionado == "gasto",
                    onClick = { tipoSeleccionado == "gasto" },
                    colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
                )
                Text(text = "Gasto")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tipoSeleccionado == null,
                    onClick = { tipoSeleccionado == null },
                    colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
                )
                Text(text = "Todos")
            }
        }

        if(!tipoSeleccionado.isNullOrEmpty()){
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(categorias){ categoria ->
                    categoriaItem(
                        categoria = categoria,
                        onClick = {
                            busquedaCategoria = categoria.nombre
                        }
                    )
                }
            }
        }else{
            Text(
                text = "No hay transacciones que coincidan con los filtros",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(
            onClick = {
                val fechaFiltro = runCatching { LocalDate.parse(busquedaFecha, DateTimeFormatter.ofPattern("dd/MM/yyyy")) }.getOrNull()

                transaccionesFiltradas = when(tipoSeleccionado){
                    "ingreso" -> uiState.ingresos
                    "gasto" -> uiState.gastos
                    else -> uiState.ingresos + uiState.gastos
                }.filter { transaccion ->
                    (busquedaCategoria.isEmpty() || transaccion.categoria.contains(busquedaCategoria, ignoreCase = true)) &&
                            (fechaFiltro == null || transaccion.fecha == fechaFiltro.format(DateTimeFormatter.ISO_DATE))
                }
            },
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = NaranjaClaro,
                contentColor = Blanco
            )
        ) {
            Text(
                text = "Filtrar",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }

    //mostrar resultados filtrados
    if (transaccionesFiltradas.isNotEmpty()) {
        LazyColumn {
            items(transaccionesFiltradas) { transaccion ->
                Text(text = "${transaccion.tipo}: ${transaccion.cantidad} - ${transaccion.categoria} (${transaccion.fecha})")
            }
        }
    } else {
        Text(
            text = "No hay transacciones que coincidan con los filtros",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

