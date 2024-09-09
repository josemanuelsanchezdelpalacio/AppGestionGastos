package com.dam2jms.appgestiongastos.models

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam2jms.appgestiongastos.data.ConversionMoneda
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

class CurrencyViewModel : ViewModel() {

    private val monedaConvertida = ConversionMoneda()

    private val _resultadoConversion = MutableStateFlow<Map<String, Double>>(emptyMap())
    val resultadoConversion: StateFlow<Map<String, Double>> = _resultadoConversion

    private val _monedasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val monedasDisponibles: StateFlow<List<String>> = _monedasDisponibles

    init {
        obtenerMonedasDisponibles()
    }

    /**metodo que obtiene las monedas para conversion y las almacena*/
    private fun obtenerMonedasDisponibles() {
        viewModelScope.launch {
            val tasas = monedaConvertida.obtenerTasasCambio("EUR")
            _monedasDisponibles.value = tasas.keys.toList()
        }
    }

    /**metodo que convierte una cantidad de monedas desde una moneda origen a otra moneda destino
     * @param cantidades un mapa con las monedas y las cantidades a convertir
     * @param monedaOrigen la moneda desde la cual se convierten
     * @param monedaDestino la moneda a la que se desea convertir
     * **/
    fun convertirMonedas(cantidades: Map<String, Double>, monedaOrigen: String, monedaDestino: String) {
        viewModelScope.launch {
            val resultado = cantidades.mapValues { (_, cantidad) ->
                monedaConvertida.convertirMoneda(cantidad, monedaOrigen, monedaDestino)
            }
            _resultadoConversion.value = resultado
        }
    }

    /**metodo que obtiene el simbolo de una moneda
     * @param codigoMoneda el codigo de la moneda
     * @return el simbolo de la moneda o el codigo si no se encuentra*/
    fun obtenerSimboloMoneda(codigoMoneda: String): String {
        return try{
            Currency.getInstance(codigoMoneda).symbol
        }catch (e: IllegalArgumentException) {
            codigoMoneda
        }
    }

    /**metodo que obtiene el nombre completo de las monedas
     * @param codigoMoneda el codigo de la moneda
     * @return el nombre completo de la moneda seguido del codigo*/

    fun obtenerNombreMoneda(codigoMoneda: String): String{
        return try{
            val moneda = Currency.getInstance(codigoMoneda)
            val nombre = moneda.getDisplayName(Locale.getDefault())
            "$nombre ($codigoMoneda)"
        }catch (e: IllegalArgumentException) {
            codigoMoneda
        }
    }

    /**metodo para obtener la tasa de cambio entre monedas
     * @param monedaOrigen moneda desde la que se convierte
     * @param monedaDestino moneda a la que se convierte
     * @return la tasa de cambio*/
    suspend fun obtenerTasaCambio(monedaOrigen: String, monedaDestino: String): Double {
        return try {
            val rates = monedaConvertida.obtenerTasasCambio(monedaOrigen)
            rates[monedaDestino] ?: 1.0  //devuelve 1.0 si la tasa no se encuentra
        } catch (e: Exception) {
            1.0  //devuelve 1.0 en caso de error
        }
    }
}

