package com.dam2jms.appgestiongastos.models

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
import com.dam2jms.appgestiongastos.ui.theme.Gris
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.utils.FireStoreUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class TransactionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    //inicializo el ViewModel y lee las transacciones al crear el ViewModel
    init {
        leerTransacciones()
    }

    /**
     * Actualiza el estado del UI con las listas de ingresos y gastos proporcionadas.
     *
     * @param ingresos La lista de transacciones de tipo "ingreso" a actualizar.
     * @param gastos La lista de transacciones de tipo "gasto" a actualizar.
     */
    fun actualizarTransaccion(ingresos: List<Transaccion>, gastos: List<Transaccion>) {
        _uiState.update { it.copy(ingresos = ingresos, gastos = gastos) }
    }

    /**
     * Lee las transacciones desde Firestore, filtra por tipo y actualiza los ingresos y gastos en el estado del UI.
     *
     * Se espera que FireStoreUtil obtenga todas las transacciones y que estas sean filtradas
     * en función del tipo para actualizar el estado del UI.
     *
     * filtro las transacciones por tipo y actualiza el estado del UI
     * @param ingresos
     * @param gastos
     */
    fun leerTransacciones() {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }
                actualizarTransaccion(ingresos, gastos)
            },
            onFailure = {}
        )
    }

    /**metodo para el icono del calendario de la clase TransactionScreen*/
    fun showDatePicker(context: Context, fechaActual: LocalDate, fechaSeleccionada: (LocalDate) -> Unit) {
        val año = fechaActual.year
        val mes = fechaActual.monthValue - 1
        val dia = fechaActual.dayOfMonth

        DatePickerDialog(context, { _, selecAño, selecMes, selecDia ->
            val nuevaFecha = LocalDate.of(selecAño, selecMes + 1, selecDia)
            fechaSeleccionada(nuevaFecha)

            _uiState.value = _uiState.value.copy(fecha = nuevaFecha.toString())
        }, año, mes, dia).show()
    }

    /**metodo para el calendario horizontal con los ultimos 30 dias de la clase TransactionScreen*/
    @Composable
    fun horizontalCalendar(fechaSeleccionada: LocalDate, onDateSelected: (LocalDate) -> Unit) {

        val fechas = remember{
            (0..30).map { LocalDate.now().minusDays(it.toLong()) }
        }

        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fechas) { fecha ->
                val seleccionada = fecha == fechaSeleccionada
                val background = if(seleccionada) MaterialTheme.colorScheme.primary else Color.Transparent
                val textColor = if(seleccionada) Blanco else MaterialTheme.colorScheme.onBackground

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(background, shape = CircleShape)
                        .border(1.dp, if (seleccionada) Blanco else Gris, shape = CircleShape)
                        .clickable { onDateSelected(fecha) },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = fecha.dayOfMonth.toString(),
                        color = textColor,
                        fontWeight = if(seleccionada) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

