package com.dam2jms.appgestiongastos.models

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam2jms.appgestiongastos.data.CurrencyConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Currency
import java.util.Locale

class CurrencyViewModel : ViewModel() {

    private val monedaConvertida = CurrencyConverter()

    //estado para almacenar el resultado de la conversion de monedas
    private val _resultadoConversion = MutableStateFlow<Map<String, Double>>(emptyMap())
    val resultadoConversion: StateFlow<Map<String, Double>> = _resultadoConversion

    //estado para almacenar la lista de monedas disponibles
    private val _monedasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val monedasDisponibles: StateFlow<List<String>> = _monedasDisponibles

    //inicianliza la lista de monedas
    init {
        obtenerMonedasDisponibles()
    }

    /**metodo para obtener la lista de monedas disponibles
     * @param tasas obtiene las tasas de cambio para el euro y actualiza de monedas disponibles
     * */
    private fun obtenerMonedasDisponibles() {
        viewModelScope.launch {
            try {
                val rates = monedaConvertida.obtenerTasasCambio("EUR")
                _monedasDisponibles.value = rates.keys.toList()
            } catch (e: Exception) { }
        }
    }

    /**metodo para convertir monedas entre diferentes tipos de cambio
     * @param resultado convierte cada cantidad con la tasa de cambio */
    fun convertirMonedas(cantidades: Map<String, Double>, fromCurrency: String, toCurrency: String) {
        viewModelScope.launch {
            try {
                val resultado = cantidades.mapValues { (tipo, cantidad) ->
                    monedaConvertida.convertirMoneda(cantidad, fromCurrency, toCurrency)
                }
                _resultadoConversion.value = resultado
            } catch (e: Exception) {}
        }
    }

    /** metodo para obtener el simbolo de las monedas*/
    fun obtenerSimboloMoneda(codigoMoneda: String): String {
        return try{
            Currency.getInstance(codigoMoneda).symbol
        }catch (e: IllegalArgumentException) {
            codigoMoneda
        }
    }

    /**metodo para obtener el nombre completo de cada moneda*/
    fun obtenerNombreCompleto(codigoMoneda: String): String{
        return try{
            val moneda = Currency.getInstance(codigoMoneda)
            val nombre = moneda.getDisplayName(Locale.getDefault())
            "$nombre ($codigoMoneda)"
        }catch (e: IllegalArgumentException) {
            codigoMoneda
        }
    }

    /** metodo para obtener la tasa de cambio entre dos monedas
     * @param tasas obtengo las tasas de cambio para la moneda de origen
     * @return la tasa de cambio entra la moneda origen y la moneda convertida*/
    suspend fun obtenerTasaCambio(fromCurrency: String, toCurrency: String): Double {
        return try {
            val rates = monedaConvertida.obtenerTasasCambio(fromCurrency)
            //devuelve 1.0 si la tasa no se encuentra
            rates[toCurrency] ?: 1.0
        } catch (e: Exception) { 1.0 } //devuelve 1.0 en caso de error
    }
}
