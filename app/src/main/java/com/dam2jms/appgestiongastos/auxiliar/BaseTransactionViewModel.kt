package com.dam2jms.appgestiongastos.auxiliar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.dam2jms.appgestiongastos.states.Transaccion
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@RequiresApi(Build.VERSION_CODES.O)
open class BaseTransactionViewModel : ViewModel() {

    protected val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

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
}