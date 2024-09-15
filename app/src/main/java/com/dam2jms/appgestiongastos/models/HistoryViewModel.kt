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

    init {
        cargarTransacciones()
    }

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

    fun buscarTransacciones(buscarTipo: String, tipo: String, buscarFecha: LocalDate, buscarCategoria: String){

        val transacciones = _uiState.value.ingresos + _uiState.value.gastos

        val filtrarPorTipo = when(tipo) {
            "ingreso" -> transacciones.filter { it.tipo == "ingreso" }
            "gasto" -> transacciones.filter { it.tipo == "gasto" }
            else -> transacciones
        }

        val filtrarTransacciones = when(buscarTipo) {
            "fecha" -> filtrarPorTipo.filter { it.fecha == buscarFecha.toString() }
            "categoria" -> filtrarPorTipo.filter { it.categoria.contains(buscarCategoria, ignoreCase = true) }
            else -> filtrarPorTipo
        }

        _uiState.update { currentState ->
            currentState.copy(transaccionesFiltradas = filtrarTransacciones)
        }
    }

}

