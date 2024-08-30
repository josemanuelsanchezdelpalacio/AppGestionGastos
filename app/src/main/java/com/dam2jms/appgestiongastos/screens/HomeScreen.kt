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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import com.dam2jms.appgestiongastos.components.Components.menu
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
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, mvvm: HomeViewModel, currencyViewModel: CurrencyViewModel){

    val uiState by mvvm.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val monedasDisponibles by currencyViewModel.availableCurrencies.collectAsState()
    var seleccionMoneda by remember { mutableStateOf(uiState.monedaActual) }

    LaunchedEffect(seleccionMoneda) {
        mvvm.actualizarMoneda(seleccionMoneda)
    }

    //para el menu lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { menu(navController = navController) }
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
                HomeScreenBody(paddingValues = paddingValues, uiState = uiState, availableCurrencies = monedasDisponibles, selectedCurrency = seleccionMoneda, onCurrencySelected = { seleccionMoneda = it }, currencyViewModel = currencyViewModel)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenBody(paddingValues: PaddingValues, uiState: UiState, availableCurrencies: List<String>, selectedCurrency: String, onCurrencySelected: (String) -> Unit, currencyViewModel: CurrencyViewModel) {

    val conversionMoneda by currencyViewModel.conversionResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            item {
                Text(
                    "Selecciona una moneda para ver tus finanzas:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                var isExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCurrency,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        availableCurrencies.forEach { currency ->
                            DropdownMenuItem(
                                text = { Text(currency) },
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
                    moneda = selectedCurrency
                )
            }
            item {
                cajaAhorros(
                    ahorrosDiarios = uiState.ahorrosDiarios,
                    ahorrosMensuales = uiState.ahorrosMensuales,
                    moneda = selectedCurrency
                )
            }
            item {
                cajaFinanzasFijas(
                    ingresosDiarios = uiState.ingresosDiarios,
                    ingresosMensuales = uiState.ingresosMensuales,
                    gastosDiarios = uiState.gastosDiarios,
                    gastosMensuales = uiState.gastosMensuales,
                    moneda = selectedCurrency
                )
            }
    }
}



/**muestra un grafico circular con los gastos e ingresos */
@Composable
fun graficoCircularConInfo(gastos: Float, ingresos: Float, moneda: String){

    //calcula el total sumando gastos e ingresos
    val total = gastos + ingresos

    //calcular la proporcion de los gastos e ingresos comparado al total
    //si es mayor que 0 divide los gastos entre el total
    val radioGastos = if(total > 0) gastos/total else 0f
    val radioIngresos = if (total > 0) ingresos / total else 0f

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(Gris, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            //para graficos personalizados. Dibuja el grafico circular gris
            Canvas(modifier = Modifier.size(180.dp)) {

                //dibuja el grafico circular segun los porcentajes de ingresos y gastos
                //ingresos verde/ gastos rojo
                drawArc(
                    color = VerdeClaro,
                    startAngle = 360 * radioGastos -90f,
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
            Text(text = "Balance\nmensual", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.width(16.dp))

        //cajas con los gastos del mes y los ingresos del mes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = VerdeClaro, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Ingresos mes", color = Blanco, fontWeight = FontWeight.Bold)
                    Text(text = String.format("%.2f €", ingresos, moneda), color = Blanco, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = RojoClaro, shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Gastos mes", color = Blanco, fontWeight = FontWeight.Bold)
                    Text(text = String.format("%.2f €", gastos, moneda), color = Blanco, fontSize = 16.sp)
                }
            }
        }
    }
}


/** muestra informacion dentro de una caja sobre la cantidad ahorrada por el usuario en el ultimo mes y ultimo año*/
@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun cajaAhorros(ahorrosDiarios: Long, ahorrosMensuales: Long, moneda: String){

    //obtengo el mes actual a tiempo real
    val mesActual = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("es"))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NaranjaOscuro, shape = RoundedCornerShape(8.dp))
            .padding(20.dp)
    ){
        //cajas para el ahorro (porcentaje restante entre los gastos e ingresos) mensual y anual
        Column {
            Text(text = "AHORROS", color = Blanco, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)){
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ahorro diario", color = Blanco, fontWeight = FontWeight.Bold)
                    Text(text = String.format("%.2f €", ahorrosDiarios.toDouble(), moneda), color = Blanco)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Ahorro mensual (${mesActual.capitalize()})", color = Blanco, fontWeight = FontWeight.Bold)
                    Text(text = String.format("%.2f €", ahorrosMensuales.toDouble(), moneda), color = Blanco)
                }
            }

        }
    }
}


/**lo mismo que el anterior pero para mostrar ingresos/gastos anuales y mensuales*/
@Composable
fun cajaFinanzasFijas(ingresosDiarios: Long, ingresosMensuales: Long, gastosDiarios: Long, gastosMensuales: Long, moneda: String){

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NaranjaClaro, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {

        //cajas para los ingresos y gastos mensuales y anuales
        Column {
            Text(
                "INGRESOS Y GASTOS",
                color = Blanco,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ingresos diarios", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f €", ingresosDiarios.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ingresos mensuales", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f €", ingresosMensuales.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gastos diarios", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f €", gastosDiarios.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gastos mensuales", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(String.format("%.2f €", gastosMensuales.toDouble(), moneda), color = Blanco, fontSize = 16.sp)
                }
            }
        }
    }
}
