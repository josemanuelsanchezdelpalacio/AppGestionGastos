package com.dam2jms.appgestiongastos.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.ScreenComponents.AuthRadioButton
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.components.ScreenComponents.menu
import com.dam2jms.appgestiongastos.data.CategoriaAPI.obtenerCategorias
import com.dam2jms.appgestiongastos.models.AddTransactionViewModel
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, mvvm: AddTransactionViewModel) {

    val uiState by mvvm.uiState.collectAsState()

    val context = LocalContext.current

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            menu(navController = navController)
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text = "Añadir Transacción", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NaranjaOscuro)
                )
            }
        ) { paddingValues ->
            AddTransactionScreenBody(paddingValues = paddingValues, uiState = uiState, navController = navController, mvvm = mvvm, context = context)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTransactionScreenBody(paddingValues: PaddingValues, uiState: UiState, navController: NavController, mvvm: AddTransactionViewModel, context: Context) {

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.cantidad,
            onValueChange = { newValue -> mvvm.actualizarDatosTransaccion(newValue, uiState.descripcion, uiState.tipo) },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = { newValue -> mvvm.actualizarDatosTransaccion(uiState.cantidad, newValue, uiState.tipo) },
            label = { Text("Descripcion") },
            leadingIcon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Descripcion") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AuthRadioButton(
                seleccion = uiState.tipo == "ingreso",
                onClick = {
                    mvvm.actualizarDatosTransaccion(uiState.cantidad, uiState.descripcion, "ingreso")
                },
                label = "Ingreso"
            )

            AuthRadioButton(
                seleccion = uiState.tipo == "gasto",
                onClick = {
                    mvvm.actualizarDatosTransaccion(uiState.cantidad, uiState.descripcion, "gasto")
                },
                label = "Gasto"
            )
        }

        if(uiState.tipo.isNotEmpty()){
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    mvvm.categoriaItem(
                        categoria = categoria,
                        onClick = {
                            mvvm.actualizarDatosTransaccion(uiState.cantidad, categoria.nombre, uiState.tipo)
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val cantidadValida = validarCantidad(context, uiState.cantidad)
                val descripcionValida = validarDescripcion(context, uiState.descripcion)
                val tipoSeleccionado = uiState.tipo.isNotEmpty()

                if (!tipoSeleccionado) {
                    Toast.makeText(context, "Debe seleccionar Ingresos o Gastos", Toast.LENGTH_SHORT).show()
                }

                if (cantidadValida && descripcionValida) {
                    val transaction = Transaccion(
                        id = "",
                        cantidad = uiState.cantidad.toDoubleOrNull() ?: 0.0,
                        descripcion = uiState.descripcion,
                        fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                        tipo = uiState.tipo
                    )
                    mvvm.agregarTransaccion(transaction, context)
                    navController.popBackStack()
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
                text = "Añadir Transacción",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}