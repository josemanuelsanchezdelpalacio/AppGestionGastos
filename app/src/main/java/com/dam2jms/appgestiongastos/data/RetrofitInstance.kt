package com.dam2jms.appgestiongastos.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: HarvestApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.harvestapp.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HarvestApiService::class.java)
    }
}
