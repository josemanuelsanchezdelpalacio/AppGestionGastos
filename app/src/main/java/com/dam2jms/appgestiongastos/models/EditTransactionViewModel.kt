package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.dam2jms.appgestiongastos.data.Categoria
import com.dam2jms.appgestiongastos.data.CategoriaAPI.obtenerCategorias
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.ui.theme.NaranjaClaro
import com.dam2jms.appgestiongastos.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class EditTransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Actualiza el estado del UI con la cantidad, categoría y tipo de transacción proporcionados.
     *
     * @param cantidad La cantidad a actualizar en el estado del UI.
     * @param categoria La categoría a actualizar en el estado del UI.
     * @param tipo El tipo de transacción (por ejemplo, "ingreso" o "gasto") a actualizar en el estado del UI.
     */
    fun actualizarDatosTransaccion(cantidad: String?, categoria: String?, tipo: String) {
        _uiState.update { it.copy(
            cantidad = cantidad?.toDoubleOrNull() ?: uiState.value.cantidad,
            categoria = categoria ?: uiState.value.categoria,
            tipo = tipo
        ) }
    }

    /**
     * Edita una transacción existente en Firestore y actualiza el estado del UI en caso de éxito o fracaso.
     *
     * @param transaccion La transacción a editar.
     * @param context El contexto de la aplicación utilizado para mostrar el Toast.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun editarTransaccion(transaccion: Transaccion, context: Context) {
        // Determina la colección en Firestore según el tipo de transacción
        val nombreColeccion = if (transaccion.tipo == "ingreso") "ingresos" else "gastos"

        FireStoreUtil.editarTransaccion(
            coleccion = nombreColeccion,
            transaccion = transaccion,
            onSuccess = {
                Toast.makeText(context, "${transaccion.tipo.capitalize()} editado con éxito", Toast.LENGTH_SHORT).show()
                leerTransacciones()  // Recarga las transacciones después de editar
            },
            onFailure = { exception ->
                Toast.makeText(context, "Error al editar el ${transaccion.tipo}: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**
     * Lee las transacciones desde Firestore, filtra por tipo y actualiza los ingresos y gastos en el estado del UI.
     */
    fun leerTransacciones() {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }
                _uiState.update { it.copy(ingresos = ingresos, gastos = gastos) }
            },
            onFailure = { /* Manejo de errores */ }
        )
    }

    /** UI Composable para mostrar una categoría en una lista */
    @Composable
    fun categoriaItem(categoria: Categoria, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = obtenerIconoCategoria(categoria.nombre),
                    contentDescription = categoria.nombre,
                    tint = NaranjaClaro,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = categoria.nombre, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    /** Método para obtener el icono de cada categoría */
    @Composable
    fun obtenerIconoCategoria(categoria: String): ImageVector {
        return when (categoria.toLowerCase()) {
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
}

