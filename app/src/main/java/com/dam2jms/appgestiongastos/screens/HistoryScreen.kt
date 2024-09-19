package com.dam2jms.appgestiongastos.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.DatePickerComponents.showDatePicker
import com.dam2jms.appgestiongastos.components.ItemComponents.RadioButtonLabel
import com.dam2jms.appgestiongastos.components.ItemComponents.TransactionItem
import com.dam2jms.appgestiongastos.components.ItemComponents.categoriaItem
import com.dam2jms.appgestiongastos.components.ScreenComponents
import com.dam2jms.appgestiongastos.components.ScreenComponents.menu
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.data.CategoriaAPI.obtenerCategorias
import com.dam2jms.appgestiongastos.models.HistoryViewModel
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(navController: NavController, mvvm: HistoryViewModel){

    val uiState by mvvm.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { menu(navController = navController) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Historial de transacciones", color = Blanco) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "icono menu",
                                tint = Blanco
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(AppScreen.HomeScreen.route) }) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "atras",
                                tint = Blanco
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaOscuro)
                )
            }
        ) { paddingValues ->
            HistoryScreenBody(paddingValues = paddingValues, mvvm = mvvm, uiState = uiState, navController = navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreenBody(paddingValues: PaddingValues, uiState: UiState, navController: NavController, mvvm: HistoryViewModel){

    var buscarTipo by remember { mutableStateOf("fecha") }
    var tipo by remember { mutableStateOf("todos")}
    var buscarFecha by remember { mutableStateOf(LocalDate.now()) }
    var buscarCategoria by remember { mutableStateOf("") }
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(buscarTipo, tipo){
        if(buscarTipo == "categoria"){
            categorias = obtenerCategorias(tipo)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            RadioButtonLabel(value = "fecha", label = "Fecha", selectedValue = buscarTipo) { buscarTipo = it }
            RadioButtonLabel(value = "categoria", label = "Categoria", selectedValue = buscarTipo) { buscarTipo = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            RadioButtonLabel(value = "todos", label = "Todos", selectedValue = tipo) { tipo = it }
            RadioButtonLabel(value = "ingreso", label = "Ingresos", selectedValue = tipo) { tipo = it }
            RadioButtonLabel(value = "gasto", label = "Gastos", selectedValue = tipo) { tipo = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(buscarTipo == "fecha") {
            Button(onClick = {
                showDatePicker(context, buscarFecha) { buscarFecha = it }
            },
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaClaro)
            ){
                Text("Seleccionar fecha: ${buscarFecha.format(DateTimeFormatter.ISO_DATE)}")
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.4f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias){ categoria ->
                    categoriaItem(
                        categoria = categoria,
                        onClick = { buscarCategoria = categoria.nombre }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            mvvm.buscarTransacciones(buscarTipo, tipo, buscarFecha, buscarCategoria)
        },
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaClaro),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Buscar")
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (uiState.transaccionesFiltradas.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                items(uiState.transaccionesFiltradas) { transaccion ->
                    TransactionItem(
                        transaccion = transaccion,
                        navController = navController,
                        mvvm = TransactionViewModel(),
                        context = LocalContext.current
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay transacciones",
                    style = MaterialTheme.typography.bodyLarge,
                    color = NaranjaClaro,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

