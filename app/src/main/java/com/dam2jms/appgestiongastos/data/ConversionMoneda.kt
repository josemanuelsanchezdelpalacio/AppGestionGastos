package com.dam2jms.appgestiongastos.data

import androidx.compose.ui.geometry.times
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.time.times

class ConversionMoneda {

    //clave para acceder a la api de exchangeRate
    private val claveApi = "d81f3898c35aad6e086bb265"

    //url para las solicitudes a la api
    private val url = "https://v6.exchangerate-api.com/v6/$claveApi"

    private val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tasasCambioService = retrofit.create(TasasCambioService::class.java)

    /**
     * metodo para obtener las tasas de cambio para una moneda
     * @param monedaBase el codigo de la moneda base (ejemplo: "EUR")
     * @return un mapa con los codigos de monedas y sus tasas de cambio (mapa clave valor)
     * */
    suspend fun obtenerTasasCambio(monedaBase: String): Map<String, Double>{
        val respuesta = tasasCambioService.obtenerTasas(monedaBase)
        return respuesta.conversion_rates

    }

    /**
     * metodo para convertir de una moneda a otra
     * @param cantidad la cantidad a convertir
     * @param monedaOrigen la moneda desde cual se convierte
     * @param monedaDestino la moneda a la que se convierte
     * @return la cantidad convertida*/

    suspend fun convertirMoneda(cantidad: Double, monedaOrigen: String, monedaDestino: String): Double {
        val tasas = obtenerTasasCambio(monedaOrigen)
        val tasaDestino = tasas[monedaDestino] ?: 1.0
        return cantidad * tasaDestino
    }

}



