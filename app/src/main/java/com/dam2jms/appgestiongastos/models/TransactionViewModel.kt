package com.dam2jms.appgestiongastos.models

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
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
import com.dam2jms.appgestiongastos.auxiliar.BaseTransactionViewModel
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
class TransactionViewModel : BaseTransactionViewModel() {

    //inicializo el ViewModel y leo las transacciones al crear el ViewModel
    init {
        leerTransacciones()
    }

    /**metodo para eliminar una transacción
     * @param coleccion Nombre de la coleccion en Firestore de donde se eliminara la transaccion
     * @param transaccionId ID de la transaccion que se desea eliminar
     * @param context contexto necesario para los avisos dentro del Toast*/
    fun eliminarTransaccionExistente(coleccion: String, transaccionId: String, context: Context) {
        FireStoreUtil.eliminarTransaccion(
            coleccion, transaccionId,
            onSuccess = {
                Toast.makeText(context, "Transaccion eliminada correctamente", Toast.LENGTH_SHORT).show()
                leerTransacciones()
            },
            onFailure = {
                Toast.makeText(context, "Error al eliminar la transaccion", Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**metodo para mostrar un calendario horizontal de los ultimos 30 dias
     * @param fechaSeleccionada fecha actualmente seleccionada en el calendario
     * @param onDateSelected funcion lambda que se ejecuta al seleccionar una fecha pasando la fecha selecionada*/
    @Composable
    fun horizontalCalendar(fechaSeleccionada: LocalDate, onDateSelected: (LocalDate) -> Unit) {
        val fechas = remember {
            (0..30).map { LocalDate.now().minusDays(it.toLong()) }
        }

        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(fechas) { fecha ->
                val seleccionada = fecha == fechaSeleccionada
                val background = if (seleccionada) MaterialTheme.colorScheme.primary else Color.Transparent
                val textColor = if (seleccionada) Blanco else MaterialTheme.colorScheme.onBackground

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(background, shape = CircleShape)
                        .border(1.dp, if (seleccionada) Blanco else Gris, shape = CircleShape)
                        .clickable { onDateSelected(fecha) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fecha.dayOfMonth.toString(),
                        color = textColor,
                        fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
