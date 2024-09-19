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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditTransactionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Cargar los datos de la transacción que se quiere modificar
    fun cargarTransaccion(transaccion: Transaccion) {
        _uiState.update { currentState ->
            currentState.copy(
                id = transaccion.id,
                fecha = transaccion.fecha,
                cantidad = transaccion.cantidad,
                categoria = transaccion.categoria,
                tipo = transaccion.tipo
            )
        }
    }

    // Actualizar los campos de la transacción a medida que se editan
    fun actualizarCampo(campo: String, valor: Any) {
        _uiState.update { currentState ->
            when (campo) {
                "fecha" -> currentState.copy(fecha = valor as String)
                "cantidad" -> currentState.copy(cantidad = valor as Double)
                "categoria" -> currentState.copy(categoria = valor as String)
                "tipo" -> currentState.copy(tipo = valor as String)
                else -> currentState
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun modificarTransaccion(collection: String, context: Context, navController: NavController) {
        viewModelScope.launch {
            try {
                // Crear objeto Transaccion actualizado
                val transaccionActualizada = Transaccion(
                    id = _uiState.value.id,
                    fecha = _uiState.value.fecha,
                    cantidad = _uiState.value.cantidad,
                    categoria = _uiState.value.categoria,
                    tipo = _uiState.value.tipo
                )

                // Llamar a FireStoreUtil para actualizar la transacción
                FireStoreUtil.actualizarTransaccion(
                    collection = collection,
                    transaccion = transaccionActualizada,
                    onSuccess = {
                        Toast.makeText(context, "Transacción actualizada correctamente", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // Volver a la pantalla anterior
                    },
                    onFailure = { e ->
                        Toast.makeText(context, "Error al actualizar la transacción: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                )
            } catch (e: Exception) {
                Log.e("EditTransactionViewModel", "Error al modificar la transacción: ${e.message}")
                Toast.makeText(context, "Error al modificar la transacción", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
