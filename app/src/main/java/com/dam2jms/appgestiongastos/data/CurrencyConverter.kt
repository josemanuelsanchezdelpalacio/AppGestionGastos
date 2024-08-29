package com.dam2jms.appgestiongastos.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class CurrencyConverter {

    private val apiKey = "d81f3898c35aad6e086bb265"
    private val baseURL = "https://v6.exchangerate-api.com/v6/$apiKey"

    suspend fun getExchangeRates(baseCurrency: String): Map<String, Double>{
        return withContext(Dispatchers.IO){
            val url = URL("$baseURL/latest/$baseCurrency")
            val response = url.readText()
            val jsonObject = JSONObject(response)
            val rates = jsonObject.getJSONObject("conversion_rates")
            rates.keys().asSequence().associateWith { rates.getDouble(it) }
        }
    }

    suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val rates = getExchangeRates(fromCurrency)
        val rate = rates[toCurrency] ?: throw Exception("Currency not found")
        return amount * rate
    }
}