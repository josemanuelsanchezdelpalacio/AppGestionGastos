package com.dam2jms.appgestiongastos.data

data class TasasCambioResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>

)
