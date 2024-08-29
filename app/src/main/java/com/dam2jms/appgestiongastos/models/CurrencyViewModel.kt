package com.dam2jms.appgestiongastos.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam2jms.appgestiongastos.data.CurrencyConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel: ViewModel() {

    private val currencyConverter = CurrencyConverter()

    private val _conversionResult = MutableStateFlow<Double?>(null)
    val conversionResult: StateFlow<Double?> = _conversionResult

    private val _availableCurrencies = MutableStateFlow<List<String>>(emptyList())
    val availableCurrencies: StateFlow<List<String>> = _availableCurrencies

    init {
        fetchAvailableCurrencies()
    }

    private fun fetchAvailableCurrencies(){
        viewModelScope.launch {
            try {
                val rates = currencyConverter.getExchangeRates("EUR")
                _availableCurrencies.value = rates.keys.toList()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String){

        viewModelScope.launch{
            try{
                val result = currencyConverter.convertCurrency(amount, fromCurrency, toCurrency)
                _conversionResult.value = result
            }catch (e: Exception){

            }
        }
    }
}