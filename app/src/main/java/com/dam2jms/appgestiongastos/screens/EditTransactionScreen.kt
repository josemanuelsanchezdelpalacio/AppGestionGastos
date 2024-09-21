package com.dam2jms.appgestiongastos.screens

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.components.ItemComponents.categoriaItem
import com.dam2jms.appgestiongastos.components.ScreenComponents.menu
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.data.CategoriaAPI.obtenerCategorias
import com.dam2jms.appgestiongastos.models.EditTransactionViewModel
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.*
import com.dam2jms.appgestiongastos.utils.Validaciones.validarCantidad
import com.dam2jms.appgestiongastos.utils.Validaciones.validarDescripcion
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionScreen(navController: NavController, mvvm: EditTransactionViewModel, transactionId: String) {

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
                    title = { Text(text = "Editar Transacción", color = Blanco) },
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
                                tint = Blanco
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NaranjaOscuro)
                )
            }
        ) { paddingValues ->
            EditTransactionScreenBody(paddingValues = paddingValues, mvvm = mvvm, uiState = uiState, context = context, navController = navController, transactionId = transactionId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionScreenBody(
    paddingValues: PaddingValues,
    mvvm: EditTransactionViewModel,
    uiState: UiState,
    context: Context,
    navController: NavController,
    transactionId: String // <-- Añadimos el transactionId aquí
) {

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
            value = uiState.cantidad.toString(),
            onValueChange = { nuevaCantidad -> mvvm.actualizarDatosTransaccion(nuevaCantidad, uiState.categoria, uiState.tipo) },
            label = { Text("Cantidad") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(imageVector = Icons.Filled.AttachMoney, contentDescription = "Cantidad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = uiState.categoria,
            onValueChange = { nuevaCategoria -> mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), nuevaCategoria, uiState.tipo) },
            label = { Text("Categoria") },
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Filled.Category, contentDescription = "Descripcion") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.tipo == "ingreso",
                    onClick = { mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), uiState.categoria, tipo = "ingreso") }
                )
                Text(
                    text = "Ingreso",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.tipo == "gasto",
                    onClick = { mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), uiState.categoria, tipo = "gasto") }
                )
                Text(
                    text = "Gasto",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if(uiState.tipo.isNotEmpty()){
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    categoriaItem(
                        categoria = categoria,
                        onClick = {
                            mvvm.actualizarDatosTransaccion(uiState.cantidad.toString(), categoria.nombre, uiState.tipo)
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val cantidadValida = validarCantidad(context, uiState.cantidad.toString())
                val categoriaValida = validarDescripcion(context, uiState.categoria)
                val tipoSeleccionado = uiState.tipo.isNotEmpty()

                if (!tipoSeleccionado) {
                    Toast.makeText(context, "Debe seleccionar Ingresos o Gastos", Toast.LENGTH_SHORT).show()
                }

                if (cantidadValida && categoriaValida) {
                    // Utiliza el transactionId en lugar de un ID vacío
                    val transaction = Transaccion(
                        id = transactionId, // <-- Aquí usamos el ID de la transacción
                        cantidad = uiState.cantidad,
                        categoria = uiState.categoria,
                        fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                        tipo = uiState.tipo
                    )
                    mvvm.editarTransaccion(transaction, context)
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
