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


    // Método para modificar la transacción usando Firestore
    fun modificarTransaccion(
        transaccionId: String,
        collection: String,
        context: Context,
        navController: NavController
    ) {
        val cantidadDouble = _uiState.value.cantidad.toDoubleOrNull() ?: 0.0

        // Validar la cantidad
        if (cantidadDouble == 0.0 && _uiState.value.cantidad.isNotBlank()) {
            Toast.makeText(context, "Error: La cantidad no es válida", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que los campos no estén vacíos
        if (_uiState.value.categoria.isBlank() || _uiState.value.tipo.isBlank()) {
            Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val transaccion = Transaccion(
                id = transaccionId,
                cantidad = cantidadDouble,
                categoria = _uiState.value.categoria,
                fecha = _uiState.value.fecha,
                tipo = _uiState.value.tipo
            )

            FireStoreUtil.modificarTransaccion(
                collection = collection,
                transaccionId = transaccionId,
                transaccion = transaccion,
                onSuccess = {
                    Toast.makeText(context, "Transacción modificada", Toast.LENGTH_SHORT).show()
                    navController.navigateUp()
                },
                onFailure = { e ->
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

