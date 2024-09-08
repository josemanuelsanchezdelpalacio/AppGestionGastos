package com.dam2jms.appgestiongastos.data

import com.google.gson.annotations.SerializedName

data class ExchangeRateResponse(
    @SerializedName("conversion_rates") val conversionRates: Map<String, Double>,
    @SerializedName("base_code") val baseCode: String
)
