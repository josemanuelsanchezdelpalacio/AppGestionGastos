package com.dam2jms.appgestiongastos.screens

import android.app.DatePickerDialog
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.Components
import com.dam2jms.appgestiongastos.components.Components.AuthRadioButton
import com.dam2jms.appgestiongastos.components.Components.AuthTextField
import com.dam2jms.appgestiongastos.components.Components.menu
import com.dam2jms.appgestiongastos.models.HomeViewModel
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.google.type.Color
import com.google.type.DateTime
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, mvvm: TransactionViewModel){

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
                    title = { Text("GESTION DE GASTOS", color = Blanco) },
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
                HistoryScreenBody(paddingValues = paddingValues, mvvm = mvvm, uiState = uiState)
            }
        )
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreenBody(paddingValues: PaddingValues, mvvm: TransactionViewModel, uiState: UiState){

    var busquedaCategoria by remember { mutableStateOf("") }
    var busquedaFecha by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }
    var transaccionesFiltradas by remember { mutableStateOf(uiState.ingresos + uiState.gastos) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            AuthRadioButton(
                seleccion = tipoSeleccionado == "ingreso",
                onClick = { tipoSeleccionado = "ingreso" },
                label = "Ingresos"
            )

            AuthRadioButton(
                seleccion = tipoSeleccionado == "gasto",
                onClick = { tipoSeleccionado = "gasto" },
                label = "Gastos"
            )

            AuthRadioButton(
                seleccion = tipoSeleccionado == null,
                onClick = { tipoSeleccionado = null },
                label = "Todos"
            )
        }

        OutlinedTextField(
            value = busquedaCategoria,
            onValueChange = { busquedaCategoria = it },
            label = { Text("Buscar por categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = busquedaFecha,
            onValueChange = { busquedaFecha = it },
            label = { Text("Buscar por fecha (dd/MM/yyyy)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                val fechaFiltro = runCatching {
                    LocalDate.parse(busquedaFecha, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }.getOrNull()

                transaccionesFiltradas = when(tipoSeleccionado){
                    "ingreso" -> uiState.ingresos
                    "gasto" -> uiState.gastos
                    else -> uiState.ingresos + uiState.gastos
                }.filter { transaccion ->
                    (busquedaCategoria.isEmpty() || transaccion.descripcion.contains(busquedaCategoria, ignoreCase = true)) &&
                            (fechaFiltro == null || transaccion.fecha == fechaFiltro.format(DateTimeFormatter.ISO_DATE))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Filtrar")
        }

        if(transaccionesFiltradas.isNotEmpty()){
            LazyColumn {
                items(transaccionesFiltradas){ transaccion ->
                    TransactionItem(transaccion = transaccion)
                }
            }
        }else{
            Text(
                text = "No hay transacciones que coincidan con los filtros",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
