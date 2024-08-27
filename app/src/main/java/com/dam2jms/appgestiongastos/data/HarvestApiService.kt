package com.dam2jms.appgestiongastos.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface HarvestApiService {
    @GET("expense_categories")
    suspend fun getExpenseCategories(
        @Header("Authorization") authToken: String,
        @Header("Harvest-Account-Id") accountId: String
    ): Response<HarvestCategoriesResponse>
}


data class HarvestCategoriesResponse(
    val expense_categories: List<HarvestCategory>
)

data class HarvestCategory(
    val id: Long,
    val name: String
)
