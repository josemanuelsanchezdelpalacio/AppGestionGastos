package com.dam2jms.appgestiongastos.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.DatePickerComponents
import com.dam2jms.appgestiongastos.components.DatePickerComponents.showDatePicker
import com.dam2jms.appgestiongastos.components.ScreenComponents
import com.dam2jms.appgestiongastos.models.EditTransactionViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionScreen(navController: NavController, mvvm: EditTransactionViewModel, seleccionarFecha: String){

    val uiState by mvvm.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    //convierto seleccionarFecha a LocalDate para usarlo en el selector de fecha
    var fecha by remember {
        mutableStateOf(runCatching { LocalDate.parse(seleccionarFecha) }.getOrElse { LocalDate.now() })
    }
    val context = LocalContext.current
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { ScreenComponents.menu(navController = navController)}
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
                            DatePickerComponents.showDatePicker(context, fecha) { nuevaFecha ->
                                fecha = nuevaFecha
                            }
                        }) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "seleccionarFecha"
                            )
                        }
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
            },
            floatingActionButtonPosition = FabPosition.Center,
            bottomBar = {
                BottomAppBar(
                    containerColor = NaranjaOscuro
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    FloatingActionButton(
                        onClick = { navController.navigate(AppScreen.AddTransactionScreen.route) },
                        containerColor = NaranjaClaro,
                        contentColor = Blanco,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Añadir transacción",
                            tint = Blanco
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        ) { paddingValues ->
            EditTransactionBodyScreen(paddingValues = paddingValues, navController = navController, mvvm = mvvm, uiState = uiState, seleccionarFecha = fecha.toString())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionBodyScreen(paddingValues: PaddingValues, navController: NavController, mvvm: EditTransactionViewModel, uiState: UiState, seleccionarFecha: String){

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.cantidad,
            onValueChange = { mvvm.actualizarDatosTransaccion(cantidad = it)},
            label = { Text("Cantidad")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(Icons.Default.Money, contentDescription = null)}
        )

        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = { mvvm.actualizarDatosTransaccion(descripcion = it)},
            label = { Text("Descripcion")},
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = "icono descripcion")}
        )

        OutlinedTextField(
            value = uiState.fecha,
            onValueChange = {},
            label = { Text("Fecha")},
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val fechaActual =
                        runCatching { LocalDate.parse(seleccionarFecha) }.getOrElse { LocalDate.now() }
                    showDatePicker(context, fechaActual) { nuevaFecha ->
                        mvvm.actualizarDatosTransaccion(fecha = nuevaFecha.toString())
                    }
                },
            enabled = false,
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "icono calendario")}
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {

            RadioButton(
                selected = uiState.tipo == "ingreso",
                onClick = { mvvm.actualizarDatosTransaccion(tipo = "ingreso") },
                colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
            )
            Text(text = "Ingreso")
        }

        Row (verticalAlignment = Alignment.CenterVertically) {

            RadioButton(
                selected = uiState.tipo == "gasto",
                onClick = { mvvm.actualizarDatosTransaccion(tipo = "gasto") },
                colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
            )
            Text(text = "Gasto")
        }
    }

    Button(
        onClick = {
            mvvm.modificarTransaccion(
                transaccionId = uiState.id,
                collection = if(uiState.tipo == "ingreso") "ingresos" else "gastos",
                onSuccess = {
                    Toast.makeText(context, "Transaccion modificada", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                },
                onFailure = { e->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = NaranjaClaro)
    ) {
        Text(text = "Guardar cambios", color = Blanco)
    }
}

