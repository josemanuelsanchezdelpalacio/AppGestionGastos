package com.dam2jms.appgestiongastos.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.ScreenComponents.menu
import com.dam2jms.appgestiongastos.models.CurrencyViewModel
import com.dam2jms.appgestiongastos.models.HomeViewModel
import com.dam2jms.appgestiongastos.models.LoginViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.Gris
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.dam2jms.appgestiongastos.ui.theme.RojoClaro
import com.dam2jms.appgestiongastos.ui.theme.VerdeClaro
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Currency
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, mvvm: HomeViewModel, currencyViewModel: CurrencyViewModel){

    val uiState by mvvm.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val monedasDisponibles by currencyViewModel.monedasDisponibles.collectAsState()
    var seleccionMoneda by remember { mutableStateOf(uiState.monedaActual)}

    LaunchedEffect(seleccionMoneda) {
        mvvm.actualizarMoneda(seleccionMoneda)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { menu(navController = navController) }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("GESTION DE GASTOS", color = Blanco, fontSize = 18.sp) },
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
                        IconButton(onClick = { navController.navigate(AppScreen.LoginScreen.route) }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "atras",
                                tint = Blanco
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaOscuro)
                )
            }
        ){ paddingValues ->
            HomeScreenBody(paddingValues = paddingValues, uiState = uiState, availableCurrencies = monedasDisponibles, selectedCurrency = seleccionMoneda, onCurrencySelected = { seleccionMoneda = it }, currencyViewModel = currencyViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenBody(paddingValues: PaddingValues, uiState: UiState, availableCurrencies: List<String>, selectedCurrency: String, onCurrencySelected: (String) -> Unit, currencyViewModel: CurrencyViewModel){

    val conversionResult by currencyViewModel.resultadoConversion.collectAsState()
    val currencySymbol = currencyViewModel.obtenerSimboloMoneda(selectedCurrency)

    LaunchedEffect(selectedCurrency) {
        currencyViewModel.convertirMonedas(
            mapOf(
                "ingresosMensuales" to uiState.ingresosMensuales.toDouble(),
                "gastosMensuales" to uiState.gastosMensuales.toDouble(),
                "ahorrosDiarios" to uiState.ahorrosDiarios.toDouble(),
                "ahorrosMensuales" to uiState.ahorrosMensuales.toDouble(),
                "ingresosDiarios" to uiState.ingresosDiarios.toDouble(),
                "gastosDiarios" to uiState.gastosDiarios.toDouble()
            ),
            monedaOrigen = uiState.monedaActual,
            monedaDestino = selectedCurrency
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Selecciona una moneda para el cambio",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            var isExpanded by remember { mutableStateOf(false)}
            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it}
            ) {
                OutlinedTextField(
                    value = currencyViewModel.obtenerNombreMoneda(selectedCurrency),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxSize()
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false}
                ) {
                    availableCurrencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currencyViewModel.obtenerNombreMoneda(currency), fontSize = 14.sp) },
                            onClick = {
                                onCurrencySelected(currency)
                                isExpanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            graficoCircularConInfo(
                gastos = uiState.gastosMensuales.toFloat(),
                ingresos = uiState.ingresosMensuales.toFloat(),
                moneda = currencySymbol
            )
        }

        item {
            cajaAhorros(
                ahorrosDiarios = uiState.ahorrosDiarios,
                ahorrosMensuales = uiState.ahorrosMensuales,
                moneda = currencySymbol
            )
        }

        item {
            cajaFinanzasFijas(
                ingresosDiarios = conversionResult["ingresosDiarios"]?.toLong() ?: 0L,
                ingresosMensuales = conversionResult["ingresosMensuales"]?.toLong() ?: 0L,
                gastosDiarios = conversionResult["gastosDiarios"]?.toLong() ?: 0L,
                gastosMensuales = conversionResult["gastosMensuales"]?.toLong() ?: 0L,
                moneda = currencySymbol
            )
        }
    }
}

@Composable
fun graficoCircularConInfo(gastos: Float, ingresos: Float, moneda: String) {
    val total = gastos + ingresos
    val radioGastos = if (total > 0) gastos / total else 0f
    val radioIngresos = if (total > 0) ingresos / total else 0f

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Gris, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(120.dp)) {
                drawArc(
                    color = VerdeClaro,
                    startAngle = 360 * radioGastos - 90f,
                    sweepAngle = 360 * radioIngresos,
                    useCenter = true
                )
                drawArc(
                    color = RojoClaro,
                    startAngle = -90f,
                    sweepAngle = 360 * radioGastos,
                    useCenter = true
                )
            }
            Text(
                text = "Balance\nmensual",
                color = Blanco,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = VerdeClaro, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Ingresos mes", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = String.format("%.2f %s", ingresos, moneda), color = Blanco, fontSize = 12.sp)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = RojoClaro, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Gastos mes", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = String.format("%.2f %s", gastos, moneda), color = Blanco, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun cajaAhorros(ahorrosDiarios: Long, ahorrosMensuales: Long, moneda: String){
    val mesActual = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("es"))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NaranjaOscuro, shape = RoundedCornerShape(8.dp))
            .padding(20.dp)
    ){
        Column {
            Text(text = "AHORROS", color = Blanco, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)){
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ahorro diario", color = Blanco, fontWeight = FontWeight.Bold)
                    Text(text = String.format("%.2f %s", ahorrosDiarios.toDouble(), moneda), color = Blanco)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ahorro mensual (${mesActual.capitalize()})", color = Blanco, fontWeight = FontWeight.Bold)
                    Text(text = String.format("%.2f %s", ahorrosMensuales.toDouble(), moneda), color = Blanco)
                }
            }
        }
    }
}

@Composable
fun cajaFinanzasFijas(ingresosDiarios: Long, ingresosMensuales: Long, gastosDiarios: Long, gastosMensuales: Long, moneda: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NaranjaClaro, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Text("INGRESOS Y GASTOS", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ingresos diarios", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f %s", ingresosDiarios.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ingresos mensuales", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f %s", ingresosMensuales.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gastos diarios", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f %s", gastosDiarios.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gastos mensuales", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f %s", gastosDiarios.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
            }
        }
    }
}


