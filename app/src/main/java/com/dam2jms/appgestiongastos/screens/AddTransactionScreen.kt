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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.components.Components.menu
import com.dam2jms.appgestiongastos.models.CategoryViewModel
import com.dam2jms.appgestiongastos.models.TransactionViewModel
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.ui.theme.NaranjaOscuro
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(navController: NavController, mvvm: TransactionViewModel, categoryViewModel: CategoryViewModel) {

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
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NaranjaOscuro)
                )
            }
        ) { paddingValues ->
            AddTransactionScreenBody(
                paddingValues = paddingValues,
                uiState = uiState,
                navController = navController,
                mvvm = mvvm,
                context = context,
                categoryViewModel = categoryViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTransactionScreenBody(
    paddingValues: PaddingValues,
    uiState: UiState,
    navController: NavController,
    mvvm: TransactionViewModel,
    context: Context,
    categoryViewModel: CategoryViewModel
) {

    val categorias by categoryViewModel.categories.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // Use LazyColumn as the only scrollable component
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = uiState.cantidad,
                onValueChange = { newValue -> mvvm.actualizarDatosTransaccion(newValue, uiState.descripcion, uiState.tipo) },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(imageVector = Icons.Filled.Add, contentDescription = "Cantidad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = uiState.tipo == "ingreso",
                    onClick = {
                        mvvm.actualizarDatosTransaccion(uiState.cantidad, uiState.descripcion, "ingreso")
                        categoryViewModel.obtenerCategorias("ingreso")
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
                )
                Text(
                    text = "Ingreso",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NaranjaClaro
                )
                Spacer(modifier = Modifier.width(32.dp))
                RadioButton(
                    selected = uiState.tipo == "gasto",
                    onClick = {
                        mvvm.actualizarDatosTransaccion(uiState.cantidad, uiState.descripcion, "gasto")
                        categoryViewModel.obtenerCategorias("gasto")
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
                )
                Text(
                    text = "Gasto",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = NaranjaClaro
                )
            }
        }

        items(categorias) { categoria ->
            CategoriaItem(
                categoria = categoria,
                onClick = {
                    mvvm.actualizarDatosTransaccion(uiState.cantidad, categoria.nombre, uiState.tipo)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val cantidadValida = mvvm.validarCantidad(context, uiState.cantidad)
                    val descripcionValida = mvvm.validarDescripcion(context, uiState.descripcion)
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
}

@Composable
fun CategoriaItem(categoria: Categoria, onClick: () -> Unit){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                imageVector = getCategoryIcon(categoria.nombre),
                contentDescription = categoria.nombre,
                tint = NaranjaClaro,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = categoria.nombre, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun getCategoryIcon(categoria: String): ImageVector{

    return when (categoria.toLowerCase()){
        "salario" -> Icons.Default.Money
        "casa" -> Icons.Default.Home
        "ropa" -> Icons.Default.ShoppingBag
        "educacion" -> Icons.Default.School
        "entretenimiento" -> Icons.Default.Movie
        "regalo" -> Icons.Default.CardGiftcard
        "mascota" -> Icons.Default.Pets
        "viajes" -> Icons.Default.Flight
        else -> Icons.Default.Category
    }

}