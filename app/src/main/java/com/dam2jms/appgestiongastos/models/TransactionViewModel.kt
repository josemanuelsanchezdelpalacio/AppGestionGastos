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
     * Actualiza el estado del UI con la cantidad, descripción y tipo de transacción proporcionados.
     *
     * @param cantidad La cantidad a actualizar en el estado del UI.
     * @param descripcion La descripción a actualizar en el estado del UI.
     * @param tipo El tipo de transacción (por ejemplo, "ingreso" o "gasto") a actualizar en el estado del UI.
     */
    fun actualizarDatosTransaccion(cantidad: String, descripcion: String, tipo: String) {
        _uiState.update { it.copy(cantidad = cantidad, descripcion = descripcion, tipo = tipo) }
    }

    /**
     * Lee las transacciones desde Firestore, filtra por tipo y actualiza los ingresos y gastos en el estado del UI.
     *
     * Se espera que FireStoreUtil obtenga todas las transacciones y que estas sean filtradas
     * en función del tipo para actualizar el estado del UI.
     */
    fun leerTransacciones() {
        FireStoreUtil.obtenerTransacciones(
            onSuccess = { transacciones ->
                // Filtra las transacciones por tipo y actualiza el estado del UI
                val ingresos = transacciones.filter { it.tipo == "ingreso" }
                val gastos = transacciones.filter { it.tipo == "gasto" }
                actualizarTransaccion(ingresos, gastos)
            },
            onFailure = {}
        )
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

    @RequiresApi(Build.VERSION_CODES.O)
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
