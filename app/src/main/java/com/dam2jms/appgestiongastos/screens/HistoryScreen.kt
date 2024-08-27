package com.dam2jms.appgestiongastos.screens

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.Components.menu
import com.dam2jms.appgestiongastos.models.HomeViewModel
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
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
                        }
                        ) {
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

    val uiState by mvvm.uiState.collectAsState()
    val (busquedaCategoria, setBusquedaCategoria) = remember { mutableStateOf("") }
    val (busquedaFecha, setBusquedaFecha) = remember { mutableStateOf<LocalDate?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = busquedaCategoria,
            onValueChange = { setBusquedaCategoria(it) },
            label = { Text("Buscar por categoria") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = busquedaFecha?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))?:"",
            onValueChange = {
                val fecha = runCatching {
                    LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }.getOrNull()
                setBusquedaFecha(fecha)
            },
            label = { Text("Buscar por fecha (dd/MM/yyyy)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        //para filtrar transacciones por categoria o fecha
        val filtroTransacciones = uiState.ingresos.plus(uiState.gastos).filter { transaccion ->
            transaccion.descripcion.contains(busquedaCategoria, ignoreCase = true) &&
                    (busquedaFecha == null || transaccion.fecha == busquedaFecha.format(DateTimeFormatter.ISO_DATE))
        }

        //mostrar transacciones filtradas o mensaje si no hay resultados
        if(busquedaCategoria.isNotEmpty() || busquedaFecha != null){
            LazyColumn {
                items(filtroTransacciones){ transaccion ->
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