package com.dam2jms.appgestiongastos.data

import retrofit2.http.GET
import retrofit2.http.Path

interface TasasCambioService {

    /**
     * metodo para obtener las tasas de cambio para una moneda
     *
     * @param claveApi la clave de la API requerida para la solicitud
     * @param base la moneda desde la cual se va a convertir
     * @return respuesta con las tasas de cambio
     * **/

    @GET("latest/{base}")
    suspend fun obtenerTasas(@Path("base") base: String): TasasCambioResponse

}