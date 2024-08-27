package com.dam2jms.appgestiongastos.models

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
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

class TransactionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Inicializo el ViewModel y leo las transacciones al crear el ViewModel
    init {
        leerTransacciones()
    }

    /**
     * Lee las transacciones desde Firestore, filtra por tipo y actualiza los ingresos y gastos en el estado del UI.
     */
    fun leerTransacciones() {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                // Filtra las transacciones por tipo y actualiza el estado del UI
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }
                actualizarTransaccion(ingresos, gastos)
            },
            onFailure = {
                // Manejo de errores si es necesario
            }
        )
    }

    /**
     * Actualiza el estado del UI con las listas de ingresos y gastos proporcionadas.
     */
    fun actualizarTransaccion(ingresos: List<Transaccion>, gastos: List<Transaccion>) {
        _uiState.update { it.copy(ingresos = ingresos, gastos = gastos) }
    }

    /**
     * Actualiza el estado del UI con la cantidad, descripción y tipo de transacción proporcionados.
     */
    fun actualizarDatosTransaccion(cantidad: String, descripcion: String, tipo: String) {
        _uiState.update { it.copy(cantidad = cantidad, descripcion = descripcion, tipo = tipo) }
    }

    /**
     * Agrega una nueva transacción a Firestore y actualiza el estado del UI en caso de éxito o fracaso.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun agregarTransaccion(transaccion: Transaccion, context: Context) {
        if (transaccion.tipo.isEmpty()) {
            Toast.makeText(context, "Debe seleccionar ingreso o gasto", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            FireStoreUtil.agregarTransaccion(
                transaccion = transaccion,
                onSuccess = {
                    Toast.makeText(context, "${transaccion.tipo.capitalize()} añadida", Toast.LENGTH_SHORT).show()
                    actualizarTransacciones(transaccion.tipo)
                },
                onFailure = { exception ->
                    Toast.makeText(context, "Ocurrio un error a la hora de crear la transaccion", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun actualizarTransacciones(tipo: String) {
        val coleccion = if (tipo == "ingreso") "ingresos" else "gastos"

        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                _uiState.update {
                    if (coleccion == "ingresos") {
                        it.copy(ingresos = transacciones)
                    } else {
                        it.copy(gastos = transacciones)
                    }
                }
            },
            onFailure = { exception ->
                // Manejo de errores si es necesario
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePicker(context: Context, fechaActual: LocalDate, fechaSeleccionada: (LocalDate) -> Unit) {
        val año = fechaActual.year
        val mes = fechaActual.monthValue - 1
        val dia = fechaActual.dayOfMonth

        DatePickerDialog(context, { _, selecAño, selecMes, selecDia ->
            val nuevaFecha = LocalDate.of(selecAño, selecMes + 1, selecDia)
            fechaSeleccionada(nuevaFecha)
        }, año, mes, dia).show()
    }

    /** Metodo para validar la cantidad para la transaccion **/
    fun validarCantidad(context: Context, amount: String): Boolean {
        return if (amount.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDouble() > 0) {
            true
        } else {
            Toast.makeText(context, "Ingrese una cantidad valida", Toast.LENGTH_SHORT).show()
            false
        }
    }


    /** Metodo para validar la descripcion de la transaccion **/
    fun validarDescripcion(context: Context, description: String): Boolean {
        return if (description.isNotEmpty()) {
            true
        } else {
            Toast.makeText(context, "Ingrese una descripcion", Toast.LENGTH_SHORT).show()
            false
        }
    }
}