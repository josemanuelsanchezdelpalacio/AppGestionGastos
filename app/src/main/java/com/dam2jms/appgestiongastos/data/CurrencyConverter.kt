package com.dam2jms.appgestiongastos.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class CurrencyConverter {

    //clave para acceder al servicio de ExchangeRate
    private val apiKey = "d81f3898c35aad6e086bb265"

    //url para las solicitudes a la api
    private val baseURL = "https://v6.exchangerate-api.com/v6/$apiKey"

    /**metodo que obtiene las tasas de cambio para una moneda
     * @param url para la solicitud
     * @param respuesta lee la respuesta de la URL
     * @param jsonObject convierte la respuesta en un objeto JSON
     * @param tasas extrae del JSON las tasas de cambio
     * @return devuelve las tasas de cambio en un Map clave-valor
     * */
    suspend fun obtenerTasasCambio(moneda: String): Map<String, Double>{

        return withContext(Dispatchers.IO) {
            val url = URL("$baseURL/latest/$moneda")
            val respuesta = url.readText()
            val jsonObject = JSONObject(respuesta)
            val tasas = jsonObject.getJSONObject("tasas de cambio")
            tasas.keys().asSequence().associateWith { tasas.getDouble(it) }
        }
    }

    /**metodo que hace el cambio de una moneda a otra
     * @param tasas obtengo la tasas de cambio para la moneda original
     * @param tasa obtengo la tasa de cambio para la moneda de destino
     * @return devuelve la cantidad convertida */
    suspend fun convertirMoneda(cantidad: Double, monedaOrigen: String, monedaConvertida: String): Double{
        val tasas = obtenerTasasCambio(monedaOrigen)
        val tasa = tasas[monedaConvertida]?: throw Exception("Moneda no encontrada")
        return cantidad * tasa
    }
}


