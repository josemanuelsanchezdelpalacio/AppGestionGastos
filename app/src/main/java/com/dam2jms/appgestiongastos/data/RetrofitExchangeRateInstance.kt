package com.dam2jms.appgestiongastos.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitExchangeRateInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/v6/d81f3898c35aad6e086bb265/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ExchangeRateAPI by lazy {
        retrofit.create(ExchangeRateAPI::class.java)
    }

}