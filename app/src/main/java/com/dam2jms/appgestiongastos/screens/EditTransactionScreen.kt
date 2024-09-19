package com.dam2jms.appgestiongastos.screens

import android.content.Context
import android.os.Build
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import kotlinx.coroutines.selects.select
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionScreen(navController: NavController, mvvm: EditTransactionViewModel){

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
                    title = { Text("Modificar transaccion", color = Blanco) },
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
                        IconButton(onClick = { navController.navigate(AppScreen.TransactionScreen.route) }) {
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
                            onClick = {
                                mvvm.modificarTransaccion(
                                    collection = if(uiState.tipo == "ingreso") "ingresos" else "gastos",
                                    context = context,
                                    navController = navController
                                )
                            },
                            containerColor = NaranjaClaro,
                            contentColor = Blanco,
                            elevation = FloatingActionButtonDefaults.elevation(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Guardar cambios",
                                tint = Blanco
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                )
            }
        ) { paddingValues ->
            EditTransactionScreenBody(paddingValues = paddingValues, mvvm = mvvm, uiState = uiState)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditTransactionScreenBody(paddingValues: PaddingValues, mvvm: EditTransactionViewModel, uiState: UiState) {
    val context = LocalContext.current

    var categorias by remember { mutableStateOf<List<Categoria>>(emptyList()) }

    LaunchedEffect(uiState.tipo) {
        if (uiState.tipo.isNotEmpty()) {
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
            onValueChange = { nuevaCantidad ->
                val cantidad = nuevaCantidad.toDoubleOrNull() ?: 0.0
                mvvm.actualizarCampo("cantidad", cantidad)
            },
            label = { Text(text = "Cantidad") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = "icono cantidad") }
        )

        OutlinedTextField(
            value = uiState.categoria,
            onValueChange = { nuevaCategoria ->
                mvvm.actualizarCampo("categoria", nuevaCategoria)
            },
            label = { Text(text = "Categoría") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Category, contentDescription = "icono categoria") },
            readOnly = true
        )

        OutlinedTextField(
            value = uiState.fecha,
            onValueChange = { },
            label = { Text(text = "Fecha") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Muestra el DatePickerDialog cuando se hace clic en el campo
                    val fechaInicial = if (uiState.fecha.isNotBlank()) {
                        try {
                            LocalDate.parse(uiState.fecha)
                        } catch (e: DateTimeParseException) {
                            LocalDate.now()
                        }
                    } else {
                        LocalDate.now()
                    }
                    DatePickerComponents.showDatePicker(context, fechaInicial) { nuevaFecha ->
                        mvvm.actualizarCampo("fecha", nuevaFecha.format(DateTimeFormatter.ISO_DATE))
                    }
                },
            readOnly = true,
            leadingIcon = {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "icono calendario",
                    modifier = Modifier.clickable {
                        // También mostrar el DatePickerDialog si se hace clic en el icono
                        val fechaInicial = if (uiState.fecha.isNotBlank()) {
                            try {
                                LocalDate.parse(uiState.fecha)
                            } catch (e: DateTimeParseException) {
                                LocalDate.now()
                            }
                        } else {
                            LocalDate.now()
                        }
                        DatePickerComponents.showDatePicker(context, fechaInicial) { nuevaFecha ->
                            mvvm.actualizarCampo("fecha", nuevaFecha.format(DateTimeFormatter.ISO_DATE))
                        }
                    }
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RadioButtonWithLabel(
                label = "Ingreso",
                selected = uiState.tipo == "ingreso",
                onClick = { mvvm.actualizarCampo("tipo", "ingreso") }
            )
            RadioButtonWithLabel(
                label = "Gasto",
                selected = uiState.tipo == "gasto",
                onClick = { mvvm.actualizarCampo("tipo", "gasto") }
            )
        }

        if (uiState.tipo.isNotEmpty()) {
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
                            mvvm.actualizarCampo("categoria", categoria.nombre)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun RadioButtonWithLabel(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = NaranjaClaro)
        )
        Text(text = label)
    }
}
