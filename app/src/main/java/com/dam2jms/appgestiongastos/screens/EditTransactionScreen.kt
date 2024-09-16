package com.dam2jms.appgestiongastos.screens

import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.DatePickerComponents
import com.dam2jms.appgestiongastos.components.DatePickerComponents.showDatePicker
import com.dam2jms.appgestiongastos.components.ItemComponents.categoriaItem
import com.dam2jms.appgestiongastos.components.ScreenComponents
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.components.ScreenComponents.menu
import com.dam2jms.appgestiongastos.data.CategoriaAPI.obtenerCategorias
import com.dam2jms.appgestiongastos.models.AddTransactionViewModel
import com.dam2jms.appgestiongastos.models.EditTransactionViewModel
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.navigation.AppScreen
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import com.dam2jms.appgestiongastos.utils.Validaciones.validarCantidad
import com.dam2jms.appgestiongastos.utils.Validaciones.validarDescripcion
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionScreen(
    navController: NavController,
    mvvm: EditTransactionViewModel,
    seleccionarFecha: String
) {
    val uiState by mvvm.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Convierte seleccionarFecha a LocalDate para usarlo en el selector de fecha
    var fecha by remember { mutableStateOf(runCatching { LocalDate.parse(seleccionarFecha) }.getOrElse { LocalDate.now() }) }

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
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        mvvm.modificarTransaccion(
                            transaccionId = uiState.id,
                            collection = if (uiState.tipo == "ingreso") "ingresos" else "gastos",
                            context = context,
                            navController = navController
                        )
                    },
                    containerColor = NaranjaClaro,
                    contentColor = Blanco,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Guardar cambios",
                        tint = Blanco
                    )
                }
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = NaranjaOscuro
                ) {
                    Text(
                        text = "Modifica los detalles y guarda los cambios",
                        modifier = Modifier.padding(16.dp),
                        color = Blanco
                    )
                }
            }
        ) { paddingValues ->
            EditTransactionBodyScreen(paddingValues = paddingValues, mvvm = mvvm, uiState = uiState)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionBodyScreen(paddingValues: PaddingValues, mvvm: EditTransactionViewModel, uiState: UiState) {

    val context = LocalContext.current
    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    LaunchedEffect(uiState.tipo) {
        if(uiState.tipo.isNotEmpty()){
            categorias = obtenerCategorias(uiState.tipo)
        }
    }

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
            onValueChange = { nuevaCantidad -> mvvm.actualizarDatosTransaccion(cantidad = nuevaCantidad) },
            label = { Text(text = "Cantidad")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = "Icono categoria") }
        )

        OutlinedTextField(
            value = uiState.categoria,
            onValueChange = { mvvm.actualizarDatosTransaccion(uiState.cantidad, it, uiState.tipo) },
            label = { Text(text = "Categoria")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            leadingIcon = { Icon(Icons.Default.Category, contentDescription = "Icono categoria") }
        )

        OutlinedTextField(
            value = uiState.fecha,
            onValueChange = {},
            label = { Text("Fecha") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val initialDate = if (uiState.fecha.isNotBlank()) {
                        try {
                            LocalDate.parse(uiState.fecha)
                        } catch (e: DateTimeParseException) {
                            LocalDate.now()
                        }
                    } else {
                        LocalDate.now()
                    }
                    showDatePicker(context, initialDate) { nuevaFecha ->
                        mvvm.actualizarDatosTransaccion(fecha = nuevaFecha.format(DateTimeFormatter.ISO_DATE))
                    }
                },
            enabled = false,
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Icono calendario") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.tipo == "ingreso",
                    onClick = { mvvm.actualizarDatosTransaccion(tipo = "ingreso") },
                    colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
                )
                Text(text = "Ingreso")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.tipo == "gasto",
                    onClick = { mvvm.actualizarDatosTransaccion(tipo = "gasto") },
                    colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
                )
                Text(text = "Gasto")
            }
        }

        if(uiState.tipo.isNotEmpty()){
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    categoriaItem(
                        categoria = categoria,
                        onClick = {
                            mvvm.actualizarDatosTransaccion(uiState.cantidad, categoria.nombre, uiState.tipo)
                        }
                    )
                }
            }
        }
    }
}


