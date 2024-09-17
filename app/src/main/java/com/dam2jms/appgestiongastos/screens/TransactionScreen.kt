package com.dam2jms.appgestiongastos.screens

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.DatePickerComponents.showDatePicker
import com.dam2jms.appgestiongastos.components.ItemComponents.TransactionItem
import com.dam2jms.appgestiongastos.components.ScreenComponents.menu
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.dam2jms.appgestiongastos.ui.theme.RojoClaro
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(navController: NavController, mvvm: TransactionViewModel, seleccionarFecha: String) {

    val uiState by mvvm.uiState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var displayType by remember { mutableStateOf("ingresos") }
    var fecha by remember { mutableStateOf(LocalDate.parse(seleccionarFecha)) }

    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { menu(navController = navController) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TRANSACCIONES", color = Blanco) },
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
                        IconButton(onClick = {
                            showDatePicker(context, fecha) { nuevaFecha ->
                                fecha = nuevaFecha
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "seleccion fecha",
                                tint = Blanco
                            )
                        }
                        IconButton(onClick = { navController.navigate(AppScreen.HomeScreen.route) }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "atras",
                                tint = Blanco
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaOscuro)
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            bottomBar = {
                BottomAppBar(
                    containerColor = NaranjaOscuro,
                    content = {
                        Spacer(modifier = Modifier.weight(1f))
                        FloatingActionButton(
                            onClick = { navController.navigate(AppScreen.AddTransactionScreen.route) },
                            containerColor = NaranjaClaro,
                            contentColor = Blanco,
                            elevation = FloatingActionButtonDefaults.elevation(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Añadir transaccion",
                                tint = Blanco
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                )
            }
        ) { paddingValues ->
            TransactionsScreenBody(
                paddingValues = paddingValues,
                navController = navController,
                mvvm = mvvm,
                uiState = uiState,
                seleccionarFecha = fecha.toString(),
                displayType = displayType,
                onDisplayTypeChange = { displayType = it })
        }
    }
}


@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun TransactionsScreenBody(
    paddingValues: PaddingValues,
    navController: NavController,
    mvvm: TransactionViewModel,
    uiState: UiState,
    seleccionarFecha: String,
    displayType: String,
    onDisplayTypeChange: (String) -> Unit
) {

    val fechaSeleccionada = LocalDate.parse(seleccionarFecha)

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ultimos 30 dias",
            style = MaterialTheme.typography.titleMedium,
            color = NaranjaOscuro,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            color = NaranjaOscuro,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )

        mvvm.horizontalCalendar(
            fechaSeleccionada = fechaSeleccionada,
            onDateSelected = { fecha ->
                val nuevaFechaSelec = fecha.format(DateTimeFormatter.ISO_DATE)
                navController.navigate(AppScreen.TransactionScreen.createRoute(nuevaFechaSelec))
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = displayType == "ingresos",
                onClick = { onDisplayTypeChange("ingresos") },
                colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
            )

            Text(text = "Ingresos", modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.height(16.dp))

            RadioButton(
                selected = displayType == "gastos",
                onClick = { onDisplayTypeChange("gastos") },
                colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
            )

            Text(text = "Gastos", modifier = Modifier.align(Alignment.CenterVertically))
        }

        val filtroTransacciones = if (displayType == "ingresos") {
            uiState.ingresos.filter { it.fecha == seleccionarFecha }
        } else {
            uiState.gastos.filter { it.fecha == seleccionarFecha }
        }

        if (filtroTransacciones.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                items(filtroTransacciones) { transaccion ->
                    TransactionItem(
                        transaccion = transaccion,
                        navController = navController,
                        mvvm = mvvm,
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