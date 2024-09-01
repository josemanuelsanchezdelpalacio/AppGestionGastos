package com.dam2jms.appgestiongastos.models

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

    private val currencyConverter = CurrencyConverter()

    private val _conversionResult = MutableStateFlow<Map<String, Double>>(emptyMap())
    val conversionResult: StateFlow<Map<String, Double>> = _conversionResult

    private val _availableCurrencies = MutableStateFlow<List<String>>(emptyList())
    val availableCurrencies: StateFlow<List<String>> = _availableCurrencies

    init {
        fetchAvailableCurrencies()
    }

    private fun fetchAvailableCurrencies() {
        viewModelScope.launch {
            try {
                val rates = currencyConverter.getExchangeRates("EUR")
                _availableCurrencies.value = rates.keys.toList()
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    fun convertAllCurrencies(amounts: Map<String, Double>, fromCurrency: String, toCurrency: String) {
        viewModelScope.launch {
            try {
                val result = amounts.mapValues { (type, amount) ->
                    currencyConverter.convertCurrency(amount, fromCurrency, toCurrency)
                }
                _conversionResult.value = result
            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }

    fun getCurrencySymbol(currencyCode: String): String {
        return try{
            Currency.getInstance(currencyCode).symbol
        }catch (e: IllegalArgumentException) {
            currencyCode
        }
    }

    fun getCurrencyFullName(currencyCode: String): String{
        return try{
            val currency = Currency.getInstance(currencyCode)
            val displayName = currency.getDisplayName(Locale.getDefault())
            "$displayName ($currencyCode)"
        }catch (e: IllegalArgumentException) {
            currencyCode
        }
    }

    // Función para obtener la tasa de cambio entre dos monedas
    suspend fun obtenerTasaCambio(fromCurrency: String, toCurrency: String): Double {
        return try {
            val rates = currencyConverter.getExchangeRates(fromCurrency)
            rates[toCurrency] ?: 1.0  // Devuelve 1.0 si la tasa no se encuentra
        } catch (e: Exception) {
            1.0  // Devuelve 1.0 en caso de error
        }
    }
}
