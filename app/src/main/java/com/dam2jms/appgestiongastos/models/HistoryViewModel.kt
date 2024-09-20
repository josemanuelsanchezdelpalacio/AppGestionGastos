package com.dam2jms.appgestiongastos.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.appgestiongastos.states.UiState
import com.dam2jms.appgestiongastos.utils.FireStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate


class HistoryViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    /**inicializa el viewmodel cargando las transacciones **/
    init {
        cargarTransacciones()
    }

    /**
     * metodo para obtener las transacciones de firestore y actualizo el estado de la interfaz
     * Filtra las transacciones en ingresos y gastos
     * @return actualizo la interfaz con las transacciones filtradas**/
    private fun cargarTransacciones(){

        viewModelScope.launch {
            FireStoreUtil.obtenerTransacciones(
                onSuccess = { transacciones ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            ingresos = transacciones.filter { it.tipo == "ingreso" },
                            gastos = transacciones.filter { it.tipo == "gasto" },
                            transaccionesFiltradas = transacciones
                        )
                    }
                },
                onFailure = {}
            )
        }
    }

    /**
     * metodo para filtrar las transacciones segun los criterios proporcionados, como tipo, fecha y categoria
     * @param buscarTipo Tipo de filtro (ya sea "fecha" o "categoria")
     * @param tipo Tipo de transaccion ("ingreso o "gasto")
     * @param buscarFecha Fecha para filtrar las transacciones
     * @param buscarCategoria categoria para filtrar las transacciones**/
    fun buscarTransacciones(buscarTipo: String, tipo: String, buscarFecha: LocalDate, buscarCategoria: String){

        //combino ingresos y gastos en una sola lista
        val transacciones = _uiState.value.ingresos + _uiState.value.gastos

        //filtro las transacciones segun el tipo seleccionado ("ingreso" o "gasto")
        val filtrarPorTipo = when(tipo) {
            "ingreso" -> transacciones.filter { it.tipo == "ingreso" }
            "gasto" -> transacciones.filter { it.tipo == "gasto" }
            else -> transacciones
        }

        //filtro las transacciones segun el tipo de busqueda ("fecha" o "categoria")
        val filtrarTransacciones = when(buscarTipo) {
            "fecha" -> filtrarPorTipo.filter { it.fecha == buscarFecha.toString() }
            "categoria" -> filtrarPorTipo.filter { it.categoria.contains(buscarCategoria, ignoreCase = true) }
            else -> filtrarPorTipo
        }

        //actualizo la interfaz mostrando las transacciones filtradas
        _uiState.update { currentState ->
            currentState.copy(transaccionesFiltradas = filtrarTransacciones)
        }
    }

}

