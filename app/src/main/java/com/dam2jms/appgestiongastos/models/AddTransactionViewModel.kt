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
import com.dam2jms.appgestiongastos.auxiliar.BaseTransactionViewModel
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.Blanco
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
class AddTransactionViewModel : BaseTransactionViewModel() {

    /**
     * Actualiza el estado del UI con la cantidad, descripción y tipo de transacción proporcionados.
     *
     * @param cantidad La cantidad a actualizar en el estado del UI.
     * @param descripcion La descripción a actualizar en el estado del UI.
     * @param tipo El tipo de transacción (por ejemplo, "ingreso" o "gasto") a actualizar en el estado del UI.
     */
    fun actualizarDatosTransaccion(cantidad: String?, categoria: String?, tipo: String) {
        _uiState.update { it.copy(
                cantidad = cantidad?.toDoubleOrNull() ?: uiState.value.cantidad,
                categoria = categoria ?: uiState.value.categoria,
                tipo = tipo
            )
        }

        if(tipo.isNotEmpty()){
            obtenerCategoriasPorTipo()
        }
    }

    /**
     * metodo que obtiene las categorias segun el tipo y actualiza el estado de la interfaz*
     * @param tipo el tipo de transaccion (ingreso/gasto)
     * */
    private fun obtenerCategoriasPorTipo(){
        _uiState.update {
            it.copy(categorias = _uiState.value.categorias)
        }
    }

    /**
     * Agrega una nueva transacción a Firestore y actualiza el estado del UI en caso de éxito o fracaso.
     *
     * @param transaccion La transacción a agregar.
     * @param context El contexto de la aplicación utilizado para mostrar el Toast.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarTransaccion(transaccion: Transaccion, context: Context) {
        //determina la coleccion en Firestore segun el tipo de transaccion
        val nombreColeccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos"
        //copio la transaccion y agrego la fecha actual
        val nuevaTransaccion = transaccion.copy(fecha = LocalDate.now().format(DateTimeFormatter.ISO_DATE))

        FireStoreUtil.añadirTransaccion(
            collection = nombreColeccion,
            transaccion = nuevaTransaccion,
            onSuccess = {
                Toast.makeText(context, "${transaccion.tipo.capitalize()} agregado con éxito", Toast.LENGTH_SHORT).show()
                leerTransacciones()
            },
            onFailure = { exception ->
                Toast.makeText(context, "Error al agregar el ${transaccion.tipo}: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
