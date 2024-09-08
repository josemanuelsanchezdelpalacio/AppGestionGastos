package com.dam2jms.appgestiongastos.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateAPI {

    @GET("latest/{baseCurrency}")
    suspend fun obtenerTasasDeCambio(
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRateResponse

}