package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class EditTransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    //metodo para actualizar los datos de la transacción en el UiState
    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarDatosTransaccion(cantidad: String? = null, descripcion: String? = null, tipo: String? = null, fecha: String? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                cantidad = cantidad ?: currentState.cantidad,
                categoria = descripcion ?: currentState.categoria,
                tipo = tipo ?: currentState.tipo,
                fecha = validarFecha(fecha) ?: currentState.fecha
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validarFecha(fecha: String?): String? {
        return if(fecha.isNullOrBlank()){
            LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        }else{
            try{
                LocalDate.parse(fecha).format(DateTimeFormatter.ISO_DATE)
            }catch (e: DateTimeParseException){
                LocalDate.now().format(DateTimeFormatter.ISO_DATE)
            }
        }
    }


    fun modificarTransaccion(
        transaccionId: String,
        collection: String,
        context: Context,
        navController: NavController
    ) {
        try {
            // Validación de datos
            val cantidad = _uiState.value.cantidad.toDoubleOrNull()
                ?: throw IllegalArgumentException("La cantidad debe ser un número válido")

            if (cantidad <= 0) {
                throw IllegalArgumentException("La cantidad debe ser mayor que cero")
            }

            if (_uiState.value.categoria.isBlank()) {
                throw IllegalArgumentException("La categoría no puede estar vacía")
            }

            if (_uiState.value.fecha.isBlank()) {
                throw IllegalArgumentException("La fecha no puede estar vacía")
            }

            if (_uiState.value.tipo !in listOf("ingreso", "gasto")) {
                throw IllegalArgumentException("El tipo debe ser 'ingreso' o 'gasto'")
            }

            // Crear el objeto de transacción con los datos validados
            val transaccion = Transaccion(
                id = transaccionId,
                cantidad = cantidad,
                categoria = _uiState.value.categoria,
                fecha = _uiState.value.fecha,
                tipo = _uiState.value.tipo
            )

            // Determinar la colección correcta para Firestore (ingresos o gastos)
            val coleccionCorrecta = if (_uiState.value.tipo == "ingreso") "ingresos" else "gastos"

            FireStoreUtil.modificarTransaccion(
                collection = coleccionCorrecta,
                transaccionId = transaccionId,
                transaccion = transaccion,
                onSuccess = {
                    Log.d("EditTransactionViewModel", "Transacción modificada con éxito")
                    Toast.makeText(context, "Transacción modificada con éxito", Toast.LENGTH_SHORT).show()
                    navController.navigateUp() // Navegar hacia atrás después de modificar
                },
                onFailure = { e ->
                    Log.e("EditTransactionViewModel", "Error al modificar transacción", e)
                    Toast.makeText(context, "Error al modificar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        } catch (e: IllegalArgumentException) {
            Log.e("EditTransactionViewModel", "Datos de transacción inválidos", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("EditTransactionViewModel", "Error inesperado al modificar transacción", e)
            Toast.makeText(context, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

}

