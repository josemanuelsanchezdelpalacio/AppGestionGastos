package com.dam2jms.appgestiongastos.models

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class EditTransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    //metodo para actualizar los datos de la transacción en el UiState
    fun actualizarDatosTransaccion(cantidad: String? = null, descripcion: String? = null, tipo: String? = null, fecha: String? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                cantidad = cantidad ?: currentState.cantidad,
                descripcion = descripcion ?: currentState.descripcion,
                tipo = tipo ?: currentState.tipo,
                fecha = fecha ?: currentState.fecha
            )
        }
    }

    //metodo para modificar la transacción usando Firestore
    fun modificarTransaccion(transaccionId: String, collection: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val transaccion = Transaccion(
            id = transaccionId,
            cantidad = _uiState.value.cantidad.toDoubleOrNull() ?: 0.0,
            descripcion = _uiState.value.descripcion,
            fecha = _uiState.value.fecha,
            tipo = _uiState.value.tipo
        )

        FireStoreUtil.modificarTransaccion(
            collection = collection,
            transaccionId = transaccionId,
            transaccion = transaccion,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
